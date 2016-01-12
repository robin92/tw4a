package pl.rbolanowski.tw4a;

import java.io.*;

import org.junit.Test;

import static org.junit.Assert.*;
import static pl.rbolanowski.tw4a.StreamUtil.*;

public class StreamUtilTest {

    private final byte[] mBytes = new byte[] { 17, 16, 0, 55, 23, 66, 127, -128, -23, 66 };

    @Test public void readsInputStream() throws Exception {
        assertArrayEquals(mBytes, read(new ByteArrayInputStream(mBytes)).toByteArray());
    }

    @Test public void copiesStream() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        copy(new ByteArrayInputStream(mBytes), outputStream);
        assertArrayEquals(mBytes, outputStream.toByteArray());
    }

}

