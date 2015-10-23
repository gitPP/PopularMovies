package com.mycompany.popluarmovies_i;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Priya on 10/17/15.
 */
public class SharedPreference {
    public SharedPreference() {
        super();
    }

    public void storeFavorites(Context context, List favorites) {
        // used for store arrayList in json format
        SharedPreferences sharedPref = context.getSharedPreferences("sortFavorite",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
        editor.putString("favorite", jsonFavorites);
        editor.commit();
    }
    public ArrayList loadFavorites(Context context) {
        // used for retrieving arraylist from json formatted string
        SharedPreferences settings;
        List favorites;
        settings = context.getSharedPreferences("sortFavorite",Context.MODE_PRIVATE);
        if (settings.contains("favorite")) {
            String jsonFavorites = settings.getString("favorite", null);
            Gson gson = new Gson();
            MoviesInfo[] favoriteItems = new MoviesInfo[1];
            favoriteItems = gson.fromJson(jsonFavorites,MoviesInfo[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList(favorites);
        } else
            return null;
        return (ArrayList) favorites;
    }
    public void addFavorite(Context context, MoviesInfo favMovie) {
        List favorites = loadFavorites(context);
        if (favorites == null)
            favorites = new ArrayList();
        favorites.add(favMovie);
        storeFavorites(context, favorites);
    }
    public void removeFavorite(Context context, MoviesInfo favMovie) {
        ArrayList favorites = loadFavorites(context);

        if (favorites != null) {
            Iterator<MoviesInfo> iterator = favorites.iterator();
            while (iterator.hasNext()){
                MoviesInfo moviesInfo = iterator.next();
                if((moviesInfo.id).equals(favMovie.id)){
                    iterator.remove();
                }
            }
            storeFavorites(context, favorites);
        }


    }
    public void removeAllFavorite(Context context) {
        ArrayList favorites = loadFavorites(context);
        for(int i = favorites.size()-1; i >= 0; i--){
            favorites.remove(favorites.get(i));
        }
        storeFavorites(context,favorites);
    }
}
