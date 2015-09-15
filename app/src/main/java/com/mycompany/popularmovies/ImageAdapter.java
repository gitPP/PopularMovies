package com.mycompany.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Priya on 9/1/15.
 */
public class ImageAdapter extends ArrayAdapter<MoviesInfo> {

    private  Context context;
    private List<MoviesInfo> moviesInfoList;

    public ImageAdapter(Context context,List<MoviesInfo> moviesInfoList) {

        super(context, R.layout.activity_main, moviesInfoList);

        this.context = context;
        this.moviesInfoList = moviesInfoList;
    }

    @Override
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            //imageView.setLayoutParams(new GridView.LayoutParams(80, 80));
            //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //imageView.setAdjustViewBounds(true);

            //imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w185"+ moviesInfoList.get(position).poster_path)
                .into(imageView);
        return imageView;
    }



}
