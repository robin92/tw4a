package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.content.Context;

import com.google.inject.Inject;
import pl.rbolanowski.tw4a.backend.*;

public class TaskwarriorBackendFactory implements BackendFactory {

    private static final String TASKWARRIOR_FILENAME = "task";
    private static final String TASKWARRIOR_RC = "taskrc";
    private static final String TASKWARRIOR_DATADIR = "taskdata";

    private Context mContext;
    private TaskwarriorProvider mProvider;

    @Inject
    public TaskwarriorBackendFactory(Context context) {
        mContext = context;
        mProvider = new AssetTaskwarriorProvider(mContext);
    }

    @Override
    public Configurator newConfigurator() {
        return new NativeTaskwarriorConfigurator(mContext, mProvider, getSpec());
    }

    protected static NativeTaskwarriorConfigurator.Spec getSpec() {
        NativeTaskwarriorConfigurator.Spec spec = new NativeTaskwarriorConfigurator.Spec();
        spec.binary = TASKWARRIOR_FILENAME;
        spec.config = TASKWARRIOR_RC;
        spec.dataDir = TASKWARRIOR_DATADIR;
        return spec;
    }

    @Override
    public Database newDatabase() {
        Taskwarrior taskwarrior = new NativeTaskwarrior(mContext, getSpec());
        return new TaskwarriorDatabase(taskwarrior, new JsonTranslator());
    }

}
