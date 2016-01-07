package pl.rbolanowski.tw4a.backend.internal;

import java.io.*;
import org.junit.*;
import pl.rbolanowski.tw4a.StreamUtil;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DirectoryExporterTest implements DirectoryExporter.Handler {

    private static final String PREFIX = "tw4a";
    private static final String SUFFIX = DirectoryExporterTest.class.getSimpleName();

    private static interface Matcher<T> {

        boolean match(T elem);

    }

    private StreamUtil mStreams = new StreamUtil();
    private File mDir;
    private File[] mFiles;

    @Override
    public void onStreamReady(String name, InputStream inputStream) throws IOException {
        File file = find(matchByName(name), mFiles);
        assertNotNull(file);
        assertEquals(file.getAbsolutePath(), new String(mStreams.read(inputStream).toByteArray()));
    }

    private static <T> T find(Matcher<T> matcher, T... elements) {
        for (T elem : elements) {
            if (matcher.match(elem)) return elem;
        }
        return null;
    }

    private static Matcher<File> matchByName(final String name) {
        return new Matcher<File>() {
            @Override
            public boolean match(File file) {
                return name.equals(file.getName());
            }
        };
    }

    @Before public void createTemporaryFiles() throws Exception {
        mDir = createTemporaryDirectory();
        mFiles = createTemporaryFiles(3);
    }

    private static File createTemporaryDirectory() throws Exception {
        File temp = File.createTempFile(PREFIX, SUFFIX);
        temp.delete();
        temp.mkdir();
        return temp;
    }

    private File[] createTemporaryFiles(int count) throws Exception {
        File[] files = new File[count];
        for (int i = 0; i < files.length; i++) {
            files[i] = File.createTempFile(PREFIX, SUFFIX, mDir);
            FileOutputStream outputStream = new FileOutputStream(files[i]);
            try {
                outputStream.write(files[i].getAbsolutePath().getBytes());
            }
            finally {
                outputStream.close();
            }
        }
        return files;
    }

    @Test(expected = NullPointerException.class)
    public void reportsErrorOnUsingNullFile() throws Exception {
        new DirectoryExporter(null).export(this);
    }

    @Test(expected = NullPointerException.class)
    public void reportsErrorOnUsingNullHandler() throws Exception {
        File dir = mock(File.class);
        when(dir.isDirectory()).thenReturn(true);
        new DirectoryExporter(dir).export(null);
    }

    @Test(expected = IOException.class)
    public void supportsOnlyDirectories() throws Exception {
        File file = mock(File.class);
        when(file.isDirectory()).thenReturn(false);
        new DirectoryExporter(file).export(null);
    }

    @Test public void exportsFileFromDirectory() throws Exception {
        new DirectoryExporter(mDir).export(this);
    }

    @After public void clearTemporaryFiles() {
        for (File file : mFiles) file.delete();
        mDir.delete();
    }

}

