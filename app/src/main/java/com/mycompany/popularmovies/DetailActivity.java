package com.mycompany.popularmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        TextView originalTitleTextView = (TextView) findViewById(R.id.originalTitleTextView);
        originalTitleTextView.setText(getIntent().getStringExtra("original_title"));

        ImageView posterImageView =(ImageView) findViewById(R.id.posterImageView);
        Picasso.with(getApplicationContext())
                .load("http://image.tmdb.org/t/p/w185"+getIntent().getStringExtra("poster_path") )
                .into(posterImageView);

        TextView releaseDateTextView = (TextView) findViewById(R.id.releaseDateTextView);
        releaseDateTextView.setText(getIntent().getStringExtra("release_date"));

        TextView voteAveTextView = (TextView) findViewById(R.id.voteAveTextView);
        voteAveTextView.setText(getIntent().getStringExtra("vote_average"));

        TextView overviewTextView = (TextView) findViewById(R.id.overviewTextView);
        overviewTextView.setText(getIntent().getStringExtra("overview"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
