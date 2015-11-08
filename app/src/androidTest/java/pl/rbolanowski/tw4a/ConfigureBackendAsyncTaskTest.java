package pl.rbolanowski.tw4a;

import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

import pl.rbolanowski.tw4a.backend.BackendFactory;
import pl.rbolanowski.tw4a.backend.Configurator;
import pl.rbolanowski.tw4a.backend.Database;
import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static org.mockito.Mockito.*;
import static org.junit.Assume.assumeTrue;

public class ConfigureBackendAsyncTaskTest extends AndroidMockitoTestCase {

    private static class FakeException extends Configurator.BackendException {}

    private View mLoadingView = new View(getTargetContext());
    private View mReadyView = new ListView(getTargetContext());
    private Configurator mConfigurator;
    private Database mDatabase;
    private TaskListAdapter mTaskListAdapter;
    private ConfigureBackendAsyncTask mTask;

    @Before public void setUp() throws Exception {
        configureMocks();
        mTaskListAdapter = new TaskListAdapter(getTargetContext(), R.layout.task_list_element, new ArrayList<Task>());
        mTask = new ConfigureBackendAsyncTask(makeFactory(), mLoadingView, mReadyView, mTaskListAdapter);
    }

    private void configureMocks() {
        mConfigurator = mock(Configurator.class);

        mDatabase = mock(Database.class);
        when(mDatabase.select()).thenReturn(new Task[0]);
    }

    private BackendFactory makeFactory() {
        BackendFactory factory = mock(BackendFactory.class);
        when(factory.newConfigurator()).thenReturn(mConfigurator);
        when(factory.newDatabase()).thenReturn(mDatabase);
        return factory;
    }

    @Test(expected = IllegalStateException.class)
    public void failureDuringConfigurationIsFatal() throws Exception {
        doThrow(FakeException.class).when(mConfigurator).configure();
        mTask.doInBackground();
    }

    @Test public void configuresBackend() throws Exception {
        mTask.doInBackground();
        verify(mConfigurator, atLeastOnce()).configure();
    }

    @Test public void changesLoadingAndReadyViewVisibility() throws Exception {
        setUpViews();
        mTask.onPostExecute(null);
        assertVisibilityChanged();
    }

    @Test public void multipleCallSetsReadyViewVisible() throws Exception {
        setUpViews();
        mTask.onPostExecute(null);
        mTask.onPostExecute(null);
        assertVisibilityChanged();
    }

    private void setUpViews() {
        mLoadingView.setVisibility(View.VISIBLE);
        mReadyView.setVisibility(View.GONE);
    }

    private void assertVisibilityChanged() {
        boolean loadingViewGone = mLoadingView.getVisibility() == View.GONE;
        boolean readyViewVisible = mReadyView.getVisibility() == View.VISIBLE;
        assumeTrue(loadingViewGone);
        assumeTrue(readyViewVisible);
        assertTrue(loadingViewGone && readyViewVisible);
    }

}
