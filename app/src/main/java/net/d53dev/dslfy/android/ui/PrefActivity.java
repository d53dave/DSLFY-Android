package net.d53dev.dslfy.android.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import net.d53dev.dslfy.android.R;

/**
 * Created by davidsere on 19/11/15.
 */
public class PrefActivity extends PreferenceActivity{
    SharedPreferences prefs=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Leanback_Details);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();

        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(onChange);
    }

    @Override
    public void onPause() {
        prefs.unregisterOnSharedPreferenceChangeListener(onChange);

        super.onPause();
    }

    SharedPreferences.OnSharedPreferenceChangeListener onChange=
            new SharedPreferences.OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

                }
            };
}
