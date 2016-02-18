package pl.rbolanowski.tw4a.preference;

import android.content.Intent;
import android.preference.Preference;

import roboguice.activity.RoboPreferenceActivity;
import roboguice.inject.InjectPreference;

import pl.rbolanowski.tw4a.R;

public class PreferenceActivity extends RoboPreferenceActivity
    implements Preference.OnPreferenceClickListener {

    private String mCurrentKey;

    @Override
    public void onCreate(android.os.Bundle savedState) {
        super.onCreate(savedState);
        addPreferencesFromResource(R.xml.preferences);

        findPreference(getString(R.string.pref_key_taskwarrior_private_key))
            .setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Intent fileBrowserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileBrowserIntent.setType("application/x-pem-file");
        fileBrowserIntent.addCategory(Intent.CATEGORY_OPENABLE);
        mCurrentKey = preference.getKey();
        startActivityForResult(fileBrowserIntent, 0);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent result) {
        super.onActivityResult(requestCode, responseCode, result);
        if (responseCode == RESULT_OK) {
            getPreferenceManager().getSharedPreferences()
                .edit()
                .putString(mCurrentKey, result.getData().toString())
                .commit();
        }
        mCurrentKey = null;
    }

}

