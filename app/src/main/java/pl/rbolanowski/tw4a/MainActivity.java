package pl.rbolanowski.tw4a;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import pl.rbolanowski.tw4a.backend.*;
import pl.rbolanowski.tw4a.backend.taskwarrior.TaskwarriorBackendFactory;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onStart() {
        super.onStart();
        configureBackendAsync();
    }

    private void configureBackendAsync() {
        new ConfigureBackendAsyncTask(
                new TaskwarriorBackendFactory(this).newConfigurator(),
                findViewById(android.R.id.progress),
                findViewById(android.R.id.text1))
            .execute();
    }

}

abstract class ResourceLoadingAsyncTask extends AsyncTask<Void, Void, Void> {

    private View mLoadingView;
    private View mReadyView;

    public ResourceLoadingAsyncTask(View loadingView, View readyView) {
        mLoadingView = loadingView;
        mReadyView = readyView;
    }

    @Override
    protected void onPostExecute(Void someVoid) {
        int visibility = mLoadingView.getVisibility();
        mLoadingView.setVisibility(mReadyView.getVisibility());
        mReadyView.setVisibility(visibility);
    }

}

class ConfigureBackendAsyncTask extends ResourceLoadingAsyncTask {

    private static final String LOG_TAG = "ConfigureBackendAsyncTask";

    private Configurator mConfigurator;

    public ConfigureBackendAsyncTask(Configurator configurator, View loadingView, View readyView) {
        super(loadingView, readyView);
        mConfigurator = configurator;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            mConfigurator.configure();
        }
        catch (Configurator.BackendException e) {
            Log.e(LOG_TAG, "configuring backend failed: " + e.toString());
        }
        return null;
    }

}

