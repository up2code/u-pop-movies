package com.up2up.popmovie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAIL_FRAGMENT_TAG = "DFTAG";
    private static final String STATE_SORT = "STATE_SORT";

    private String mSortOrder;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "mSortOrder onCreate = " + mSortOrder);
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }
        else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        if(savedInstanceState!=null && savedInstanceState.containsKey(STATE_SORT)) {
            mSortOrder = savedInstanceState.getString(STATE_SORT);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortOrder = Utility.getPreferenceSortOrder(this);

        if (sortOrder != null && !sortOrder.equals(mSortOrder)) {
            MovieFragment ff = (MovieFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movie);
            if ( null != ff ) {
                ff.onSortOrderChanged();
            }

            mSortOrder = sortOrder;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mSortOrder!=null)
            outState.putString(STATE_SORT,mSortOrder);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(long movieId) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putLong(DetailFragment.MOVIE_ID, movieId);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailFragment.MOVIE_ID,movieId);
            startActivity(intent);
        }
    }
}
