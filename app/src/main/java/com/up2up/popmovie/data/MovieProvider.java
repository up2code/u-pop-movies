package com.up2up.popmovie.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by augsorn on 5/8/2558.
 */
public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper   mOpenHelper;

    static final int MOVIE = 100;
    static final int REVIEW_MOVIE = 200;
    static final int TRAILER_MOVIE = 300;

    private static final SQLiteQueryBuilder sFavoriteMovieQueryBuilder;
    private static final SQLiteQueryBuilder sFavoriteMovieReviewQueryBuilder;
    private static final SQLiteQueryBuilder sFavoriteMovieTrailerQueryBuilder;

    static{
        sFavoriteMovieQueryBuilder = new SQLiteQueryBuilder();
        sFavoriteMovieReviewQueryBuilder = new SQLiteQueryBuilder();
        sFavoriteMovieTrailerQueryBuilder = new SQLiteQueryBuilder();

        sFavoriteMovieQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);
        sFavoriteMovieReviewQueryBuilder.setTables(MovieContract.ReviewEntry.TABLE_NAME);
        sFavoriteMovieTrailerQueryBuilder.setTables(MovieContract.TrailerEntry.TABLE_NAME);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case MOVIE:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case REVIEW_MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case TRAILER_MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case REVIEW_MOVIE:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case TRAILER_MOVIE:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case REVIEW_MOVIE: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case TRAILER_MOVIE: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW_MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case TRAILER_MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEW_MOVIE:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);

            case TRAILER_MOVIE:
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER_MOVIE);
        return matcher;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
