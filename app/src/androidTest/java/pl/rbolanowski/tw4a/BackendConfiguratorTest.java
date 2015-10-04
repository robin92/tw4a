package pl.rbolanowski.tw4a;

import android.test.MoreAsserts;
import android.test.mock.MockContext;
import java.io.*;

import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static org.mockito.Mockito.*;

public class BackendConfiguratorTest extends AndroidMockitoTestCase {

    private static final String BINARY_CONTENT = "hello world";

    private MockContext mContext;
    private BackendProvider mProvider;
    private File mFile;
    private BackendConfigurator mConfigurator;
    private StreamUtil mStreams = new StreamUtil();

    private InputStream mResourceInput;
    private FileOutputStream mResourceOutput;
    private File mOutputFile;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setUpContext();
        setUpProvider();
        setUpConfigurator();
    }

    private static void verifyIsExecutable(File file) {
        verify(file).setExecutable(true);
        verify(file, never()).setExecutable(false);
    }

    private void setUpContext() {
        mContext = mock(MockContext.class);
        mFile = mock(File.class);
        when(mContext.getFileStreamPath("task")).thenReturn(mFile);
    }

    private void setUpProvider() throws Exception {
        mProvider = mock(BackendProvider.class);
        when(mProvider.getInputStream()).thenReturn(null);
    }

    private void setUpConfigurator() {
        assertNotNull(mContext, mProvider);
        mConfigurator = new BackendConfiguratorImpl(mContext, mProvider);
    }

    private static void assertThrowsBackendException(ThrowingRunnable runnable) throws Exception {
        assertThrows(BackendConfigurator.BackendException.class, runnable);
    }

    public void testGetsPathToBackendBinary() {
        verify(mContext).getFileStreamPath("task");
    }

    public void testDownloadingBackendBinaryFailureHandling() throws Exception {
        when(mFile.exists()).thenReturn(false);
        when(mProvider.getInputStream()).thenThrow(IOException.class);
        assertThrowsBackendException(new ThrowingRunnable() {
            @Override
            public void run() throws Exception {
                mConfigurator.configure();
            }
        });
    }

    public void testOpeningInternalStorageFailureHandling() throws Exception {
        when(mFile.exists()).thenReturn(false);
        when(mContext.openFileOutput(anyString(), anyInt())).thenThrow(IOException.class);
        assertThrowsBackendException(new ThrowingRunnable() {
            @Override
            public void run() throws Exception {
                mConfigurator.configure();
            }
        });
    }

    public void testDownloadsBackendBinary() throws Exception {
        configureResources();

        when(mFile.exists()).thenReturn(false);
        when(mProvider.getInputStream()).thenReturn(mResourceInput);
        when(mContext.openFileOutput(anyString(), anyInt())).thenReturn(mResourceOutput);
        mConfigurator.configure();

        verifyIsExecutable(mFile);
        MoreAsserts.assertEquals(BINARY_CONTENT.getBytes(), readFile(mOutputFile));
    }

    private void configureResources() throws Exception {
        mOutputFile = File.createTempFile("tw4aTest", ".txt");
        mOutputFile.deleteOnExit();
        mResourceInput = new ByteArrayInputStream(BINARY_CONTENT.getBytes());
        mResourceOutput = new FileOutputStream(mOutputFile);
    }

    public void testSkipsDownloadingWhenBackendAlreadyThere() throws Exception {
        when(mFile.exists()).thenReturn(true);
        mConfigurator.configure();
        verifyZeroInteractions(mProvider);
        verifyIsExecutable(mFile);
    }

    private byte[] readFile(File file) throws IOException {
        return mStreams.readAndClose(new FileInputStream(file)).toByteArray();
    }

}
