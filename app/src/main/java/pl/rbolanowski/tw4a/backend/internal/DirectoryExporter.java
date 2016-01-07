package pl.rbolanowski.tw4a.backend.internal;

import java.io.*;
import pl.rbolanowski.tw4a.backend.Exporter;

public class DirectoryExporter implements Exporter {

    private File mDir;

    public DirectoryExporter(File dir) {
        mDir = dir;
    }

    @Override
    public void export(Handler handler) throws IOException {
        if (!mDir.isDirectory()) throw new IOException("not a directory");
        for (String fileName : mDir.list()) {
            exportFile(fileName, new FileInputStream(new File(mDir, fileName)), handler);
        }
    }

    private void exportFile(String name, InputStream inputStream, Handler handler) throws IOException {
        try {
            handler.onStreamReady(name, inputStream);
        }
        finally {
            inputStream.close();
        }
    }

}

