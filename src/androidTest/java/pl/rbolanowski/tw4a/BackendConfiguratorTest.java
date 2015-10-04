package pl.rbolanowski.tw4a;

import android.test.MoreAsserts;
import android.test.mock.MockContext;
import java.io.*;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.lang.StringBuilder;

import pl.rbolanowski.tw4a.BackendProvider;
import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static org.mockito.Mockito.*;

public class BackendConfiguratorTest extends AndroidMockitoTestCase {

    private static final String BINARY_CONTENT = "hello world";

    private MockContext mContext;
    private BackendProvider mProvider;
    private BackendConfigurator mConfigurator;

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

    private void setUpProvider() {
        mProvider = mock(BackendProvider.class);
        when(mProvider.getInputStream()).thenReturn(null);
    }

    private void setUpConfigurator() {
        assertNotNull(mContext, mProvider);
        mConfigurator = new BackendConfiguratorImpl(mContext, mProvider);
    }

    public void testGetsPathToBackendBinary() {
        verify(mContext).getFileStreamPath("task");
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
        FileInputStream inputStream = new FileInputStream(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int b = 0;
        while ((b = inputStream.read()) >= 0) {
            outputStream.write(b);
        }
        inputStream.close();
        outputStream.close();
        return outputStream.toByteArray();
    }

}
 
