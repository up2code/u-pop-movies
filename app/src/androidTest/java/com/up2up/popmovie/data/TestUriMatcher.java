package com.up2up.popmovie.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {

    private static final long TEST_MOVIE_ID = 1;

    // content://com.up2up.popmovie/movie"
    private static final Uri TEST_MOVIE = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_REVIEW_MOVIE = MovieContract.ReviewEntry.CONTENT_URI;
    private static final Uri TEST_TRAILER_MOVIE = MovieContract.TrailerEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE), MovieProvider.MOVIE);
        assertEquals("Error: The REVIEW MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEW_MOVIE), MovieProvider.REVIEW_MOVIE);
        assertEquals("Error: The TRAILER MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILER_MOVIE), MovieProvider.TRAILER_MOVIE);
    }
}
