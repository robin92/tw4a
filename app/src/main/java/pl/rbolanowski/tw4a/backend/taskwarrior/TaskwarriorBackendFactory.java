package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.content.Context;
import java.net.MalformedURLException;
import java.net.URL;

import pl.rbolanowski.tw4a.backend.*;

public class TaskwarriorBackendFactory implements BackendFactory {

    private static final String TASKWARRIOR_URL_STR = "https://dl.dropboxusercontent.com/u/90959340/tw4a/2.4.4/armeabi/task";
    private static final String TASKWARRIOR_FILENAME = "task";
    private static final String TASKWARRIOR_RC = "taskrc";
    private static final String TASKWARRIOR_DATADIR = "taskdata";

    private Context mContext;
    private TaskwarriorProvider mProvider;

    public TaskwarriorBackendFactory(Context context) {
        mContext = context;
        try {
            mProvider = new UrlTaskwarriorProvider(new URL(TASKWARRIOR_URL_STR));
        }
        catch (MalformedURLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Configurator newConfigurator() {
        NativeTaskwarriorConfigurator.Spec spec = new NativeTaskwarriorConfigurator.Spec();
        spec.binary = TASKWARRIOR_FILENAME;
        spec.config = TASKWARRIOR_RC;
        spec.dataDir = TASKWARRIOR_DATADIR;
        return new NativeTaskwarriorConfigurator(mContext, mProvider, spec);
    }

    @Override
    public Database newDatabase() {
        Taskwarrior taskwarrior = new NativeTaskwarrior(mContext);
        return new TaskwarriorDatabase(taskwarrior, new JsonTranslator());
    }

}
