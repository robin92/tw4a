package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.content.Context;
import android.util.Log;

import pl.rbolanowski.tw4a.StreamUtil;
import pl.rbolanowski.tw4a.backend.Configurator;

import java.io.*;

public class NativeTaskwarriorConfigurator implements Configurator {

    private static class FatalBackendException extends BackendException {}

    private static interface Action {

        void perform(File file);

    }

    private static class MakeExecutableAction implements Action {

        @Override
        public void perform(File file) {
            file.setExecutable(true);
        }

    }

    public static class Spec {

        public String binary;
        public String config;
        public String dataDir;

    }

    public static interface ResourceProvider {

        String[] list(String dir);

        InputStream open(String name);

    }

    private static final String LOG_TAG = NativeTaskwarriorConfigurator.class.getSimpleName();

    // note lack of root and ending slash
    private static final String BIN_PREFIX = "usr/bin";
    private static final String LIB_PREFIX = "usr/lib";

    private Context mContext;
    private Spec mSpec;
    private ResourceProvider mResource;
    private StreamUtil mStreams = new StreamUtil();
    private Action mMakeExecutable = new MakeExecutableAction();

    public NativeTaskwarriorConfigurator(Context context, ResourceProvider resource, Spec spec) {
        mContext = context;
        mResource = resource;
        mSpec = spec;
    }

    @Override
    public void configure() throws BackendException {
        try {
            fetchAssets(LIB_PREFIX);
            fetchAssets(BIN_PREFIX, mMakeExecutable);
            configureRcFile();
            configureDataDir();
        }
        catch (IOException e) {
            throw new FatalBackendException();
        }
    }

    private void fetchAssets(String dir) throws IOException {
        fetchAssets(dir, null);
    }

    private void fetchAssets(String dir, Action action) throws IOException {
        String[] names = mResource.list(dir);
        for (String name : names) {
            String assetName = joinPath(dir, name);
            File assetFile = fetchAsset(assetName, name);
            if (action != null) {
                action.perform(assetFile);
            }
        }
    }

    private static String joinPath(String... elements) {
        if (elements.length < 1) return null;
        StringBuilder builder = new StringBuilder(elements[0]);
        for (int i = 1; i < elements.length; i++) {
            builder.append("/").append(elements[i]);
        }
        return builder.toString();
    }

    private File fetchAsset(String assetName, String name) throws IOException {
        Log.d(LOG_TAG, String.format("fetching %s as %s", assetName, name));
        File asset = mContext.getFileStreamPath(name);
        if (!asset.exists()) {
            copyAsset(assetName, name);
        }
        return asset;
    }

    private void copyAsset(String src, String targetName) throws IOException {
        InputStream inputStream = mResource.open(src);
        OutputStream outputStream = mContext.openFileOutput(targetName, Context.MODE_PRIVATE);
        copy(inputStream, outputStream);
    }

    private void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        try {
            mStreams.copy(inputStream, outputStream);
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }

    private void configureRcFile() throws IOException {
        mContext.getFileStreamPath(mSpec.config).createNewFile();
    }

    private void configureDataDir() throws BackendException {
        File dataDir = mContext.getFileStreamPath(mSpec.dataDir);
        if (!existsAsDirectory(dataDir) && !dataDir.mkdir()) {
            throw new FatalBackendException();
        }
    }

    private static boolean existsAsDirectory(File file) {
        return file.exists() && file.isDirectory();
    }

}

