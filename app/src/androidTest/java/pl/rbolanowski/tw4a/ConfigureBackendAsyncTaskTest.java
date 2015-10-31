package pl.rbolanowski.tw4a;

import android.view.View;

import org.junit.Before;
import org.junit.Test;

import pl.rbolanowski.tw4a.backend.Configurator;
import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static org.mockito.Mockito.*;

public class ConfigureBackendAsyncTaskTest extends AndroidMockitoTestCase {

    private View mLoadingView = makeView();
    private View mReadyView = makeView();
    private Configurator mConfigurator;
    private ConfigureBackendAsyncTask mTask;

    private static View makeView() {
        return new View(getTargetContext());
    }

    @Before public void setUp() throws Exception {
        mConfigurator = mock(Configurator.class);
        mTask = new ConfigureBackendAsyncTask(mConfigurator, mLoadingView, mReadyView);
    }

    @Test public void configuresBackend() throws Exception {
        mTask.doInBackground();
        verify(mConfigurator, atLeastOnce()).configure();
    }

    @Test public void changesLoadingAndReadyViewVisibility() throws Exception {
        mLoadingView.setVisibility(View.VISIBLE);
        mReadyView.setVisibility(View.GONE);

        mTask.onPostExecute(null);

        assertEquals(View.GONE, mLoadingView.getVisibility());
        assertEquals(View.VISIBLE, mReadyView.getVisibility());
    }

}
