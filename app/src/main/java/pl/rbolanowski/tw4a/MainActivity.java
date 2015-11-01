package pl.rbolanowski.tw4a;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ListView;

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
        Database database = DatabaseProvider.getInstance().getDatabase();
        ListView list = (ListView) findViewById(android.R.id.list);
        Task[] values = database.select();
        TaskListAdapter taskListAdapter = new TaskListAdapter(this, R.layout.task_list_element, values);
        list.setAdapter(taskListAdapter);
    }

    private void configureBackendAsync() {
        new ConfigureBackendAsyncTask(
                new TaskwarriorBackendFactory(this),
                findViewById(android.R.id.progress),
                findViewById(android.R.id.list))
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
        mLoadingView.setVisibility(View.GONE);
        mReadyView.setVisibility(View.VISIBLE);
    }

}

class ConfigureBackendAsyncTask extends ResourceLoadingAsyncTask {

    private static final String LOG_TAG = "ConfigureBackendAsyncTask";

    private Configurator mConfigurator;
    private Database mDatabase;

    public ConfigureBackendAsyncTask(BackendFactory factory, View loadingView, View readyView) {
        super(loadingView, readyView);
        mConfigurator = factory.newConfigurator();
        mDatabase = factory.newDatabase();
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
        DatabaseProvider.getInstance().setDatabase(mDatabase);
    }

}

