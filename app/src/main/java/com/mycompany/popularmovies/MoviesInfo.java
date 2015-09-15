package com.mycompany.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Priya on 9/10/15.
 */
public class MoviesInfo implements Parcelable{
    String id;
    String backdrop_path;
    String original_title;
    String poster_path;
    String overview;
    String vote_average;
    String release_date;

    public  MoviesInfo(){

    }

    private MoviesInfo(Parcel in){
        id = in.readString();
        backdrop_path = in.readString();
        original_title = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        vote_average = in.readString();
        release_date = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return id + "--" + backdrop_path + "--" + original_title + "--" + poster_path
            + "--" + overview + "--" + vote_average + "--" + release_date; }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(backdrop_path);
        parcel.writeString(original_title);
        parcel.writeString(poster_path);
        parcel.writeString(overview);
        parcel.writeString(vote_average);
        parcel.writeString(release_date);

    }

    public final Parcelable.Creator<MoviesInfo> CREATOR = new Parcelable.Creator<MoviesInfo>(){
        @Override
        public MoviesInfo createFromParcel(Parcel parcel) {
            return new MoviesInfo(parcel);
        }

        @Override
        public MoviesInfo[] newArray(int i){
            return new MoviesInfo[i];
        }

    };
}
