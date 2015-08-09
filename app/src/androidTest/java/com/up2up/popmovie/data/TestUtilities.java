package com.up2up.popmovie.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.up2up.popmovie.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by augsorn on 30/7/2558.
 */
public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {

            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static long insertMovieValues(Context context) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieValues();

        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Movie Values", movieRowId != -1);

        return movieRowId;
    }

    static ContentValues createTrailerValues(long movieRowId) {
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY,movieRowId);
        testValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID,"533ec654c3a36854480003eb");
        testValues.put(MovieContract.TrailerEntry.COLUMN_NAME,"/2lECpi35Hnbpa4y46JX0aY3AWTy.jpg");
        testValues.put(MovieContract.TrailerEntry.COLUMN_SITE,"YouTube");
        testValues.put(MovieContract.TrailerEntry.COLUMN_KEY,"SUXWAEX2jlg");
        testValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, "Trailer");
        return testValues;
    }

    static ContentValues createMovieValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 550);
        testValues.put(MovieContract.MovieEntry.COLUMN_TITLE,"Fight Club");
        testValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,"/2lECpi35Hnbpa4y46JX0aY3AWTy.jpg");
        testValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,"A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \\\"fight clubs\\\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.");
        testValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "1999-10-14");
        testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,7.7);
        testValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, 139);
        testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT,3185);
        testValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, 0);
        return testValues;
    }

    static ContentValues createReviewValues(long movieRowId) {
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY,movieRowId);
        testValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID,"5010553819c2952d1b000451\"");
        testValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR,"Travis Bell");
        testValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT,"I felt like this was a tremendous end to Nolan's Batman trilogy. The Dark Knight Rises may very well have been the weakest of all 3 films but when you're talking about a scale of this magnitude, it still makes this one of the best movies I've seen in the past few years.\\r\\n\\r\\nI expected a little more _Batman_ than we got (especially with a runtime of 2:45) but while the story around the fall of Bruce Wayne and Gotham City was good I didn't find it amazing. This might be in fact, one of my only criticisms�it was a long movie but still, maybe too short for the story I felt was really being told. I feel confident in saying this big of a story could have been split into two movies.\\r\\n\\r\\nThe acting, editing, pacing, soundtrack and overall theme were the same 'as-close-to-perfect' as ever with any of Christopher Nolan's other films. Man does this guy know how to make a movie!\\r\\n\\r\\nYou don't have to be a Batman fan to enjoy these movies and I hope any of you who feel this way re-consider. These 3 movies are without a doubt in my mind, the finest display of comic mythology ever told on the big screen. They are damn near perfect.");
        testValues.put(MovieContract.ReviewEntry.COLUMN_URL,"http://j.mp/QSjAK2");
        return testValues;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
