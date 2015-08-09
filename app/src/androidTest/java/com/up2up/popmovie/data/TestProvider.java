package com.up2up.popmovie.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by augsorn on 6/8/2558.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // MovieProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        // content://com.up2up.popmovie/movie/
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.up2up.popmovie/weather
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        // content://com.up2up.popmovie/review/
        type = mContext.getContentResolver().getType(MovieContract.ReviewEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.up2up.popmovie/review
        assertEquals("Error: the ReviewEntry CONTENT_URI should return ReviewEntry.CONTENT_TYPE",
                MovieContract.ReviewEntry.CONTENT_TYPE, type);

        // content://com.up2up.popmovie/trailer/
        type = mContext.getContentResolver().getType(MovieContract.TrailerEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.up2up.popmovie/review
        assertEquals("Error: the TrailerEntry CONTENT_URI should return TrailerEntry.CONTENT_TYPE",
                MovieContract.TrailerEntry.CONTENT_TYPE, type);
    }

    public void testBasicMovieQuery() {
        // insert our test records into the database
        ContentValues movieValues = TestUtilities.createMovieValues();
        TestUtilities.insertMovieValues(mContext);

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQuery", movieCursor, movieValues);
    }

    public void testBasicTrailerQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();
        long movieRowId = TestUtilities.insertMovieValues(mContext);

        ContentValues trailerValues = TestUtilities.createTrailerValues(movieRowId);

        long trailerRowId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, trailerValues);
        assertTrue("Unable to Insert TrailerEntry into the Database", trailerRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor trailerCursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicTrailerQuery", trailerCursor, trailerValues);
    }

    public void testBasicReviewQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long movieRowId = TestUtilities.insertMovieValues(mContext);

        ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);

        long reviewRowId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, reviewValues);
        assertTrue("Unable to Insert ReviewEntry into the Database", reviewRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor reviewCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicReviewQuery", reviewCursor, reviewValues);
    }


    public void testUpdateMovie() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createMovieValues();

        Uri movieUri = mContext.getContentResolver().
                insert(MovieContract.MovieEntry.CONTENT_URI, values);
        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);
        Log.d(LOG_TAG, "New row id: " + movieRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieContract.MovieEntry._ID, movieRowId);
        updatedValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Test update overview");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI, updatedValues, MovieContract.MovieEntry._ID + "= ?",
                new String[] { Long.toString(movieRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,   // projection
                MovieContract.MovieEntry._ID + " = " + movieRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateMovie.  Error validating movie entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
                cursor, testValues);

        //Trailer

        ContentValues trailerValues = TestUtilities.createTrailerValues(movieRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieContract.TrailerEntry.CONTENT_URI, true, tco);
        Uri trailerInsertUri = mContext.getContentResolver().insert(MovieContract.TrailerEntry.CONTENT_URI, trailerValues);
        assertTrue(trailerInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor trailerCursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating TrailerEntry insert.",
                trailerCursor, trailerValues);

        //Review

        ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, tco);
        Uri reviewInsertUri = mContext.getContentResolver().insert(MovieContract.ReviewEntry.CONTENT_URI, reviewValues);
        assertTrue(reviewInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        Cursor reviewCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating ReviewEntry insert.",
                reviewCursor, reviewValues);

    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our movie delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);

        // Register a content observer for our review delete.
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, reviewObserver);

        // Register a content observer for our trailer delete.
        TestUtilities.TestContentObserver trailerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, trailerObserver);

        deleteAllRecordsFromProvider();

        movieObserver.waitForNotificationOrFail();
        reviewObserver.waitForNotificationOrFail();
        trailerObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
        mContext.getContentResolver().unregisterContentObserver(trailerObserver);
    }


}
