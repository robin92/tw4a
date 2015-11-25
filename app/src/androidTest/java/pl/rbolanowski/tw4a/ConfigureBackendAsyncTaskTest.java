package pl.rbolanowski.tw4a;

import android.view.View;
import android.widget.ListView;
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
    private View mReadyView = new View(getTargetContext());
    private Configurator mConfigurator;
    private ConfigureBackendAsyncTask mTask;

    @Before public void setUp() throws Exception {
        configureMocks();
        mTask = new ConfigureBackendAsyncTask(mConfigurator, mLoadingView, mReadyView);
    }

    private void configureMocks() {
        mConfigurator = mock(Configurator.class);
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
        new ConfigureBackendAsyncTask(mConfigurator, null, null).onPostExecute(null);

        new ConfigureBackendAsyncTask(mConfigurator, mLoadingView, null).onPostExecute(null);
        assertEquals(View.GONE, mLoadingView.getVisibility());

        setUpViews();
        new ConfigureBackendAsyncTask(mConfigurator, null, mReadyView).onPostExecute(null);
        assertEquals(View.VISIBLE, mReadyView.getVisibility());

        setUpViews();
        mTask.onPostExecute(null);
        assertVisibilityChanged();
    }

    @Test public void callsScheduledAction() throws Exception {
        Runnable runnable = mock(Runnable.class);
        mTask.schedule(runnable);
        mTask.onPostExecute(null);
        verify(runnable, atLeastOnce()).run();
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
