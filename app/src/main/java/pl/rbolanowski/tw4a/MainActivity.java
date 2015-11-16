package pl.rbolanowski.tw4a;

import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

import com.google.inject.Inject;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import static android.view.ContextMenu.ContextMenuInfo;
import static android.widget.AdapterView.AdapterContextMenuInfo;

import pl.rbolanowski.tw4a.backend.*;

@ContentView(R.layout.main)
public class MainActivity extends BaseActivity {

    @Inject private BackendFactory mBackend;
    @InjectView(android.R.id.list) private ListView mListView;
    @InjectView(android.R.id.progress) private View mLoadingView;

    private TaskAdapter mTaskAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTaskAdapter = new TaskAdapter(this, R.layout.task_list_element, new ArrayList<Task>());
        mListView.setAdapter(mTaskAdapter);
        registerForContextMenu(mListView);
    }

    @Override
    public void onStart() {
        super.onStart();
        configureBackendAsync();
    }

    private void configureBackendAsync() {
        new ConfigureBackendAsyncTask(mBackend, mLoadingView, mListView, mTaskAdapter).execute();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_done:
                handleCompleteTask(info);
                return true;

            case R.id.menu_edit:
                handleNotImplementedFeature();
                return true;

            default: return true;
        }
    }

    private void handleCompleteTask(AdapterContextMenuInfo info) {
        Database database = mBackend.newDatabase();
        completeTask(database, mTaskAdapter.getItem(info.position));
        mTaskAdapter.clear();
        mTaskAdapter.addAll(database.select());
        mTaskAdapter.notifyDataSetChanged();
    }

    private void completeTask(Database database, Task task) {
        try {
            task.done = true;
            database.update(task);
        }
        catch (Database.NotStoredException e) {
            task.done = false;
            throw new RuntimeException(e.toString());
        }
    }

    private void handleNotImplementedFeature() {
        Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
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
    private TaskAdapter mTaskListAdapter;

    public ConfigureBackendAsyncTask(BackendFactory backend, View loadingView, View readyView, TaskAdapter taskListAdapter) {
        super(loadingView, readyView);
        mConfigurator = backend.newConfigurator();
        mDatabase = backend.newDatabase();
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
        mTaskListAdapter.addAll(mDatabase.select());
    }

}
