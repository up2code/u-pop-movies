package com.up2up.popmovie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.up2up.popmovie.adapter.MovieAdapter;
import com.up2up.popmovie.async.FetchMovieTask;
import com.up2up.popmovie.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieFragment extends Fragment implements
        FetchMovieTask.FetchListener {

    public interface Callback {
        public void onItemSelected(long movieId);
    }

    private final String LOG_TAG = this.getClass().getSimpleName();


    MovieAdapter mAdapter;

    public MovieFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.grid_movie);
        mAdapter = new MovieAdapter(getActivity(),R.id.grid_movie,new ArrayList<Movie>());
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = (Movie) adapterView.getItemAtPosition(i);
                ((Callback) getActivity()).onItemSelected(movie.getId());
            }
        });

        return rootView;
    }

    public void onSortOrderChanged() {
        fetchMovie();
    }

    @Override
    public void onResume() {
        super.onResume();

        fetchMovie();
    }

    public void fetchMovie() {
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = sh.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

        FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity(),this);
        fetchMovieTask.execute(sortBy);

    }


    @Override
    public void onFetchComplete(List<Movie> movieList) {
        mAdapter.clear();

        if(movieList==null)
            return;

        for(Movie movie : movieList) {
            mAdapter.add(movie);
        }
        mAdapter.notifyDataSetChanged();
    }

}
