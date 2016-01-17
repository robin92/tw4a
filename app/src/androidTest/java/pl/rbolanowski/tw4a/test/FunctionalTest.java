package pl.rbolanowski.tw4a.test;

import android.content.Context;

import java.io.File;

import org.junit.*;

import pl.rbolanowski.tw4a.MainActivity;
import pl.rbolanowski.tw4a.backend.*;
import pl.rbolanowski.tw4a.backend.taskwarrior.TaskwarriorBackendFactory;
import pl.rbolanowski.tw4a.test.AndroidTestCase;
import pl.rbolanowski.tw4a.test.AnimationDisabledRule;

public class FunctionalTest extends AndroidTestCase {

    @ClassRule public static final AnimationDisabledRule mAnimationDisabled = new AnimationDisabledRule();

    protected Context mContext;
    protected BackendFactory mBackend;    // cant inject (no context yet)

    @Before public void setUp() throws Exception {
        mContext = getTargetContext();
        clearDirectories();
        createBackend();
        configureBackend();
        populateDatabase();
    }

    private void createBackend() throws  Exception {
        mBackend = new TaskwarriorBackendFactory(mContext);
    }

    private void configureBackend() throws Exception {
        mBackend.newConfigurator().configure();
    }

    private void populateDatabase() throws Exception {}

    @After public void tearDown() {
        clearDirectories();
    }

    private void clearDirectories() {
        clearDirectory(mContext.getFileStreamPath("taskdata"));
        clearDirectory(mContext.getCacheDir());
    }

    private static void clearDirectory(File dir) {
        for (String child : dir.list()) {
            new File(dir, child).delete();
        }
    }

}

