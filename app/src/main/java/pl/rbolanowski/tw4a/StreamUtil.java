package pl.rbolanowski.tw4a;

import java.io.*;

public class StreamUtil {

    private static final int BUFFER_SIZE = 16 * 1024;

    public void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte buffer[] = new byte[BUFFER_SIZE];
        int bytesRead = -1, totalBytesRead = 0;
        while ((bytesRead = inputStream.read(buffer)) >= 0) {
            totalBytesRead += bytesRead;
            outputStream.write(buffer, 0, bytesRead);
        }
    }

    public ByteArrayOutputStream read(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        copy(inputStream, outputStream);
        return outputStream;
    }

}

