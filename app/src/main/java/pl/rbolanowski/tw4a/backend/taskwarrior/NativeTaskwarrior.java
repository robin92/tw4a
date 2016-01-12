package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.*;
import java.util.Vector;

import pl.rbolanowski.tw4a.Streams;

public class NativeTaskwarrior implements Taskwarrior {

    private static final String LOG_TAG = NativeTaskwarrior.class.getSimpleName();

    private Context mContext;
    private File mBinary;
    private File mConfig;
    private File mDataDir;
    private String[] mEnvironment;

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
            String.format("LD_LIBRARY_PATH=%s", mConfig.getParentFile()),
            String.format("TASKRC=%s", mConfig.getAbsolutePath()),
            String.format("TASKDATA=%s", mDataDir.getAbsolutePath()),
        };
    }

    @Override
    public Taskwarrior.Output export() {
        return executeWithRuntimeException(new CommandBuilder(Command.Export).build());
    }

    @Override
    public Output put(@NonNull String description) {
        return executeWithRuntimeException(new CommandBuilder(Command.Add).appendArg(description).build());
    }

    @Override
    public Output modify(@NonNull String uuid, String description, InternalTask.Status status) {
        String args[] = new CommandBuilder(Command.Modify)
            .setFilter("uuid", uuid)
            .appendArg("status", status)
            .appendArg(description)
            .build();
        return executeWithRuntimeException(args);
    }

    private Output executeWithRuntimeException(String... args) {
        try {
            return execute(args);
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e.toString());
        }
    }

    private Output execute(String... args) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(prepend(mBinary.getAbsolutePath(), args), mEnvironment, mBinary.getParentFile());
        process.waitFor();

        Output out = new Output();
        out.stderr = new String(Streams.read(process.getErrorStream()).toByteArray());
        out.stdout = new String(Streams.read(process.getInputStream()).toByteArray());
        return log(args, out);
    }

    private static String[] prepend(String first, String[] second) {
        String[] merged = new String[1 + second.length];
        merged[0] = first;
        for (int i = 0; i < second.length; i++) merged[1 + i] = second[i];
        return merged;
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

enum Command {

    Add     ("add"),
    Export  ("export"),
    Modify  ("modify");

    private String mRepr;

    Command(String repr) {
        mRepr = repr;
    }

    @Override
    public String toString() { return mRepr; }

}

class CommandBuilder {

    private static class Pair {

        String name;
        String value;

        public Pair(String name, String value) {
            this.name = name;
            this.value = value;
        }

    }

    private Command mCommand;
    private Pair mFilter;
    private Vector<Pair> mArgs = new Vector<>();

    public CommandBuilder(Command command) {
        mCommand = command;
    }

    public CommandBuilder setFilter(String name, Object value) {
        mFilter = new Pair(name, value.toString());
        return this;
    }

    public CommandBuilder appendArg(Object value) {
        return appendArg(null, value);
    }

    public CommandBuilder appendArg(String name, Object value) {
        if (value != null) {
            mArgs.add(new Pair(name, value.toString()));
        }
        return this;
    }

    public String[] build() {
        Vector<String> args = new Vector<>();
        if (mFilter != null) args.add(translate(mFilter));
        args.add(mCommand.toString());
        args.addAll(translateAll(mArgs));
        return args.toArray(new String[0]);
    }

    private static String translate(Pair pair) {
        return pair.name == null ? pair.value : pair.name + ":\"" + pair.value + "\"";
    }

    private static Vector<String> translateAll(Vector<Pair> pairs) {
        Vector<String> result = new Vector<>();
        for (Pair pair : pairs) {
            result.add(translate(pair));
        }
        return result;
    }

}
