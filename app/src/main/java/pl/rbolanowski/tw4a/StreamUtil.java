package pl.rbolanowski.tw4a;

import java.io.*;

// TODO: test suite
public class StreamUtil {

    public void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        ByteArrayOutputStream buffer = read(inputStream);
        buffer.writeTo(outputStream);
    }

    public ByteArrayOutputStream readAndClose(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = read(inputStream);
        inputStream.close();
        return buffer;
    }

    public ByteArrayOutputStream read(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int oneByte = -1;
        while ((oneByte = inputStream.read()) > 0) {
            buffer.write(oneByte);
        }
        return buffer;
    }

    public void close(InputStream... streams) throws IOException {
        for (InputStream stream : streams) {
            stream.close();
        }
    }

    public void close(OutputStream... streams) throws IOException {
        for (OutputStream stream : streams) {
            stream.close();
        }
    }

}
