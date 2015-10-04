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

    public void configure();

}

interface BackendProvider {

    public InputStream getInputStream() throws IOException;

}

class BackendConfiguratorImpl implements BackendConfigurator {

    private static final String BACKEND_FILENAME = "task";

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
    public void configure() {
        try {
            InputStream inputStream = mProvider.getInputStream();
            OutputStream outputStream = mContext.openFileOutput(BACKEND_FILENAME, Context.MODE_PRIVATE);
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
        catch (IOException e) {
            // TODO: error handling
            throw new RuntimeException(e.toString());
        }
    }

}

