package com.mycompany.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {



    List<MoviesInfo> moviesInfoList = null;
    ImageAdapter imageAdapter = null;
    Context context;
    GridView moviesGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        moviesGridView = (GridView) findViewById(R.id.moviesGridView);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {

            fetchMovies();
        }else{
            moviesInfoList = savedInstanceState.getParcelableArrayList("movies");
            imageAdapter = new ImageAdapter(getApplicationContext(), moviesInfoList);
            moviesGridView.setAdapter(imageAdapter);
        }
        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MoviesInfo moviesInfo = moviesInfoList.get(position);

                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("id", moviesInfo.id);
                intent.putExtra("poster_path", moviesInfo.poster_path);
                intent.putExtra("backdrop_path", moviesInfo.backdrop_path);
                intent.putExtra("original_title", moviesInfo.original_title);
                intent.putExtra("overview", moviesInfo.overview);
                intent.putExtra("vote_average", moviesInfo.vote_average);
                intent.putExtra("release_date", moviesInfo.release_date);


                startActivity(intent);

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

       fetchMovies();

    }

    private void fetchMovies(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortType = prefs.getString("sort","popularity.desc");

        FetchMovies fetchMovies = new FetchMovies();
        fetchMovies.execute(sortType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", (ArrayList<? extends Parcelable>) moviesInfoList);
    }

    public class FetchMovies extends AsyncTask<String, Void, Integer>{

        private  final String TAG = FetchMovies.class.getSimpleName();

        @Override
        protected Integer doInBackground(String... params) {
            InputStream inputStream = null;

            HttpURLConnection urlConnection = null;
            Integer result = 0;

            try{
                final String BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                final String API_KEY = "22636d0fb604622b939192c2def017ba";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(TAG,url.toString());

                urlConnection = (HttpURLConnection)url.openConnection();
                 /* for Get request */
                urlConnection.setRequestMethod("GET");
                int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
                if (statusCode == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String response = convertInputStreamToString(inputStream);
                    Log.v(TAG,response);
                    processMoviesInfo(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }


            }catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if (result == 1 ) {
                imageAdapter = new ImageAdapter(getApplicationContext(), moviesInfoList);
                moviesGridView.setAdapter(imageAdapter);



            }
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        StringBuffer buffer = new StringBuffer();
        while((line = bufferedReader.readLine()) != null){
            //result += line;

            buffer.append(line + "\n");
        }
        result = buffer.toString();

            /* Close Stream */
        if(null!=inputStream){
            inputStream.close();
        }
        return result;
    }

    private void processMoviesInfo(String infoString) throws JSONException {


        moviesInfoList = new ArrayList<MoviesInfo>();

        JSONObject movieJson = new JSONObject(infoString);
        JSONArray resultArray = movieJson.getJSONArray("results");

        for (int i=0; i < resultArray.length(); i++){

            // Get the JSON object representing the movie
            JSONObject movie = resultArray.getJSONObject(i);
            MoviesInfo moviesInfo = new MoviesInfo();
            moviesInfo.id = movie.getString("id");
            moviesInfo.poster_path = movie.getString("poster_path");
            moviesInfo.backdrop_path = movie.getString("backdrop_path");
            moviesInfo.original_title = movie.getString("original_title");
            moviesInfo.overview = movie.getString("overview");
            moviesInfo.release_date = movie.getString("release_date");
            moviesInfo.vote_average = movie.getString("vote_average");
            moviesInfoList.add(moviesInfo);
        }

    }
}
