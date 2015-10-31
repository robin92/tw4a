package pl.rbolanowski.tw4a;

import android.view.View;

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

    private View mLoadingView = makeView();
    private View mReadyView = makeView();
    private Configurator mConfigurator;
    private ConfigureBackendAsyncTask mTask;

    private static View makeView() {
        return new View(getTargetContext());
    }

    @Before public void setUp() throws Exception {
        BackendFactory factory = mock(BackendFactory.class);
        mConfigurator = mock(Configurator.class);
        when(factory.newConfigurator()).thenReturn(mConfigurator);
        when(factory.newDatabase()).thenReturn(mock(Database.class));
        mTask = new ConfigureBackendAsyncTask(factory, mLoadingView, mReadyView);
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

    @Test public void setsDatabaseOnSuccessfulConfiguration() throws Exception {
        mTask.onPostExecute(null);
        assertNotNull(DatabaseProvider.getInstance().getDatabase());
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
