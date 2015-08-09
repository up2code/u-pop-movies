package com.up2up.popmovie;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utility {

    static String getPreferenceSortOrder(Context context) {
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
        return sh.getString(context.getString(R.string.pref_sort_key), context.getString(R.string.pref_sort_default));
    }
}
