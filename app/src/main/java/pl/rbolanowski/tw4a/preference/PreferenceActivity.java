package pl.rbolanowski.tw4a.preference;

import roboguice.activity.RoboPreferenceActivity;

import pl.rbolanowski.tw4a.R;

public class PreferenceActivity extends RoboPreferenceActivity {

    @Override
    public void onCreate(android.os.Bundle savedState) {
        super.onCreate(savedState);
        addPreferencesFromResource(R.xml.preferences);
    }

}

