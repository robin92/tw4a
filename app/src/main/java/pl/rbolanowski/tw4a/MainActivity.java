package pl.rbolanowski.tw4a;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.net.URL;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

}

class BackendConfigurator {

    private static final String BACKEND_FILENAME = "task";

    private Context mContext;
    private File mTaskFile;

    public BackendConfigurator(Context context) {
        mContext = context;
        mTaskFile = mContext.getFileStreamPath(BACKEND_FILENAME);
    }

}

