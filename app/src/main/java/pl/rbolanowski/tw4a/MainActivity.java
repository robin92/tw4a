package pl.rbolanowski.tw4a;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.google.inject.Inject;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import pl.rbolanowski.tw4a.backend.*;

@ContentView(R.layout.main)
public class MainActivity extends BaseActivity {

    @Inject private BackendFactory mBackend;
    @InjectView(android.R.id.content) private View mContent;
    @InjectView(android.R.id.progress) private View mLoadingView;

    @Override
    public void onStart() {
        super.onStart();
        configureBackendAsync();
    }

    private void configureBackendAsync() {
        new ConfigureBackendAsyncTask(mBackend.newConfigurator(), mLoadingView, mContent)
            .schedule(new Runnable() {
                @Override
                public void run() {
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(android.R.id.content, new TaskListFragment());
                    transaction.commit();
                }
            })
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
        setVisibility(mLoadingView, View.GONE);
        setVisibility(mReadyView, View.VISIBLE);
    }

    private static void setVisibility(View view, int value) {
        if (view != null) {
            view.setVisibility(value);
        }
    }

}

class ConfigureBackendAsyncTask extends ResourceLoadingAsyncTask {

    private static final String LOG_TAG = "ConfigureBackendAsyncTask";

    private Configurator mConfigurator;
    private Runnable mRunnable;

    public ConfigureBackendAsyncTask(Configurator configurator, View loadingView, View readyView) {
        super(loadingView, readyView);
        mConfigurator = configurator;
    }

    public ConfigureBackendAsyncTask schedule(Runnable runnable) {
        mRunnable = runnable;
        return this;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            mConfigurator.configure();
        }
        catch (Configurator.BackendException e) {
            Log.e(LOG_TAG, "configuring backend failed: " + e.toString());
            throw new IllegalStateException();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void someVoid) {
        super.onPostExecute(someVoid);   
        if (mRunnable != null) {
            mRunnable.run();
        }
    }

}

