package com.up2up.popmovie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.up2up.popmovie.R;
import com.up2up.popmovie.model.Movie;

import java.util.ArrayList;


public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Context context, int resource, ArrayList<Movie> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.grid_item_movie, null);

        Movie movie = getItem(position);

        ImageView imgPoster = (ImageView) convertView.findViewById(R.id.grid_item_poster);


        Picasso.with(getContext()).load(movie.getPosterPath()).into(imgPoster);


        return convertView;
    }
}
