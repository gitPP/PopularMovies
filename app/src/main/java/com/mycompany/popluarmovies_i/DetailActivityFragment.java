package com.mycompany.popluarmovies_i;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
import java.util.Iterator;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    ArrayList<String> trailerKeyList,trailerNameList,reviewList;
    ArrayAdapter<String> trailerAdapter, reviewAdapter;
    ListView trailerListView,reviewListView;
    Bundle bundle;
    MoviesInfo moviesInfo;


    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if(savedInstanceState != null){

            bundle = savedInstanceState.getBundle("movie");

        }else{
            bundle = getArguments();

        }



        if(bundle != null){
            moviesInfo = bundle.getParcelable("moviesInfo");
            TextView originalTitleTextView = (TextView) rootView.findViewById(R.id.originalTitleTextView);
            originalTitleTextView.setText(moviesInfo.original_title);

            ImageView posterImageView =(ImageView) rootView.findViewById(R.id.posterImageView);
            Picasso.with(getActivity())
                    .load("http://image.tmdb.org/t/p/w185"+ moviesInfo.poster_path )
                    .into(posterImageView);

            TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.releaseDateTextView);
            releaseDateTextView.setText(moviesInfo.release_date);

            TextView voteAveTextView = (TextView) rootView.findViewById(R.id.voteAveTextView);
            voteAveTextView.setText(moviesInfo.vote_average);

            TextView overviewTextView = (TextView) rootView.findViewById(R.id.overviewTextView);
            overviewTextView.setText(moviesInfo.overview);

            FetchVideosReviews fetchVideosReviews = new FetchVideosReviews();
            fetchVideosReviews.execute(moviesInfo.id);

            trailerListView = (ListView) rootView.findViewById(R.id.trailerListView);
            trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String videoUrl = "https://www.youtube.com/watch?v=" + trailerKeyList.get(position);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));

                }
            });

            reviewListView = (ListView) rootView.findViewById(R.id.reviewListView);

            final Button favorite = (Button) rootView.findViewById(R.id.favorite);

            Boolean isfav = false;
            SharedPreference sharedPreference = new SharedPreference();
            ArrayList<MoviesInfo> favMovieList = sharedPreference.loadFavorites(getActivity());
            if(favMovieList != null){
                Iterator<MoviesInfo> iterator = favMovieList.iterator();
                while (iterator.hasNext()){
                    MoviesInfo favMoviesInfo = iterator.next();
                    if(favMoviesInfo.id.equals(moviesInfo.id)){
                        isfav = true;
                    }
                }
            }


            if(!isfav){
                favorite.setText("Mark as favorite");
            }
            else{
                favorite.setText("Unmark");
            }
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreference sharedPreference = new SharedPreference();
                    Bundle args = getArguments();
                    MoviesInfo info = args.getParcelable("moviesInfo");

                    if(favorite.getText().equals("Mark as favorite")) {
                        info.isFavorite = true;
                        favorite.setText("Unmark");
                        sharedPreference.addFavorite(getActivity(), info);
                        Log.v("FAV", info.original_title + " added to favorites");
                    }else{
                        info.isFavorite = false;
                        sharedPreference.removeFavorite(getActivity(),info);
                        favorite.setText("Mark as favorite");
                        Log.v("FAV", info.original_title + " removed from favorites");

                    }

                }
            });


            return rootView;

        }

        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("movie",bundle);
    }

    public class FetchVideosReviews extends AsyncTask<String, Void, Integer> {

        private  final String TAG = FetchVideosReviews.class.getSimpleName();
        @Override
        protected Integer doInBackground(String... params) {


            InputStream inputStream = null;

            HttpURLConnection urlConnection = null;
            Integer result = 0;

            try{
                final String BASE_URL =
                        "http://api.themoviedb.org/3/movie";
                final String PATH_ID = params[0];
                final String PATH_REVIEWS = "reviews";
                final String APPEND_PARAM = "append_to_response";
                final String APPEND = "videos,reviews";

                final String API_KEY_PARAM = "api_key";

                final String API_KEY = "22636d0fb604622b939192c2def017ba";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(PATH_ID)
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
                        .appendQueryParameter(APPEND_PARAM, APPEND)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(TAG, url.toString());

                urlConnection = (HttpURLConnection)url.openConnection();
                 /* for Get request */
                urlConnection.setRequestMethod("GET");
                int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
                if (statusCode == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String response = convertInputStreamToString(inputStream);
                    Log.v(TAG, response);
                    processTrailerReviewInfo(response);
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
            if (result == 1){
                trailerAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,trailerNameList);
                trailerListView.setAdapter(trailerAdapter);

                reviewAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,reviewList);
                reviewListView.setAdapter(reviewAdapter);
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

    private void processTrailerReviewInfo(String infoString) throws JSONException {


        trailerKeyList = new ArrayList<>();
        trailerNameList = new ArrayList<>();
        reviewList = new ArrayList<>();

        JSONObject movieJson = new JSONObject(infoString);
        JSONObject videoJson = movieJson.getJSONObject("videos");
        JSONArray resultVideoArray = videoJson.getJSONArray("results");

        for (int i=0; i < resultVideoArray.length(); i++){

            JSONObject trailer = resultVideoArray.getJSONObject(i);
            trailerKeyList.add(trailer.getString("key"));
            trailerNameList.add(trailer.getString("name"));

        }

        JSONObject reviewsJson = movieJson.getJSONObject("reviews");
        JSONArray resultReviewsArray = reviewsJson.getJSONArray("results");

        for (int i=0; i < resultReviewsArray.length(); i++){
            JSONObject review = resultReviewsArray.getJSONObject(i);
            reviewList.add(review.getString("author") + "\n" + review.getString("content"));

        }

    }
}
