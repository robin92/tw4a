package pl.rbolanowski.tw4a;

import org.junit.Before;
import org.junit.Test;

import pl.rbolanowski.tw4a.backend.BackendFactory;
import pl.rbolanowski.tw4a.backend.Configurator;
import pl.rbolanowski.tw4a.backend.Database;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import static pl.rbolanowski.tw4a.ConfigureBackendAsyncTask.*;

public class ConfigureBackendAsyncTaskTest {

    private static class FakeException extends Configurator.BackendException {}

    private Configurator mConfigurator;
    private ConfiguringFinishedListener mListener;
    private ConfigureBackendAsyncTask mTask;

    @Before public void setUp() throws Exception {
        configureMocks();
        mTask = new ConfigureBackendAsyncTask(mConfigurator);
    }

    private void configureMocks() {
        mConfigurator = mock(Configurator.class);
        mListener = mock(ConfiguringFinishedListener.class);
    }

    @Test public void configuresBackend() throws Exception {
        assertTrue(mTask.doInBackground());
        verify(mConfigurator, atLeastOnce()).configure();
    }

    @Test public void configuringBackendFails() throws Exception {
        doThrow(FakeException.class).when(mConfigurator).configure();
        assertFalse(mTask.doInBackground());
        verify(mConfigurator, atLeastOnce()).configure();
    }

    @Test public void configuringSuccessCallsListener() throws Exception {
        mTask.setConfiguringFinishedListener(mListener);
        mTask.onPostExecute(true);
        verify(mListener, times(1)).onConfiguringSucceeded();
    }

    @Test public void configuringFailureCallsListener() throws Exception {
        mTask.setConfiguringFinishedListener(mListener);
        mTask.onPostExecute(false);
        verify(mListener, times(1)).onConfiguringFailed();
    }

}

