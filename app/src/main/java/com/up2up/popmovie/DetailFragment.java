package com.up2up.popmovie;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.up2up.popmovie.adapter.ReviewAdapter;
import com.up2up.popmovie.adapter.TrailerAdapter;
import com.up2up.popmovie.async.FetchMovieDetailTask;
import com.up2up.popmovie.async.FetchReviewTask;
import com.up2up.popmovie.async.FetchTrailerTask;
import com.up2up.popmovie.data.MovieContract;
import com.up2up.popmovie.model.Movie;
import com.up2up.popmovie.model.Review;
import com.up2up.popmovie.model.Trailer;

import java.util.ArrayList;
import java.util.List;


public class DetailFragment extends Fragment implements FetchMovieDetailTask.FetchMovieDetailListener,
        FetchTrailerTask.FetchTrailerListener,
        FetchReviewTask.FetchReviewListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = this.getClass().getSimpleName();


    public static final int MOVIE_LOADER = 100;
    public static final int TRAILER_LOADER = 200;
    public static final int REVIEW_LOADER = 300;

    public static final String MOVIE_ID ="movie_id";

    private LinearLayout    mContainer;
    private TextView txtReview;
    private TextView txtReleaseYear;
    private TextView txtLength;
    private TextView txtRate;
    private Button btnFavorite;
    private TextView txtTitle;
    private TextView txtOverview;
    private ListView listTrailer;
    private ListView listReview;
    private ImageView imgPoster;

    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;

    private boolean mIsFavorite;
    private Movie mMovie;
    private List<Review> mReview;
    private List<Trailer> mTrailer;
    private ShareActionProvider mShareActionProvider;
    private long movieId;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        initParam();


        newView(rootView);


        return rootView;
    }

    private void initParam() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            movieId = arguments.getLong(DetailFragment.MOVIE_ID);

        }

        Bundle bundle = getActivity().getIntent().getExtras();

        if(bundle != null) {
            movieId = bundle.getLong(MOVIE_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initParam();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        this.setShareIntent();

    }

    private Intent createShareMovieIntent(String trailerUrl) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, trailerUrl);

        return shareIntent;
    }

    public void fetchMovieDetail(long movieId) {
        FetchMovieDetailTask task = new FetchMovieDetailTask(getActivity(),this);
        task.execute(movieId);

        FetchReviewTask reviewTask = new FetchReviewTask(getActivity(), this);
        reviewTask.execute(movieId);

        FetchTrailerTask trailerTask = new FetchTrailerTask(getActivity(), this);
        trailerTask.execute(movieId);
    }

    private void newView(View view) {
        mContainer = (LinearLayout) view.findViewById(R.id.movie_layout_container);
        imgPoster = (ImageView) view.findViewById(R.id.movie_poster);
        txtTitle = (TextView) view.findViewById(R.id.movie_title);
        txtReleaseYear = (TextView) view.findViewById(R.id.movie_release_year);
        txtLength = (TextView) view.findViewById(R.id.movie_length);
        txtRate = (TextView) view.findViewById(R.id.movie_rate);
        btnFavorite = (Button) view.findViewById(R.id.movie_favorite);
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markFavorite();
            }
        });
        txtOverview = (TextView) view.findViewById(R.id.movie_overview);
        txtReview = (TextView) view.findViewById(R.id.movie_review_txt);

        listTrailer = (ListView) view.findViewById(R.id.list_movie_trailers);
        mTrailerAdapter = new TrailerAdapter(getActivity(), R.layout.list_item_trailer, Trailer.EMPTY);
        listTrailer.setAdapter(mTrailerAdapter);
        listTrailer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Trailer trailer = (Trailer) adapterView.getItemAtPosition(i);

                Intent videoClient = new Intent(Intent.ACTION_VIEW);
                videoClient.setData(trailer.getTrailerUri());
                startActivity(videoClient);
            }
        });
        listTrailer.setFocusable(false);

        listReview = (ListView) view.findViewById(R.id.list_movie_reviews);
        mReviewAdapter = new ReviewAdapter(getActivity(), R.layout.list_item_review, Review.EMPTY);
        listReview.setAdapter(mReviewAdapter);
        listReview.setFocusable(false);
        mContainer.setVisibility(View.VISIBLE);

        checkBtnFavorite();
    }

    private void checkBtnFavorite() {
        if(mIsFavorite) {
            btnFavorite.setText(R.string.favorited);
            btnFavorite.setBackgroundResource(R.color.favorite);
        } else {
            btnFavorite.setText(getString(R.string.mark_as_favorite));
            btnFavorite.setBackgroundResource(R.color.non_favorite);
        }

    }


    private void markFavorite() {
        if(mIsFavorite) {
            //Remove favorite movie
            removeFavorite();
            mIsFavorite = false;
        } else {
            //Mark favorite
            addFavorite();
            mIsFavorite = true;

        }

        checkBtnFavorite();
    }

    private void removeFavorite() {
        Thread removeBackground = new Thread(new Runnable() {

            @Override
            public void run() {

            if(mMovie!=null) {
                getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(mMovie.getId())});

                getActivity().getContentResolver().delete(MovieContract.ReviewEntry.CONTENT_URI,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ?",
                        new String[]{String.valueOf(mMovie.getId())});

                getActivity().getContentResolver().delete(MovieContract.TrailerEntry.CONTENT_URI,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ?",
                        new String[]{String.valueOf(mMovie.getId())});



                Log.d(LOG_TAG,"Favorite movie "+mMovie.getId()+" removed");
            }

            }
        });
        removeBackground.run();
    }

    private void addFavorite() {
        Thread addBackground = new Thread(new Runnable() {

            @Override
            public void run() {

                if(mMovie!=null) {
                    getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, mMovie.getContentValues());
                    Log.d(LOG_TAG, "Favorite movie " + mMovie.getId() + " added");
                }


                if(mTrailer!=null && mTrailer.size()>0) {
                    Log.d(LOG_TAG, "Add " + mTrailer.size() + " trailer(s)");
                    for(Trailer trailer : mTrailer) {
                        getActivity().getContentResolver().insert(MovieContract.TrailerEntry.CONTENT_URI, trailer.getContentValues(mMovie.getId()));
                    }
                }


                if(mReview!=null && mReview.size()>0) {
                    Log.d(LOG_TAG, "Add " + mReview.size() + " review(s)");
                    for(Review review : mReview) {
                        getActivity().getContentResolver().insert(MovieContract.ReviewEntry.CONTENT_URI, review.getContentValues(mMovie.getId()));
                    }
                }


            }
        });
        addBackground.run();
    }

    private void bindView(Movie movie) {
        txtTitle.setText(movie.getTitle());
        txtReleaseYear.setText(movie.getReleaseYear());
        txtLength.setText(String.valueOf(movie.getRuntimeStr(getActivity())));
        txtRate.setText(String.valueOf(movie.getVoteStr(getActivity())));
        txtOverview.setText(movie.getOverview());
        Picasso.with(getActivity()).load(movie.getPosterPath()).into(imgPoster);
    }

    private void clearView() {
        txtTitle.setText("");
        txtReleaseYear.setText("");
        txtLength.setText("");
        txtRate.setText("");
        txtOverview.setText("");
    }

    private void setShareIntent() {
        Log.d(LOG_TAG,"setShareIntent()");
        if (mShareActionProvider != null && mTrailer!=null) {
            if(mTrailer.size()>0) {
                mShareActionProvider.setShareIntent(createShareMovieIntent(mTrailer.get(0).getTrailerUri().toString()));
            }
            else {
                Log.d(LOG_TAG,"ShareIntent not ready...");
                //Disable shareAction when got non-trailer movie
                mShareActionProvider.setShareIntent(new Intent());
            }
        }
    }

    @Override
    public void onFetchMovieDetailComplete(Movie movie) {
        if(movie!=null) {
            mMovie = movie;
            bindView(movie);
        }
        else
        {
            Toast.makeText(getActivity(), getString(R.string.fail_to_get_data), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFetchReviewComplete(List<Review> reviewList) {
        Log.d(LOG_TAG,"onFetchReviewComplete()");
        mReview = reviewList;
        if(reviewList!=null) {
            mReviewAdapter.clear();

            if(reviewList.size()==0) {
                txtReview.setText(getString(R.string.no_review));
            }

            for(Review review : reviewList) {
                mReviewAdapter.add(review);
            }

        }
    }

    @Override
    public void onFetchTrailerComplete(List<Trailer> trailerList) {
        Log.d(LOG_TAG,"onFetchTrailerComplete()");
        mTrailer = trailerList;
        if(trailerList!=null) {
            mTrailerAdapter.clear();

            for(Trailer trailer : trailerList) {
                mTrailerAdapter.add(trailer);
            }

            this.setShareIntent();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG,"onCreateLoader loader id = "+id);

        switch (id) {
            case MOVIE_LOADER : {
                return new CursorLoader(getActivity(),MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(movieId)},
                        null);
            }

            case TRAILER_LOADER : {
                return new CursorLoader(getActivity(),MovieContract.TrailerEntry.CONTENT_URI,
                        null,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ?",
                        new String[]{String.valueOf(movieId)},
                        null);
            }

            case REVIEW_LOADER : {
                return new CursorLoader(getActivity(),MovieContract.ReviewEntry.CONTENT_URI,
                        null,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ?",
                        new String[]{String.valueOf(movieId)},
                        null);
            }

            default :
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case MOVIE_LOADER : {

                if(data.moveToFirst()) {
                    Log.d(LOG_TAG,"Fetch from db");
                    mIsFavorite = true;
                    mMovie = new Movie();
                    mMovie.extractFromCursor(data);
                    bindView(mMovie);

                    getLoaderManager().initLoader(TRAILER_LOADER,null,this);
                    getLoaderManager().initLoader(REVIEW_LOADER,null,this);
                } else {
                    Log.d(LOG_TAG,"Fetch from api");
                    mIsFavorite = false;
                    fetchMovieDetail(movieId);
                }

                checkBtnFavorite();
                break;
            }

            case TRAILER_LOADER : {

                mTrailerAdapter.clear();
                mTrailer = new ArrayList<Trailer>();
                while(data.moveToNext()) {
                    Trailer trailer = new Trailer();
                    trailer.extractFromCursor(data);
                    mTrailer.add(trailer);
                    mTrailerAdapter.add(trailer);
                }
                Log.d(LOG_TAG,"Found "+mTrailer.size()+" trailer(s)");

                this.setShareIntent();

                break;
            }

            case REVIEW_LOADER : {

                mReviewAdapter.clear();
                mReview = new ArrayList<Review>();
                while(data.moveToNext()) {
                    Review review = new Review();
                    review.extractFromCursor(data);
                    mReview.add(review);
                    mReviewAdapter.add(review);
                }

                if(mReview.size()==0) {
                    txtReview.setText(getString(R.string.no_review));
                }

                Log.d(LOG_TAG,"Found "+mReview.size()+" review(s)");
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case MOVIE_LOADER : {
                clearView();
                break;
            }

            case TRAILER_LOADER : {
                mTrailerAdapter.clear();
                break;
            }

            case REVIEW_LOADER : {
                mReviewAdapter.clear();
                break;
            }
        }
    }
}
