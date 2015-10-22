package com.mycompany.popluarmovies_i;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    List<MoviesInfo> moviesInfoList = null;
    ImageAdapter imageAdapter = null;
    Context context;
    GridView moviesGridView;
    //String sortType;

    public interface Callback {

        public void onItemSelected (Bundle bundle);
    }



    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {

            moviesInfoList = new ArrayList<MoviesInfo>();

        } else {

            moviesInfoList = savedInstanceState.getParcelableArrayList("movies");

        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_general, false);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        moviesGridView = (GridView) rootView.findViewById(R.id.moviesGridView);
        imageAdapter = new ImageAdapter(getActivity(), moviesInfoList);
        moviesGridView.setAdapter(imageAdapter);

        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MoviesInfo moviesInfo = moviesInfoList.get(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("moviesInfo",moviesInfo);
                ((Callback)getActivity()).onItemSelected(bundle);

            }
        });

        return rootView;
    }



    @Override
    public void onStart() {
        super.onStart();
        fetchMovies();


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", (ArrayList<? extends Parcelable>) moviesInfoList);

    }

    private void fetchMovies() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = prefs.getString("sort", "popularity.desc");
        Log.v("sorttype", sortType);
        //if((sortType == "popularity.desc")|| (sortType == "vote_average.desc") ){
        if(!(sortType.equals("favorites")) ){
            Log.v("Inside fetchMovies","Inside fetchMovies");
            FetchMovies fetchMovies = new FetchMovies();
            fetchMovies.execute(sortType);
        }else{
            Log.v("Loading from sharedpref","Loading from sharedpref");
            SharedPreference sharedPreference = new SharedPreference();
            moviesInfoList = sharedPreference.loadFavorites(getActivity());
            //sharedPreference.removeAllFavorite(getActivity());
            imageAdapter = new ImageAdapter(getActivity(), moviesInfoList);
            moviesGridView.setAdapter(imageAdapter);
        }



    }

    public class FetchMovies extends AsyncTask<String, Void, Integer> {

        private final String TAG = FetchMovies.class.getSimpleName();

        @Override
        protected Integer doInBackground(String... params) {
            InputStream inputStream = null;

            HttpURLConnection urlConnection = null;
            Integer result = 0;

            try {
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
                Log.v(TAG, url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                 /* for Get request */
                urlConnection.setRequestMethod("GET");
                int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
                if (statusCode == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String response = convertInputStreamToString(inputStream);
                    Log.v(TAG, response);
                    processMoviesInfo(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }


            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if (result == 1) {
                imageAdapter = new ImageAdapter(getActivity(), moviesInfoList);
                moviesGridView.setAdapter(imageAdapter);


            }
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        StringBuffer buffer = new StringBuffer();
        while ((line = bufferedReader.readLine()) != null) {
            //result += line;

            buffer.append(line + "\n");
        }
        result = buffer.toString();

            /* Close Stream */
        if (null != inputStream) {
            inputStream.close();
        }
        return result;
    }

    private void processMoviesInfo(String infoString) throws JSONException {


        moviesInfoList = new ArrayList<MoviesInfo>();

        JSONObject movieJson = new JSONObject(infoString);
        JSONArray resultArray = movieJson.getJSONArray("results");

        for (int i = 0; i < resultArray.length(); i++) {

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
            moviesInfo.isFavorite = false;
            moviesInfoList.add(moviesInfo);
        }

    }
}
