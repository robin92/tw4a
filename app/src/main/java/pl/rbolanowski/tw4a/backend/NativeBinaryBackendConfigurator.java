package pl.rbolanowski.tw4a.backend;

import android.content.Context;
import pl.rbolanowski.tw4a.StreamUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NativeBinaryBackendConfigurator implements BackendConfigurator {

    private static final String BACKEND_FILENAME = "task";

    private static class BackendDownloadException extends BackendException {}

    private Context mContext;
    private BackendProvider mProvider;
    private File mTaskFile;
    private StreamUtil mStreams = new StreamUtil();

    public NativeBinaryBackendConfigurator(Context context, BackendProvider provider) {
        mContext = context;
        mProvider = provider;
        mTaskFile = mContext.getFileStreamPath(BACKEND_FILENAME);
    }

    @Override
    public void configure() throws BackendException {
        if (!mTaskFile.exists()) {
            acquireBackend();
        }
        mTaskFile.setExecutable(true);
    }

    private void acquireBackend() throws BackendException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = mProvider.getInputStream();
            outputStream = mContext.openFileOutput(BACKEND_FILENAME, Context.MODE_PRIVATE);
            mStreams.copy(inputStream, outputStream);
        }
        catch (IOException e) {
            throw new BackendDownloadException();
        }
        finally {
            closeQuietly(inputStream);
            closeQuietly(outputStream);
        }
    }

    private void closeQuietly(InputStream stream) {
        if (stream == null) return;
        try {
            stream.close();
        }
        catch (IOException e) {}
    }

    private void closeQuietly(OutputStream stream) {
        if (stream == null) return;
        try {
            stream.close();
        }
        catch (IOException e) {}
    }

}

