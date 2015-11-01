package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.content.Context;
import android.util.Log;

import java.io.*;

import pl.rbolanowski.tw4a.StreamUtil;

public class NativeTaskwarrior implements Taskwarrior {

    private static final String LOG_TAG = NativeTaskwarrior.class.getSimpleName();

    private Context mContext;
    private File mBinary;
    private File mConfig;
    private File mDataDir;
    private String[] mEnvironment;
    private StreamUtil mStreams = new StreamUtil();

    public NativeTaskwarrior(Context context, NativeTaskwarriorConfigurator.Spec spec) {
        mContext = context;
        accessFiles(spec);
        createEnvironment();
    }

    private void accessFiles(NativeTaskwarriorConfigurator.Spec spec) {
        mBinary = get(spec.binary);
        mConfig = get(spec.config);
        mDataDir = get(spec.dataDir);
        assert(mBinary.exists());
        assert(mConfig.exists());
        assert(mDataDir.isDirectory() && mDataDir.exists());
    }

    private File get(String name) {
        return mContext.getFileStreamPath(name);
    }

    private void createEnvironment() {
        mEnvironment = new String[] {
            String.format("TASKRC=%s", mConfig.getAbsolutePath()),
            String.format("TASKDATA=%s", mDataDir.getAbsolutePath()),
        };
    }

    @Override
    public Taskwarrior.Output export() {
        try {
            return execute(mBinary.getAbsolutePath(), "export");
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException("temporary exception: " + e.toString());
        }
    }

    @Override
    public Output put(String description) {
        try {
            return execute(mBinary.getAbsolutePath(), "add", description);
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e.toString());
        }
    }

    private Output execute(String... args) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(args, mEnvironment, mBinary.getParentFile());
        process.waitFor();

        Output out = new Output();
        out.stderr = new String(mStreams.read(process.getErrorStream()).toByteArray());
        out.stdout = new String(mStreams.read(process.getInputStream()).toByteArray());
        return log(args, out);
    }

    private Output log(String[] args, Output output) {
        StringBuilder builder = new StringBuilder();
        append(builder.append("command:\n"), args).append("\n")
            .append("stderr:\n").append(output.stderr).append("\n")
            .append("stdout:\n").append(output.stdout).append("\n");
        Log.d(LOG_TAG, builder.toString());
        return output;
    }

    private static StringBuilder append(StringBuilder builder, String... elements)  {
        for (String arg : elements) {
            builder.append(arg).append(" ");
        }
        return builder;
    }

    protected void clear() {
        clearDirectory(mDataDir);
    }

    private void clearDirectory(File dir) {
        for (String child : dir.list()) {
            new File(dir, child).delete();
        }
    }

}
