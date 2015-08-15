package com.up2up.popmovie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.up2up.popmovie.adapter.MovieAdapter;
import com.up2up.popmovie.async.FetchMovieTask;
import com.up2up.popmovie.model.Movie;

import java.util.ArrayList;

public class MovieFragment extends Fragment implements
        FetchMovieTask.FetchListener {

    public interface Callback {
        public void onItemSelected(long movieId);
    }

    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String STATE_MOVIE = "STATE_MOVIE";


    private MovieAdapter mAdapter;
    private String currentSortBy = "";
    private ArrayList<Movie> movieList;

    public MovieFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.grid_movie);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = (Movie) adapterView.getItemAtPosition(i);
                ((Callback) getActivity()).onItemSelected(movie.getId());
            }
        });
        if(savedInstanceState == null || !savedInstanceState.containsKey(STATE_MOVIE)) {
            Log.d(LOG_TAG,"onCreateView savedInstanceState = null");
            movieList = new ArrayList<Movie>();
        }
        else {
            Log.d(LOG_TAG,"onCreateView savedInstanceState not null and get stated movie");
            movieList = savedInstanceState.getParcelableArrayList(STATE_MOVIE);
        }
        mAdapter = new MovieAdapter(getActivity(),R.id.grid_movie,movieList);
        gridView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onSortOrderChanged() {
        fetchMovie();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(movieList!=null)
            outState.putParcelableArrayList(STATE_MOVIE, movieList);
        super.onSaveInstanceState(outState);
    }

    public void fetchMovie() {
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentSortBy = Utility.getPreferenceSortOrder(getActivity());

        FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity(),this);
        fetchMovieTask.execute(currentSortBy);

    }


    @Override
    public void onFetchComplete(ArrayList<Movie> movieList) {
        mAdapter.clear();

        this.movieList = movieList;

        if(movieList==null) {
            Toast.makeText(getActivity(),getString(R.string.fail_to_get_data),Toast.LENGTH_SHORT).show();
            return;
        }

        for(Movie movie : movieList) {
            mAdapter.add(movie);
        }
        mAdapter.notifyDataSetChanged();
    }

}
