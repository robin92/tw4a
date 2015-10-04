package pl.rbolanowski.tw4a.test;

import android.test.AndroidTestCase;

public class AndroidMockitoTestCase extends AndroidTestCase {

    private static final String ENV_DEXMAKER_DEXCACHE = "dexmaker.dexcache";

    @Override
    protected void setUp() throws Exception {
        configureDexmaker();
    }

    private void configureDexmaker() {
        System.setProperty(ENV_DEXMAKER_DEXCACHE, getContext().getCacheDir().getPath());
    }

    protected void assertNotNull(Object... objects) {
        for (Object object : objects) {
            assertNotNull(object);
        }
    }

}

