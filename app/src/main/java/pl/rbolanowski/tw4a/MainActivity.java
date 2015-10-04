package pl.rbolanowski.tw4a;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.io.*;
import java.net.URL;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

}

interface BackendConfigurator {

    public static abstract class BackendException extends Exception {}

    public void configure() throws BackendException;

}

interface BackendProvider {

    public InputStream getInputStream() throws IOException;

}

class BackendConfiguratorImpl implements BackendConfigurator {

    private static final String BACKEND_FILENAME = "task";

    private static class BackendDownloadException extends BackendException {}

    private Context mContext;
    private BackendProvider mProvider;
    private File mTaskFile;
    private StreamUtil mStreams = new StreamUtil();

    public BackendConfiguratorImpl(Context context, BackendProvider provider) {
        mContext = context;
        mProvider = provider;
        mTaskFile = mContext.getFileStreamPath(BACKEND_FILENAME);
    }

    @Override
    public void configure() throws BackendException {
        if (!mTaskFile.exists()) {
            acquireBackend();
        }
        assert mTaskFile.exists();
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
            mStreams.close(inputStream);
            mStreams.close(outputStream);
        }
    }

}

