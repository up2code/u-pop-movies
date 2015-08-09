package com.up2up.popmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    static final String DATABASE_NAME = "popmovie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RUNTIME + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL " +
                " );";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME + " (" +
                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " TEXT UNIQUE NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_SITE + " TEXT NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ")" +
                "); ";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " (" +
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " TEXT UNIQUE NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_URL + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ")" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
