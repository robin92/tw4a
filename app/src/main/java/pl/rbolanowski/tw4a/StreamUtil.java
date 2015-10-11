package pl.rbolanowski.tw4a;

import java.io.*;

public class StreamUtil {

    public void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        ByteArrayOutputStream buffer = read(inputStream);
        buffer.writeTo(outputStream);
    }

    public ByteArrayOutputStream read(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int oneByte = -1;
        while ((oneByte = inputStream.read()) >= 0) {
            buffer.write(oneByte);
        }
        return buffer;
    }

}

