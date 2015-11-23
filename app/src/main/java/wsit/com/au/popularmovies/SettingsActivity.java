package wsit.com.au.popularmovies;

import android.os.Bundle;
import android.preference.PreferenceActivity;

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
