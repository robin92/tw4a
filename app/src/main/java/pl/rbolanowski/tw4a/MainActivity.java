package pl.rbolanowski.tw4a;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

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

    private TaskListAdapter mTaskListAdapter;

    @Override
    public void onStart() {
        super.onStart();
        mTaskListAdapter = new TaskListAdapter(this, R.layout.task_list_element, new ArrayList<Task>());
        registerForContextMenu(mListView);
        configureBackendAsync();
    }

    private void configureBackendAsync() {
        new ConfigureBackendAsyncTask(mBackend, mLoadingView, mListView, mTaskListAdapter).execute();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Toast toast;
        String text;
        switch (item.getItemId()) {
            case R.id.menu_done:
                text = getString(R.string.done_not_implemented);
                toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                toast.show();
                text = mTaskListAdapter.getItem(info.position).description;
                toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                toast.show();
                return true;
            case R.id.menu_edit:
                text = mTaskListAdapter.getItem(info.position).description + ": " + getString(R.string.edit_not_implemented);
                toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
                toast.show();
                return true;
        }
        return true;
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
    private ListView mListView;
    private TaskListAdapter mTaskListAdapter;

    public ConfigureBackendAsyncTask(BackendFactory backend, View loadingView, View readyView, TaskListAdapter taskListAdapter) {
        super(loadingView, readyView);
        mConfigurator = backend.newConfigurator();
        mDatabase = backend.newDatabase();
        mListView = (ListView) readyView;
        mTaskListAdapter = taskListAdapter;
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
        mTaskListAdapter.addAll(values);
        mListView.setAdapter(mTaskListAdapter);
    }

}
