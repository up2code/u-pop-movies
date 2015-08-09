package com.up2up.popmovie.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by augsorn on 30/7/2558.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

     /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.TrailerEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.ReviewEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'",null);

        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(MovieContract.MovieEntry._ID);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_TITLE);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RUNTIME);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_COUNT);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VIDEO);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();

    }

    public long testMovieTable() {

        // First step: Get reference to writable database
        MovieDbHelper   dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
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

        // Third Step: Insert ContentValues into database and get a row ID back
        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME,null, testValues);

        // Verify we got a row back
        assertTrue(movieRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue("Error: No Records returned from movie query", cursor.moveToFirst());

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from movie query",
                cursor.moveToNext());

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();

        return movieRowId;
    }

    public void testTrailerTable() {

        long movieRowId = testMovieTable();

        // First step: Get reference to writable database
        MovieDbHelper   dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY,movieRowId);
        testValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID,"533ec654c3a36854480003eb");
        testValues.put(MovieContract.TrailerEntry.COLUMN_NAME,"/2lECpi35Hnbpa4y46JX0aY3AWTy.jpg");
        testValues.put(MovieContract.TrailerEntry.COLUMN_SITE,"YouTube");
        testValues.put(MovieContract.TrailerEntry.COLUMN_KEY,"SUXWAEX2jlg");
        testValues.put(MovieContract.TrailerEntry.COLUMN_TYPE,"Trailer");

        // Third Step: Insert ContentValues into database and get a row ID back
        long trailerRowId;
        trailerRowId = db.insert(MovieContract.TrailerEntry.TABLE_NAME,null, testValues);

        // Verify we got a row back
        assertTrue(trailerRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                MovieContract.TrailerEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from trailer query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Trailer Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from trailer query",
                cursor.moveToNext());

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();

    }

    public void testReviewTable() {

        long movieRowId = testMovieTable();

        // First step: Get reference to writable database
        MovieDbHelper   dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY,movieRowId);
        testValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID,"5010553819c2952d1b000451\"");
        testValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR,"Travis Bell");
        testValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT,"I felt like this was a tremendous end to Nolan's Batman trilogy. The Dark Knight Rises may very well have been the weakest of all 3 films but when you're talking about a scale of this magnitude, it still makes this one of the best movies I've seen in the past few years.\\r\\n\\r\\nI expected a little more _Batman_ than we got (especially with a runtime of 2:45) but while the story around the fall of Bruce Wayne and Gotham City was good I didn't find it amazing. This might be in fact, one of my only criticisms�it was a long movie but still, maybe too short for the story I felt was really being told. I feel confident in saying this big of a story could have been split into two movies.\\r\\n\\r\\nThe acting, editing, pacing, soundtrack and overall theme were the same 'as-close-to-perfect' as ever with any of Christopher Nolan's other films. Man does this guy know how to make a movie!\\r\\n\\r\\nYou don't have to be a Batman fan to enjoy these movies and I hope any of you who feel this way re-consider. These 3 movies are without a doubt in my mind, the finest display of comic mythology ever told on the big screen. They are damn near perfect.");
        testValues.put(MovieContract.ReviewEntry.COLUMN_URL,"http://j.mp/QSjAK2");

        // Third Step: Insert ContentValues into database and get a row ID back
        long reviewRowId;
        reviewRowId = db.insert(MovieContract.ReviewEntry.TABLE_NAME,null, testValues);

        // Verify we got a row back
        assertTrue(reviewRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                MovieContract.ReviewEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from review query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Review Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from review query",
                cursor.moveToNext());

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();

    }
}
