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
        try {
            InputStream inputStream = mProvider.getInputStream();
            OutputStream outputStream = mContext.openFileOutput(BACKEND_FILENAME, Context.MODE_PRIVATE);
            copy(inputStream, outputStream);
        }
        catch (IOException e) {
            throw new BackendDownloadException();
        }
    }

    private void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        try {
            mStreams.copy(inputStream, outputStream);
        } catch (IOException e) {
            // TODO: error handling
            throw new RuntimeException(e.toString());
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }

}
