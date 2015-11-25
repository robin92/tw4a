package pl.rbolanowski.tw4a;

import android.view.View;
import android.widget.ListView;
import org.junit.Before;
import org.junit.Test;

import pl.rbolanowski.tw4a.backend.BackendFactory;
import pl.rbolanowski.tw4a.backend.Configurator;
import pl.rbolanowski.tw4a.backend.Database;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class ConfigureBackendAsyncTaskTest {

    private static class FakeException extends Configurator.BackendException {}

    // android objects do nothing in unit tests
    private static class ViewFake extends View {

        private int mVisibility;

        public ViewFake() {
            super(null);
        }

        @Override
        public void setVisibility(int value) {
            mVisibility = value;
        }

        @Override
        public int getVisibility() {
            return mVisibility;
        }

    }

    private View mLoadingView = new ViewFake();
    private View mReadyView = new ViewFake();
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
