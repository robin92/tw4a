package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.content.Context;

import pl.rbolanowski.tw4a.StreamUtil;
import pl.rbolanowski.tw4a.backend.Configurator;

import java.io.*;

public class NativeTaskwarriorConfigurator implements Configurator {

    private static class FatalBackendException extends BackendException {}

    public static class Spec {

        public String binary;
        public String config;
        public String dataDir;

    }

    private Context mContext;
    private TaskwarriorProvider mProvider;
    private Spec mSpec;
    private StreamUtil mStreams = new StreamUtil();

    public NativeTaskwarriorConfigurator(Context context, TaskwarriorProvider provider, Spec spec) {
        mContext = context;
        mProvider = provider;
        mSpec = spec;
    }

    @Override
    public void configure() throws BackendException {
        try {
            configureBinary();
            configureRcFile();
            configureDataDir();
        }
        catch (IOException e) {
            throw new FatalBackendException();
        }
    }

    private void configureBinary() throws IOException {
        File binary = mContext.getFileStreamPath(mSpec.binary);
        if (!binary.exists()) {
            acquireBackend(mSpec.binary);
        }
        binary.setExecutable(true);
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

    private void acquireBackend(String name) throws IOException {
        InputStream inputStream = mProvider.getInputStream();
        OutputStream outputStream = mContext.openFileOutput(name, Context.MODE_PRIVATE);
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

}
