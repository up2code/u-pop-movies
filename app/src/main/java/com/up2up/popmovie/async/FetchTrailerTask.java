package com.up2up.popmovie.async;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.up2up.popmovie.R;
import com.up2up.popmovie.model.Trailer;

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

public class FetchTrailerTask extends AsyncTask<Long, Void, List<Trailer>> {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private FetchTrailerListener mListener;
    private Context mContext;

    public interface FetchTrailerListener {
        void onFetchTrailerComplete(List<Trailer> movieList);
    }

    public FetchTrailerTask(Context context, FetchTrailerListener listener) {
        this.mListener = listener;
        this.mContext = context;
    }

    @Override
    protected List<Trailer> doInBackground(Long... params) {

        String trailerJsonStr;

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.

        try {
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(String.valueOf(params[0]))
                    .appendPath("videos")
                    .appendQueryParameter(API_KEY_PARAM, mContext.getString(R.string.api_key))
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG, "Fetch url = " + builtUri.toString());

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
            trailerJsonStr = buffer.toString();
            return getTrailerDataFromJson(trailerJsonStr);
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

    private List<Trailer> getTrailerDataFromJson(String trailerJsonStr) throws JSONException {

        final String OWM_LIST = "results";
        List<Trailer> trailerList = new ArrayList<Trailer>();

        JSONObject trailerJson = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray(OWM_LIST);

        for (int i = 0; i < trailerArray.length(); i++) {
            Trailer trailer = new Trailer();
            JSONObject trailerRow = trailerArray.getJSONObject(i);
            trailer.extractJson(trailerRow);

            trailerList.add(trailer);
        }

        return trailerList;
    }

    @Override
    protected void onPostExecute(List<Trailer> trailerList) {
        super.onPostExecute(trailerList);
        mListener.onFetchTrailerComplete(trailerList);
    }
}
