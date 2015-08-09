package com.up2up.popmovie.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.up2up.popmovie.R;
import com.up2up.popmovie.data.MovieContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Movie {

    private long id;
    private String title;
    private String overview;
    private double voteAverage;
    private String releaseDate;
    private String posterPath;
    private int voteCount;
    private boolean hasVideo;
    private int runTime;
    private List<Trailer> trailers;
    private List<Review> reviews;

    final String OWM_LIST = "results";
    final String OWM_TITLE = "title";
    final String OWM_POSTER = "poster_path";
    final String OWM_OVERVIEW = "overview";
    final String OWM_ID = "id";
    final String OWM_RELEASE_DATE = "release_date";
    final String OWM_VOTE_AVERAGE = "vote_average";
    final String OWM_RUNTIME = "runtime";
    final String OWM_VOTE_COUNT = "vote_count";
    final String OWM_VIDEO = "video";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return "http://image.tmdb.org/t/p/w185/" + posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(boolean hasVideo) {
        this.hasVideo = hasVideo;
    }

    public int getRunTime() {
        return runTime;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getReleaseYear() {
        return this.getReleaseDate().subSequence(0,4).toString();
    }

    public String getVoteStr(Context context) {
        return String.format(context.getString(R.string.vote_average),this.getVoteAverage());
    }

    public String getRuntimeStr(Context context) {
        return String.format(context.getString(R.string.runtime),String.valueOf(this.getRunTime()));
    }

    public Movie extractJson(JSONObject movieRow) throws JSONException {
        this.setId(movieRow.getLong(OWM_ID));
        this.setTitle(movieRow.getString(OWM_TITLE));
        this.setOverview(movieRow.getString(OWM_OVERVIEW));
        this.setReleaseDate(movieRow.getString(OWM_RELEASE_DATE));
        this.setPosterPath(movieRow.getString(OWM_POSTER));
        this.setVoteAverage(movieRow.getDouble(OWM_VOTE_AVERAGE));

        return this;
    }

    public Movie extractJsonDetail(JSONObject movieRow) throws JSONException {
        this.setId(movieRow.getLong(OWM_ID));
        this.setTitle(movieRow.getString(OWM_TITLE));
        this.setOverview(movieRow.getString(OWM_OVERVIEW));
        this.setReleaseDate(movieRow.getString(OWM_RELEASE_DATE));
        this.setPosterPath(movieRow.getString(OWM_POSTER));
        this.setVoteAverage(movieRow.getDouble(OWM_VOTE_AVERAGE));
        this.setRunTime(movieRow.getInt(OWM_RUNTIME));
        this.setVoteCount(movieRow.getInt(OWM_VOTE_COUNT));
        this.setHasVideo(movieRow.getBoolean(OWM_VIDEO));

        return this;
    }

    public Movie extractFromCursor(Cursor c) {
        this.setId(c.getLong(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
        this.setTitle(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
        this.setOverview(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
        this.setReleaseDate(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
        this.setPosterPath(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
        this.setVoteAverage(c.getDouble(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
        this.setRunTime(c.getInt(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_RUNTIME)));
        this.setVoteCount(c.getInt(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT)));
        return this;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, this.getId());
        values.put(MovieContract.MovieEntry.COLUMN_TITLE,this.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,this.getPosterPath());
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,this.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, this.getReleaseDate());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,this.getVoteAverage());
        values.put(MovieContract.MovieEntry.COLUMN_RUNTIME, this.getRunTime());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT,this.getVoteCount());
        return values;
    }
}
