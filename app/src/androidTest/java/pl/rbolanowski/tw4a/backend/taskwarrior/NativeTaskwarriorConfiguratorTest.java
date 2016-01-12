package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.*;
import org.junit.*;

import pl.rbolanowski.tw4a.Streams;
import pl.rbolanowski.tw4a.backend.Configurator;
import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static org.mockito.Mockito.*;

public class NativeTaskwarriorConfiguratorTest extends AndroidMockitoTestCase {

    private NativeTaskwarriorConfigurator.Spec mSpec = new NativeTaskwarriorConfigurator.Spec();
    private NativeTaskwarriorConfigurator.ResourceProvider mProvider;
    private Configurator mConfigurator;

    @Before public void setUp() throws Exception {
        configureSpec();
        mProvider = mock(NativeTaskwarriorConfigurator.ResourceProvider.class);
        when(mProvider.list("usr/bin")).thenReturn(new String[] {"task"});
        when(mProvider.list("usr/lib")).thenReturn(new String[] {"first", "second"});
        when(mProvider.open(anyString())).thenReturn(new ByteArrayInputStream("content".getBytes()));
        mConfigurator = new NativeTaskwarriorConfigurator(getTargetContext(), mProvider, mSpec);
    }

    @After public void tearDown() throws Exception {
        Context context = getTargetContext();
        deleteIfExists(context.getFileStreamPath(mSpec.config));
        deleteIfExists(context.getFileStreamPath(mSpec.dataDir));
    }

    private static void deleteIfExists(File file) {
        if (!file.exists()) return;
        file.delete();
    }

    private void configureSpec() {
        mSpec.config = "dummyConfig";
        mSpec.dataDir = "dummyDirectory";
    }

    @Test(expected = Configurator.BackendException.class)
    public void openingInternalStorageFailureHandling() throws Exception {
        Context context = mock(Context.class);
        when(context.getFileStreamPath(anyString())).thenReturn(createFile("any"));
        when(context.getFileStreamPath(mSpec.binary)).thenThrow(IOException.class);
        mConfigurator = new NativeTaskwarriorConfigurator(context, mProvider, mSpec);
        mConfigurator.configure();
    }

    @Test public void createsAndConfiguresBinary() throws Exception {
        final String binary = "task";
        mConfigurator.configure();
        assertFileExists(binary);
        assertCanExecute(binary);
        assertTrue(readFile(binary).length > 0);
    }

    @Test public void createsRcFile() throws Exception {
        mConfigurator.configure();
        assertFileExists(mSpec.config);
    }

    @Test(expected = Configurator.BackendException.class)
    public void reportsErrorOnCreatingRcFile() throws Exception {
        Context context = mock(Context.class);
        when(context.getFileStreamPath(anyString())).thenReturn(createFile("any"));
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
        createsAndConfiguresBinary();
        createsAndConfiguresBinary();
        createsAndConfiguresBinary();
        createsAndConfiguresBinary();
    }

    private File createFile(String name) throws Exception {
        File file = getTargetContext().getFileStreamPath(name);
        if (!file.exists()) {
            assertTrue(file.createNewFile());
        }
        return file;
    }

    private byte[] readFile(String name) throws IOException {
        return Streams.read(getTargetContext().openFileInput(name)).toByteArray();
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
