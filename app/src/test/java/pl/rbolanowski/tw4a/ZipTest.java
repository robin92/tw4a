package pl.rbolanowski.tw4a;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.*;

import org.junit.Test;

import static org.junit.Assert.*;
import static pl.rbolanowski.tw4a.Streams.*;
import static pl.rbolanowski.tw4a.Zip.*;

public class ZipTest {

    private final byte[] mBytes = new byte[] { 17, 16, 0, 55, 23, 66, 127, -128, -23, 66 };

    private class UnzipHandlerImpl implements UnzipHandler {

        @Override
        public void onEntry(ZipEntry entry, InputStream inputStream) throws IOException {
            mUnzippedEntries.put(entry.getName(), read(inputStream).toByteArray());
        }

    }

    private Map<String, byte[]> mUnzippedEntries = new HashMap<>();

    @Test public void zipsAndUnzips() throws Exception {
        assertZipped("nothing", new byte[0]);
        assertEquals(1, mUnzippedEntries.size());
        assertZipped("any", mBytes);
        assertEquals(2, mUnzippedEntries.size());
    }

    private void assertZipped(String name, byte[] data) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        zip(outputStream, new EntryHint(name, new ByteArrayInputStream(data)));
        outputStream.close();

        unzip(new ByteArrayInputStream(outputStream.toByteArray()), new UnzipHandlerImpl());

        assertArrayEquals(data, mUnzippedEntries.get(name));
    }

    @Test public void zipsAndUnzipsMultipleElements() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        zip(
            outputStream,
            new EntryHint("first", new ByteArrayInputStream(mBytes)),
            new EntryHint("second", new ByteArrayInputStream(mBytes)),
            new EntryHint("third", new ByteArrayInputStream(mBytes))
        );
        outputStream.close();

        unzip(new ByteArrayInputStream(outputStream.toByteArray()), new UnzipHandlerImpl());
        assertUnzipped(mUnzippedEntries, mBytes, "first", "second", "third");

        assertUnzipped(
            unzip(new ByteArrayInputStream(outputStream.toByteArray())),
            mBytes,
            "first", "second", "third"
        );
    }

    private static void assertUnzipped(Map<String, byte[]> data, byte[] expected, String... names) {
        assertEquals(names.length, data.size());
        for (String name : names) {
            assertArrayEquals(expected, data.get(name));
        }
    }

}

