package pl.rbolanowski.tw4a;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import pl.rbolanowski.tw4a.backend.*;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "MainActivity";
    private static final String BACKEND_URL_STR = "https://dl.dropboxusercontent.com/u/90959340/tw4a/2.4.4/armeabi/task";

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
        BackendProvider provider;
        try {
            provider = new UrlBackendProvider(new URL(BACKEND_URL_STR));
        }
        catch (MalformedURLException e) {
            Log.wtf(LOG_TAG, "backend url is malformed: " + BACKEND_URL_STR);
            return;
        }

        assert provider != null;
        new ConfigureBackendAsyncTask(
                new NativeBinaryBackendConfigurator(this, provider),
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

    private BackendConfigurator mConfigurator;

    public ConfigureBackendAsyncTask(BackendConfigurator configurator, View loadingView, View readyView) {
        super(loadingView, readyView);
        mConfigurator = configurator;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            mConfigurator.configure();
        }
        catch (BackendConfigurator.BackendException e) {
            Log.e(LOG_TAG, "configuring backend failed: " + e.toString());
        }
        return null;
    }

}

