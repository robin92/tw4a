package pl.rbolanowski.tw4a.preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceGroup;

import roboguice.activity.RoboPreferenceActivity;
import roboguice.inject.InjectPreference;

import pl.rbolanowski.tw4a.R;

public class PreferenceActivity extends RoboPreferenceActivity
    implements
        Preference.OnPreferenceClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private String mCurrentKey;
    @InjectPreference("pref_taskwarrior_sync") private PreferenceGroup mPrefSync;
    @InjectPreference("pref_taskwarrior_sync_state") private Preference mPrefSyncState;
    @InjectPreference("pref_taskwarrior_private_key") private Preference mPrefPrivateKey;

    @Override
    public void onCreate(android.os.Bundle savedState) {
        super.onCreate(savedState);
        addPreferencesFromResource(R.xml.preferences);
        configurePreferenceClickListener(getPreferenceScreen());
        mPrefSyncState.setSummary(
            getPreferenceManager().getSharedPreferences()
                .getString(mPrefSyncState.getKey(), null));
    }

    private void configurePreferenceClickListener(PreferenceGroup preferences) {
        for (int i = 0; i < preferences.getPreferenceCount(); i++) {
            Preference preference = preferences.getPreference(i);
            tryContentPreference(preference);
            tryPreferenceGroup(preference);
        }
    }

    private void tryContentPreference(Preference input) {
        try {
            ContentPreference preference = (ContentPreference) input;
            preference.setOnPreferenceClickListener(this);
        }
        catch (ClassCastException e) {
            // pass
        }
    }

    private void tryPreferenceGroup(Preference input) {
        PreferenceGroup group = null;
        try {
            group = (PreferenceGroup) input;
        }
        catch (ClassCastException e) {
            // pass
        }
        finally {
            if (group != null) {
                configurePreferenceClickListener(group);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(this);
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

    // TODO changing sync state moved to seperate class
    // TODO handling preference change generalized
    // FIXME prepared -> off
    @Override
    public void onSharedPreferenceChanged(SharedPreferences settings, String key) {
        updateSummary(settings, key);
        if (isSynchronizationStatusChanged(settings)) {
            String[] states = getResources().getStringArray(R.array.taskwarrior_sync_states);
            settings.edit()
                .putString(mPrefSyncState.getKey(), states[1])
                .commit();
        }
    }

    private void updateSummary(SharedPreferences settings, String changedKey) {
        final String[] keys = { "pref_taskwarrior_private_key", "pref_taskwarrior_ca", "pref_taskwarrior_cert" };
        for (String key : keys) {
            if (!key.equals(changedKey)) continue;

            Preference preference = findPreference(key);
            String value = settings.getString(key, "");
            if (!"".equals(value)) {
                preference.setSummary(getString(R.string.set));
            }
        }

        if ("pref_taskwarrior_sync_state".equals(changedKey)) {
            Preference preference = findPreference(changedKey);
            preference.setSummary(settings.getString(changedKey, ""));
        }
    }

    private boolean isSynchronizationStatusChanged(SharedPreferences settings) {
        for (int i = 0; i < mPrefSync.getPreferenceCount(); i++) {
            Preference preference = mPrefSync.getPreference(i);
            String value = settings.getString(preference.getKey(), "");
            System.err.println("preference: " + preference.getKey() + ", value: '" + value + "'");
            if ("".equals(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent result) {
        super.onActivityResult(requestCode, responseCode, result);
        getPreferenceManager().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(this);

        if (responseCode == RESULT_OK) {
            getPreferenceManager().getSharedPreferences()
                .edit()
                .putString(mCurrentKey, result.getData().toString())
                .commit();
        }
        mCurrentKey = null;

        getPreferenceManager().getSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(this);
    }

}

