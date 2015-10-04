package pl.rbolanowski.tw4a.test;

import android.test.AndroidTestCase;

public class AndroidMockitoTestCase extends AndroidTestCase {

    private static final String ENV_DEXMAKER_DEXCACHE = "dexmaker.dexcache";

    public interface ThrowingRunnable {

        void run() throws Exception;

    }

    @Override
    protected void setUp() throws Exception {
        configureDexmaker();
    }

    private void configureDexmaker() {
        System.setProperty(ENV_DEXMAKER_DEXCACHE, getContext().getCacheDir().getPath());
    }

    protected static void assertNotNull(Object... objects) {
        for (Object object : objects) {
            assertNotNull(object);
        }
    }

    protected static void assertThrows(Class excClass, ThrowingRunnable runnable) throws Exception {
        boolean catched = false;
        try {
            runnable.run();
        }
        catch (Exception e) {
            if (excClass.isAssignableFrom(e.getClass())) {
                catched = true;
            }
            else {
                throw e;
            }
        }
        assertTrue(catched);
    }

}

