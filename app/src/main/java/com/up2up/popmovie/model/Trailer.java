package com.up2up.popmovie.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.up2up.popmovie.data.MovieContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Trailer {
    private String id;
    private String name;
    private String site;
    private String type;
    private String key;

    final String OWM_ID = "id";
    final String OWM_NAME = "name";
    final String OWM_SITE = "site";
    final String OWM_TYPE = "type";
    final String OWM_KEY = "key";

    public static ArrayList<Trailer> EMPTY = new ArrayList<Trailer>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Uri getTrailerUri() {
        Uri builtUri = Uri.parse("https://www.youtube.com/watch?").buildUpon()
                .appendQueryParameter("v", getKey())
                .build();
        return builtUri;
    }

    public void extractJson(JSONObject trailerJson) throws JSONException {
        this.setId(trailerJson.getString(OWM_ID));
        this.setName(trailerJson.getString(OWM_NAME));
        this.setSite(trailerJson.getString(OWM_SITE));
        this.setType(trailerJson.getString(OWM_TYPE));
        this.setKey(trailerJson.getString(OWM_KEY));
    }

    public Trailer extractFromCursor(Cursor c) {
        this.setName(c.getString(c.getColumnIndex(MovieContract.TrailerEntry.COLUMN_NAME)));
        this.setSite(c.getString(c.getColumnIndex(MovieContract.TrailerEntry.COLUMN_SITE)));
        this.setType(c.getString(c.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TYPE)));
        this.setKey(c.getString(c.getColumnIndex(MovieContract.TrailerEntry.COLUMN_KEY)));
        return this;
    }

    public ContentValues getContentValues(long movieId) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY,movieId);
        values.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID,this.getId());
        values.put(MovieContract.TrailerEntry.COLUMN_NAME,this.getName());
        values.put(MovieContract.TrailerEntry.COLUMN_SITE,this.getSite());
        values.put(MovieContract.TrailerEntry.COLUMN_KEY,this.getKey());
        values.put(MovieContract.TrailerEntry.COLUMN_TYPE, this.getType());
        return values;
    }
}
