package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.content.Context;
import android.util.Log;

import java.io.*;

import pl.rbolanowski.tw4a.StreamUtil;

public class NativeTaskwarrior implements Taskwarrior {

    private static final String LOG_TAG = NativeTaskwarrior.class.getSimpleName();
    private static final String BINARY = "task";
    private static final String DATADIR = "taskdata";
    private static final String[] ENVIRONMENT = new String[] { "TASKRC=taskrc", String.format("TASKDATA=%s", DATADIR) };

    private Context mContext;
    private File mBinary;
    private StreamUtil mStreams = new StreamUtil();

    public NativeTaskwarrior(Context context) {
        mContext = context;
        mBinary = mContext.getFileStreamPath(BINARY);
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
        Process process = Runtime.getRuntime().exec(args, ENVIRONMENT, mBinary.getParentFile());
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
        File dataDir = mContext.getFileStreamPath(DATADIR);
        if (!dataDir.isDirectory()) return;
        clearDirectory(dataDir);
    }

    private void clearDirectory(File dir) {
        for (String child : dir.list()) {
            new File(dir, child).delete();
        }
    }

}
