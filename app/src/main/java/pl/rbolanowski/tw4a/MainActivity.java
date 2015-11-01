package pl.rbolanowski.tw4a;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import android.widget.ListView;

import com.google.inject.Inject;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import pl.rbolanowski.tw4a.backend.*;

@ContentView(R.layout.main)
public class MainActivity extends RoboActivity {

    @Inject private BackendFactory mBackend;
    @InjectView(android.R.id.list) private ListView mListView;
    @InjectView(android.R.id.progress) private View mLoadingView;

    @Override
    public void onStart() {
        super.onStart();
        configureBackendAsync();
    }

    private void configureBackendAsync() {
        new ConfigureBackendAsyncTask(this, mBackend, mLoadingView, mListView).execute();
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
        mLoadingView.setVisibility(View.GONE);
        mReadyView.setVisibility(View.VISIBLE);
    }

}

class ConfigureBackendAsyncTask extends ResourceLoadingAsyncTask {

    private static final String LOG_TAG = "ConfigureBackendAsyncTask";

    private Context mContext;
    private Configurator mConfigurator;
    private Database mDatabase;
    private ListView mListView;

    public ConfigureBackendAsyncTask(Context context, BackendFactory backend, View loadingView, View readyView) {
        super(loadingView, readyView);
        mContext = context;
        mConfigurator = backend.newConfigurator();
        mDatabase = backend.newDatabase();
        mListView = (ListView) readyView;
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
        populateList();
    }

    private void populateList() {
        Task[] values = mDatabase.select();
        TaskListAdapter taskListAdapter = new TaskListAdapter(mContext, R.layout.task_list_element, values);
        mListView.setAdapter(taskListAdapter);
    }

}
