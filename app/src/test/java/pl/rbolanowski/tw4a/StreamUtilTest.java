package pl.rbolanowski.tw4a;

import java.io.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StreamUtilTest {

    private StreamUtil mStreams = new StreamUtil();
    private final byte[] mBytes = new byte[] { 17, 16, 0, 55, 23, 66, 127, -128, -23, 66 };

    @Before public void setUp() {
        assertNotNull(mStreams);
    }

    @Test public void readsInputStream() throws Exception {
        assertArrayEquals(mBytes, mStreams.read(new ByteArrayInputStream(mBytes)).toByteArray());
    }

    @Test public void copiesStream() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mStreams.copy(new ByteArrayInputStream(mBytes), outputStream);
        assertArrayEquals(mBytes, outputStream.toByteArray());
    }

}

