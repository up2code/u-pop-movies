package com.up2up.popmovie.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by augsorn on 6/8/2558.
 */
public class TestMovieContract extends AndroidTestCase {
    private static final long TEST_MOVIE_ID = 1;

    public void testBuildMovie() {
        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieUri in " +
                        "MovieContract.",
                movieUri);

                assertEquals("Error: Movie not properly appended to the end of the Uri",
                        String.valueOf(TEST_MOVIE_ID), movieUri.getLastPathSegment());

        assertEquals("Error: Movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.up2up.popmovie/movie/1");
    }

    public void testBuildReview() {
        Uri movieReviewUri = MovieContract.ReviewEntry.buildReviewUri(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildReviewUri in " +
                        "MovieContract.",
                movieReviewUri);

        assertEquals("Error: Movie Review not properly appended to the end of the Uri",
                String.valueOf(TEST_MOVIE_ID), movieReviewUri.getLastPathSegment());

        assertEquals("Error: Movie Review Uri doesn't match our expected result",
                movieReviewUri.toString(),
                "content://com.up2up.popmovie/review/1");
    }

    public void testBuildTrailer() {
        Uri movieTrailerUri = MovieContract.TrailerEntry.buildTrailerUri(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildTrailerUri in " +
                        "MovieContract.",
                movieTrailerUri);

        assertEquals("Error: Movie Trailer not properly appended to the end of the Uri",
                String.valueOf(TEST_MOVIE_ID), movieTrailerUri.getLastPathSegment());

        assertEquals("Error: Movie Trailer Uri doesn't match our expected result",
                movieTrailerUri.toString(),
                "content://com.up2up.popmovie/trailer/1");
    }
}
