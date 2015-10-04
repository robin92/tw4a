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

    public InputStream getInputStream();

}

class BackendConfiguratorImpl implements BackendConfigurator {

    private static final String BACKEND_FILENAME = "task";

    private Context mContext;
    private BackendProvider mProvider;
    private File mTaskFile;

    public BackendConfiguratorImpl(Context context, BackendProvider provider) {
        mContext = context;
        mProvider = provider;
        mTaskFile = mContext.getFileStreamPath(BACKEND_FILENAME);
    }

    @Override
    public void configure() {
        try {
            ByteArrayOutputStream backendBinary = readStream(mProvider.getInputStream());
            FileOutputStream outputStream = mContext.openFileOutput(BACKEND_FILENAME, Context.MODE_PRIVATE);
            backendBinary.writeTo(outputStream);
            outputStream.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    ByteArrayOutputStream readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int oneByte = -1;
        while ((oneByte = inputStream.read()) > 0) {
            buffer.write(oneByte);
        }
        inputStream.close();
        return buffer;
    }

}

