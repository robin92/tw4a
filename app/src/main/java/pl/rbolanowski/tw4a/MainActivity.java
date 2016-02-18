package pl.rbolanowski.tw4a;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.*;
import android.util.Log;
import android.view.View;

import com.google.inject.Inject;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import pl.rbolanowski.tw4a.backend.*;

@ContentView(R.layout.main)
public class MainActivity
    extends BaseActivity
    implements
        ConfigureBackendAsyncTask.ConfiguringFinishedListener,
        ErrorFragment.OnRetryListener {

    @Inject private BackendFactory mBackend;
    private LoadingFragment mLoadingFragment = new LoadingFragment();
    private ErrorFragment mErrorFragment = new ErrorFragment();
    private TaskListFragment mTaskListFragment = new TaskListFragment();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        mErrorFragment.setOnRetryListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        onRetry();
    }

    @Override
    public void onConfiguringSucceeded() {
        replaceContent(mTaskListFragment);
    }

    @Override
    public void onConfiguringFailed() {
        replaceContent(mErrorFragment);
    }

    @Override
    public void onRetry() {
        replaceContent(mLoadingFragment);
        configureBackendAsync();
    }

    private void configureBackendAsync() {
        ConfigureBackendAsyncTask task = new ConfigureBackendAsyncTask(mBackend.newConfigurator());
        task.setConfiguringFinishedListener(this);
        task.execute();
    }

    private void replaceContent(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(android.R.id.content, fragment);
        transaction.commit();
    }

}

class ConfigureBackendAsyncTask extends AsyncTask<Void, Void, Boolean> {

    public static interface ConfiguringFinishedListener {
        
        void onConfiguringSucceeded();

        void onConfiguringFailed();

    }

    private static final String LOG_TAG = "ConfigureBackendAsyncTask";

    private Configurator mConfigurator;
    private ConfiguringFinishedListener mConfiguringListener;

    public ConfigureBackendAsyncTask(Configurator configurator) {
        mConfigurator = configurator;
    }

    public void setConfiguringFinishedListener(ConfiguringFinishedListener listener) {
        mConfiguringListener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean status = false;
        try {
            mConfigurator.configure();
            status = true;
        }
        catch (Configurator.BackendException e) {
            Log.e(LOG_TAG, "configuring backend failed: " + e.toString());
        }
        return status;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (mConfiguringListener != null) {
            if (success) mConfiguringListener.onConfiguringSucceeded();
            else mConfiguringListener.onConfiguringFailed();
        }
    }

}

