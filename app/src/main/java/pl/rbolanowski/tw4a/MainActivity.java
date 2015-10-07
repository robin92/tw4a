package pl.rbolanowski.tw4a;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onStart() {
        super.onStart();
        new ResourceLoadingAsyncTask(
            findViewById(android.R.id.progress), findViewById(android.R.id.text1)).execute();
    }

}

class ResourceLoadingAsyncTask extends AsyncTask<Void, Void, Void> {

    private View mLoadingView;
    private View mReadyView;

    public ResourceLoadingAsyncTask(View loadingView, View readyView) {
        mLoadingView = loadingView;
        mReadyView = readyView;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void someVoid) {
        int visibility = mLoadingView.getVisibility();
        mLoadingView.setVisibility(mReadyView.getVisibility());
        mReadyView.setVisibility(visibility);
    }

}
