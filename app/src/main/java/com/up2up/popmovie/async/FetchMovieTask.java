package com.up2up.popmovie.async;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.up2up.popmovie.R;
import com.up2up.popmovie.data.MovieContract;
import com.up2up.popmovie.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchMovieTask extends AsyncTask<String,Void,List<Movie>> {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private FetchListener mListener;
    private Context mContext;

    public interface FetchListener {
        void onFetchComplete(List<Movie> movieList);
    }

    public FetchMovieTask(Context context, FetchListener listener) {
        this.mListener = listener;
        this.mContext = context;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        if(params[0].equals(mContext.getString(R.string.pref_sort_favorite))) {
            return fetchFavorite();
        } else {
            return fetchFromApi(params[0]);
        }
    }

    private List<Movie> fetchFromApi(String sortBy) {
        String movieJsonStr;

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.

        try {
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM,sortBy)
                    .appendQueryParameter(API_KEY_PARAM, mContext.getString(R.string.api_key))
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG,"Fetch url = "+ builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();
            return getMovieDataFromJson(movieJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    private List<Movie> fetchFavorite() {
        List<Movie> mList = new ArrayList<Movie>();
        Cursor c = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null,null,null,null);
        while(c.moveToNext()) {
            mList.add(new Movie().extractFromCursor(c));
        }
        c.close();
        return mList;
    }



    private List<Movie> getMovieDataFromJson(String movieJsonStr)  throws JSONException {

        final String OWM_LIST = "results";

        List<Movie> movieList = new ArrayList<Movie>();

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(OWM_LIST);

        for(int i=0;i<movieArray.length();i++) {
            Movie movie = new Movie();
            JSONObject movieRow = movieArray.getJSONObject(i);

            movie.extractJson(movieRow);

            movieList.add(movie);
        }

        return movieList;
    }

    @Override
    protected void onPostExecute(List<Movie> movieList) {
        super.onPostExecute(movieList);
        mListener.onFetchComplete(movieList);
    }
}
