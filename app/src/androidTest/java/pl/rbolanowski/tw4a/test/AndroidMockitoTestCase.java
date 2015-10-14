package pl.rbolanowski.tw4a.test;

import org.junit.After;
import org.junit.Before;

public class AndroidMockitoTestCase extends AndroidTestCase {

    private static final String ENV_DEXMAKER_DEXCACHE = "dexmaker.dexcache";

    @Before public void setUpDexmaker() {
        System.setProperty(ENV_DEXMAKER_DEXCACHE, getTargetContext().getCacheDir().getPath());
    }

    @After public void tearDownDexmaker() {
        System.clearProperty(ENV_DEXMAKER_DEXCACHE);
    }

}

