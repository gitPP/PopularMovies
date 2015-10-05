package com.mycompany.popluarmovies_i;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView originalTitleTextView = (TextView) rootView.findViewById(R.id.originalTitleTextView);
        originalTitleTextView.setText(getActivity().getIntent().getStringExtra("original_title"));

        ImageView posterImageView =(ImageView) rootView.findViewById(R.id.posterImageView);
        Picasso.with(getActivity())
                .load("http://image.tmdb.org/t/p/w185"+getActivity().getIntent().getStringExtra("poster_path") )
                .into(posterImageView);

        TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.releaseDateTextView);
        releaseDateTextView.setText(getActivity().getIntent().getStringExtra("release_date"));

        TextView voteAveTextView = (TextView) rootView.findViewById(R.id.voteAveTextView);
        voteAveTextView.setText(getActivity().getIntent().getStringExtra("vote_average"));

        TextView overviewTextView = (TextView) rootView.findViewById(R.id.overviewTextView);
        overviewTextView.setText(getActivity().getIntent().getStringExtra("overview"));
        return rootView;
    }
}
