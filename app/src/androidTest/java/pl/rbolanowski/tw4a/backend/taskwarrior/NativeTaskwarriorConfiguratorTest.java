package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.content.Context;
import java.io.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.rbolanowski.tw4a.StreamUtil;
import pl.rbolanowski.tw4a.backend.Configurator;
import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static org.mockito.Mockito.*;

public class NativeTaskwarriorConfiguratorTest extends AndroidMockitoTestCase {

    private static final String BINARY_CONTENT = "hello world";

    private TaskwarriorProvider mProvider;
    private NativeTaskwarriorConfigurator.Spec mSpec = new NativeTaskwarriorConfigurator.Spec();
    private Configurator mConfigurator;
    private StreamUtil mStreams = new StreamUtil();

    @Before public void setUp() throws Exception {
        configureProvider();
        configureSpec();
        mConfigurator = new NativeTaskwarriorConfigurator(getTargetContext(), mProvider, mSpec);
    }

    @After public void tearDown() throws Exception {
        Context context = getTargetContext();
        deleteIfExists(context.getFileStreamPath(mSpec.binary));
        deleteIfExists(context.getFileStreamPath(mSpec.config));
        deleteIfExists(context.getFileStreamPath(mSpec.dataDir));
    }

    private static void deleteIfExists(File file) {
        if (!file.exists()) return;
        file.delete();
    }

    private void configureSpec() {
        mSpec.binary = "dummyBinary";
        mSpec.config = "dummyConfig";
        mSpec.dataDir = "dummyDirectory";
    }

    private void configureProvider() throws Exception {
        mProvider = mock(TaskwarriorProvider.class);
        when(mProvider.getInputStream())
            .thenReturn(new ByteArrayInputStream(BINARY_CONTENT.getBytes()));
    }

    @Test(expected = Configurator.BackendException.class)
    public void downloadingBackendBinaryFailureHandling() throws Exception {
        reset(mProvider);
        when(mProvider.getInputStream()).thenThrow(IOException.class);
        mConfigurator.configure();
    }

    @Test(expected = Configurator.BackendException.class)
    public void openingInternalStorageFailureHandling() throws Exception {
        Context context = mock(Context.class);
        when(context.getFileStreamPath(mSpec.binary)).thenThrow(IOException.class);
        mConfigurator = new NativeTaskwarriorConfigurator(context, mProvider, mSpec);
        mConfigurator.configure();
    }

    @Test public void downloadsBackendBinary() throws Exception {
        mConfigurator.configure();
        assertFileExists(mSpec.binary);
        assertCanExecute(mSpec.binary);
        assertArrayEquals(BINARY_CONTENT.getBytes(), readFile(mSpec.binary));
    }

    @Test public void skipsDownloadingWhenBackendAlreadyThere() throws Exception {
        createFile(mSpec.binary);
        mConfigurator.configure();
        verifyZeroInteractions(mProvider);
        assertCanExecute(mSpec.binary);
    }

    @Test public void createsRcFile() throws Exception {
        mConfigurator.configure();
        assertFileExists(mSpec.config);
    }

    @Test(expected = Configurator.BackendException.class)
    public void reportsErrorOnCreatingRcFile() throws Exception {
        Context context = mock(Context.class);
        when(context.getFileStreamPath(mSpec.binary)).thenReturn(createFile(mSpec.binary));
        when(context.getFileStreamPath(mSpec.config)).thenThrow(IOException.class);
        mConfigurator = new NativeTaskwarriorConfigurator(context, mProvider, mSpec);
        mConfigurator.configure();
    }

    @Test public void createsDataDir() throws Exception {
        mConfigurator.configure();
        assertFileExists(mSpec.dataDir);
        assertIsDirectory(mSpec.dataDir);
    }

    @Test(expected = Configurator.BackendException.class)
    public void reportsErrorWhenCreatingDataDirFails() throws Exception {
        createFile(mSpec.dataDir);  // creating file, expected dir
        mConfigurator.configure();
    }

    @Test public void runsSeveralTimesInRow() throws Exception {
        downloadsBackendBinary();
        downloadsBackendBinary();
        downloadsBackendBinary();
        downloadsBackendBinary();
    }

    private File createFile(String name) throws Exception {
        File file = getTargetContext().getFileStreamPath(name);
        assertTrue(file.createNewFile());
        return file;
    }

    private byte[] readFile(String name) throws IOException {
        return mStreams.read(getTargetContext().openFileInput(name)).toByteArray();
    }

    private static void assertFileExists(String name) {
        File file = getTargetContext().getFileStreamPath(name);
        assertTrue("doesn't exist", file.exists());
    }

    private static void assertCanExecute(String name) {
        File file = getTargetContext().getFileStreamPath(name);
        assertTrue("not executable", file.canExecute());
    }

    private static void assertIsDirectory(String name) {
        File file = getTargetContext().getFileStreamPath(name);
        assertTrue("is not a directory", file.isDirectory());
    }

}
