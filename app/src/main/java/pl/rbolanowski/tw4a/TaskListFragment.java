package pl.rbolanowski.tw4a;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.Vector;

import com.google.inject.Inject;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectView;

import pl.rbolanowski.tw4a.backend.BackendFactory;
import pl.rbolanowski.tw4a.backend.Database;

import static android.view.ContextMenu.ContextMenuInfo;
import static android.widget.AdapterView.AdapterContextMenuInfo;

import static pl.rbolanowski.tw4a.backend.Database.*;

public class TaskListFragment
    extends RoboListFragment
    implements
        TaskDialog.OnTaskChangedListener,
        View.OnClickListener {

    private static final String LOG_TAG = TaskListFragment.class.getSimpleName();

    private static Vector<Task> vectorOf(Task... tasks) {
        Vector<Task> result = new Vector<Task>();
        for (Task task : tasks) result.add(task);
        return result;
    }

    private class ContextMenuHandler {

        private Database mDatabase;

        private ContextMenuHandler() {
            mDatabase = mBackend.newDatabase();
        }
        
        public boolean onItemSelected(MenuItem item) {
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            switch (item.getItemId()) {
                case R.id.done: return handleCompleteTask(info);
                case R.id.edit: return handleEditTask(info);
                default:        return handleNotImplementedFeature();
            }
        }

        private boolean handleCompleteTask(AdapterContextMenuInfo info) {
            completeTask((Task) mTaskAdapter.getItem(info.position));
            mTaskAdapter.notifyDataSetInvalidated();
            registerAdapter();
            return true;
        }
        
        private boolean handleEditTask(AdapterContextMenuInfo info) {
            Task currentTask = (Task) mTaskAdapter.getItem(info.position);
            Bundle bundle = new Bundle();
            bundle.putParcelable("current task", currentTask);
            TaskDialog dialog = new TaskDialog();
            dialog.setOnTaskChangedListener(TaskListFragment.this);
            dialog.setArguments(bundle);
            dialog.show(getActivity().getSupportFragmentManager(), "Add new task");
            return true;
        }

        private void completeTask(Task task) {
            try {
                task.done = true;
                mDatabase.update(task);
            }
            catch (Database.NotStoredException e) {
                task.done = false;
                throw new RuntimeException(e.toString());
            }
        }

        private boolean handleNotImplementedFeature() {
            Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
            return true;
        }

    }

    private class QueryListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText == null || newText.equals("")) {
                filterTasks(null);
                return true;
            }
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            filterTasks(query);
            return true;
        }

        private void filterTasks(String query) {
            mTaskAdapter.getFilter().filter(query);
        }

    }

    @Inject private BackendFactory mBackend;
    @InjectView(android.R.id.empty) private View mEmptyView;
    @InjectView(R.id.add_button) private View mAddNewTaskButton;

    private ContextMenuHandler mContextMenu;

    private TaskAdapter mTaskAdapter;
    private Vector<Task> mTasks;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        registerAdapter();
        registerContextMenu();
        registerClickListeners();
    }

    private void registerAdapter() {
        mTasks = vectorOf(mBackend.newDatabase().select());
        mTaskAdapter = new TaskAdapter(getActivity(), R.layout.task_list_element, mTasks);
        setListAdapter(mTaskAdapter);
        getListView().setEmptyView(mEmptyView);
    }

    private void registerContextMenu() {
        mContextMenu = new ContextMenuHandler();
        registerForContextMenu(getListView());
    }

    private void registerClickListeners() {
        mAddNewTaskButton.setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task_list_actions, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new QueryListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.context_task, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return mContextMenu.onItemSelected(item);
    }

    @Override
    public void onTaskChanged(Task task) {
        boolean result;
        if (task.uuid == null) {
            result = tryInsertTask(task, mBackend.newDatabase());
        } else {
            result = tryUpdateTask(task, mBackend.newDatabase());
        }

        if (result) {
            mTaskAdapter.notifyDataSetInvalidated();
            registerAdapter();
        }
    }

    private static boolean tryInsertTask(Task task, Database database) {
        try {
            database.insert(task);
            return true;
        }
        catch (AlreadyStoredException | IncompleteArgumentException  e) {
            Log.e(LOG_TAG, e.toString());
        }
        return false;
    }

    private static boolean tryUpdateTask(Task task, Database database) {
        try {
            database.update(task);
            return true;
        }
        catch (NotStoredException e) {
            Log.e(LOG_TAG, e.toString());
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        showAddTaskDialog();
    }

    private void showAddTaskDialog() {
        TaskDialog dialog = new TaskDialog();
        dialog.setOnTaskChangedListener(TaskListFragment.this);
        dialog.show(getActivity().getSupportFragmentManager(), "Add new task");
    }

}

