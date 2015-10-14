package pl.rbolanowski.tw4a.backend;

import android.support.test.runner.AndroidJUnit4;
import android.test.mock.MockContext;
import java.io.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.rbolanowski.tw4a.StreamUtil;
import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class NativeBinaryBackendConfiguratorTest extends AndroidMockitoTestCase {

    private static final String BINARY_CONTENT = "hello world";

    private MockContext mContext;
    private BackendProvider mProvider;
    private File mFile;
    private BackendConfigurator mConfigurator;
    private StreamUtil mStreams = new StreamUtil();

    private InputStream mResourceInput;
    private FileOutputStream mResourceOutput;
    private File mOutputFile;

    @Before public void setUp() throws Exception {
        configureContext();
        configureProvider();
        configureConfigurator();
    }

    private void configureContext() {
        mContext = mock(MockContext.class);
        mFile = mock(File.class);
        when(mContext.getFileStreamPath("task")).thenReturn(mFile);
    }

    private void configureProvider() throws Exception {
        mProvider = mock(BackendProvider.class);
        when(mProvider.getInputStream()).thenReturn(null);
    }

    private void configureConfigurator() {
        assertNotNull(mContext);
        assertNotNull(mProvider);
        mConfigurator = new NativeBinaryBackendConfigurator(mContext, mProvider);
    }

    @Test public void getsPathToBackendBinary() {
        verify(mContext).getFileStreamPath("task");
    }

    @Test(expected = BackendConfigurator.BackendException.class)
    public void downloadingBackendBinaryFailureHandling() throws Exception {
        when(mFile.exists()).thenReturn(false);
        when(mProvider.getInputStream()).thenThrow(IOException.class);
        mConfigurator.configure();
    }

    @Test(expected = BackendConfigurator.BackendException.class)
    public void openingInternalStorageFailureHandling() throws Exception {
        when(mFile.exists()).thenReturn(false);
        when(mContext.openFileOutput(anyString(), anyInt())).thenThrow(IOException.class);
        mConfigurator.configure();
    }

    @Test public void downloadsBackendBinary() throws Exception {
        configureResources();

        when(mFile.exists()).thenReturn(false);
        when(mProvider.getInputStream()).thenReturn(mResourceInput);
        when(mContext.openFileOutput(anyString(), anyInt())).thenReturn(mResourceOutput);
        mConfigurator.configure();

        verifyIsExecutable(mFile);
        assertArrayEquals(BINARY_CONTENT.getBytes(), readFile(mOutputFile));
    }

    private void configureResources() throws Exception {
        mOutputFile = File.createTempFile("tw4aTest", ".txt");
        mOutputFile.deleteOnExit();
        mResourceInput = new ByteArrayInputStream(BINARY_CONTENT.getBytes());
        mResourceOutput = new FileOutputStream(mOutputFile);
    }

    @Test public void skipsDownloadingWhenBackendAlreadyThere() throws Exception {
        when(mFile.exists()).thenReturn(true);
        mConfigurator.configure();
        verifyZeroInteractions(mProvider);
        verifyIsExecutable(mFile);
    }

    private static void verifyIsExecutable(File file) {
        verify(file).setExecutable(true);
        verify(file, never()).setExecutable(false);
    }

    private byte[] readFile(File file) throws IOException {
        return mStreams.read(new FileInputStream(file)).toByteArray();
    }

}
