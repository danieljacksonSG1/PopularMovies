package wsit.com.au.popularmovies.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import wsit.com.au.popularmovies.R;

/**
 * Created by guyb on 18/11/15.
 */
public class SettingsActivity extends PreferenceActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }


}
