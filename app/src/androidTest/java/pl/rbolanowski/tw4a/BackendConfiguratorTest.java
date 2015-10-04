package pl.rbolanowski.tw4a;

import android.test.MoreAsserts;
import android.test.mock.MockContext;
import java.io.*;
import java.io.File;
import java.io.FileInputStream;

import pl.rbolanowski.tw4a.BackendConfigurator;
import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static org.mockito.Mockito.*;

public class BackendConfiguratorTest extends AndroidMockitoTestCase {

    private static final String BINARY_CONTENT = "hello world";

    private MockContext mContext;
    private BackendProvider mProvider;
    private BackendConfigurator mConfigurator;
    private StreamUtil mStreams = new StreamUtil();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setUpContext();
        setUpProvider();
        setUpConfigurator();
    }

    private void setUpContext() {
        mContext = mock(MockContext.class);
    }

    private void setUpProvider() throws Exception {
        mProvider = mock(BackendProvider.class);
        when(mProvider.getInputStream()).thenReturn(null);
    }

    private void setUpConfigurator() {
        assertNotNull(mContext, mProvider);
        mConfigurator = new BackendConfiguratorImpl(mContext, mProvider);
    }

    private void assertThrowsBackendException(ThrowingRunnable runnable) throws Exception {
        assertThrows(BackendConfigurator.BackendException.class, runnable);
    }

    public void testGetsPathToBackendBinary() {
        verify(mContext).getFileStreamPath("task");
    }

    public void testDownloadingBackendBinaryFailureHandling() throws Exception {
        when(mProvider.getInputStream()).thenThrow(IOException.class);
        assertThrowsBackendException(new ThrowingRunnable() {
            @Override
            public void run() throws Exception {
                mConfigurator.configure();
            }
        });
    }

    public void testOpeningInternalStorageFailureHandling() throws Exception {
        when(mContext.openFileOutput(anyString(), anyInt())).thenThrow(IOException.class);
        assertThrowsBackendException(new ThrowingRunnable() {
            @Override
            public void run() throws Exception {
                mConfigurator.configure();
            }
        });
    }

    public void testDownloadsBackendBinary() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(BINARY_CONTENT.getBytes());
        when(mProvider.getInputStream()).thenReturn(inputStream);

        File tempFile = File.createTempFile("tw4aTest", ".txt");
        tempFile.deleteOnExit();
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        when(mContext.openFileOutput(anyString(), anyInt())).thenReturn(outputStream);

        mConfigurator.configure();
        verify(mProvider).getInputStream();
        verify(mContext).openFileOutput("task", MockContext.MODE_PRIVATE);

        MoreAsserts.assertEquals(BINARY_CONTENT.getBytes(), readFile(tempFile));
    }

    private byte[] readFile(File file) throws IOException {
        return mStreams.readAndClose(new FileInputStream(file)).toByteArray();
    }

}
