package com.up2up.popmovie.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.up2up.popmovie.data.MovieContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Review {
    private String id;
    private String author;
    private String content;
    private String url;

    final String OWM_ID = "id";
    final String OWM_AUTHOR = "author";
    final String OWM_CONTENT = "content";
    final String OWM_URL = "url";

    public static ArrayList<Review> EMPTY = new ArrayList<Review>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void extractJson(JSONObject trailerJson) throws JSONException {
        this.setId(trailerJson.getString(OWM_ID));
        this.setAuthor(trailerJson.getString(OWM_AUTHOR));
        this.setContent(trailerJson.getString(OWM_CONTENT));
        this.setUrl(trailerJson.getString(OWM_URL));
    }

    public Review extractFromCursor(Cursor c) {
        this.setAuthor(c.getString(c.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR)));
        this.setContent(c.getString(c.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT)));
        this.setUrl(c.getString(c.getColumnIndex(MovieContract.ReviewEntry.COLUMN_URL)));
        return this;
    }

    public ContentValues getContentValues(long movieId) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY,movieId);
        values.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID,this.getId());
        values.put(MovieContract.ReviewEntry.COLUMN_AUTHOR,this.getAuthor());
        values.put(MovieContract.ReviewEntry.COLUMN_CONTENT,this.getContent());
        values.put(MovieContract.ReviewEntry.COLUMN_URL,this.getUrl());
        return values;
    }
}
