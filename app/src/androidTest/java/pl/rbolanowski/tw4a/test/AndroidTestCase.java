package pl.rbolanowski.tw4a.test;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;

public class AndroidTestCase extends Assert {

    @Before public void setUpContext() {
        assertNotNull(getTargetContext());
    }

    protected static Context getTargetContext() {
        return InstrumentationRegistry.getTargetContext();
    }

}

