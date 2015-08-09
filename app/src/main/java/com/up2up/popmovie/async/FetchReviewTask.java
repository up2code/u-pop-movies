package com.up2up.popmovie.async;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.up2up.popmovie.R;
import com.up2up.popmovie.model.Review;

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

public class FetchReviewTask extends AsyncTask<Long,Void,List<Review>> {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private FetchReviewListener mListener;
    private Context mContext;

    public interface FetchReviewListener {
        void onFetchReviewComplete(List<Review> movieList);
    }

    public FetchReviewTask(Context context, FetchReviewListener listener) {
        this.mListener = listener;
        this.mContext = context;
    }

    @Override
    protected List<Review> doInBackground(Long... params) {

        String reviewJsonStr;

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
                    .appendPath("reviews")
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
            reviewJsonStr = buffer.toString();return getReviewDataFromJson(reviewJsonStr);
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

    private List<Review> getReviewDataFromJson(String reviewJsonStr)  throws JSONException {

        final String OWM_LIST = "results";
        List<Review> reviewList = new ArrayList<Review>();

        JSONObject reviewJson = new JSONObject(reviewJsonStr);
        JSONArray reviewArray = reviewJson.getJSONArray(OWM_LIST);

        for(int i=0;i<reviewArray.length();i++) {
            Review review = new Review();
            JSONObject reviewRow = reviewArray.getJSONObject(i);
            review.extractJson(reviewRow);

            reviewList.add(review);
        }

        return reviewList;
    }

    @Override
    protected void onPostExecute(List<Review> reviewList) {
        super.onPostExecute(reviewList);
        mListener.onFetchReviewComplete(reviewList);
    }
}
