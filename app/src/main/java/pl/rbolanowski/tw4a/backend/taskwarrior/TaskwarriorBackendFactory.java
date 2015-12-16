package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import com.google.inject.Inject;

import pl.rbolanowski.tw4a.backend.*;

public class TaskwarriorBackendFactory implements BackendFactory {

    private static final String TASKWARRIOR_FILENAME = "task";
    private static final String TASKWARRIOR_RC = "taskrc";
    private static final String TASKWARRIOR_DATADIR = "taskdata";

    private class AssetResourceProvider implements NativeTaskwarriorConfigurator.ResourceProvider {

        @Override
        public String[] list(String dir) {
            String[] elements = new String[0];
            try {
                elements = mContext.getAssets().list(dir);
            }
            catch (IOException e) {
                // not relevant
            }
            finally {
                return elements;
            }
        }

        @Override
        public InputStream open(String name) {
            InputStream stream = null;
            try {
                stream = mContext.getAssets().open(name);
            }
            catch (IOException e) {
                // not relevant
            }
            finally {
                return stream;
            }
        }

    }

    private Context mContext;

    @Inject
    public TaskwarriorBackendFactory(Context context) {
        mContext = context;
    }

    @Override
    public Configurator newConfigurator() {
        return new NativeTaskwarriorConfigurator(mContext, new AssetResourceProvider(), getSpec());
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
        return new TaskwarriorDatabase(new JsonParser(), taskwarrior, new Translator());
    }

}

