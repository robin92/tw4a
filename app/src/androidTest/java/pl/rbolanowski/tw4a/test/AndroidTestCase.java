package pl.rbolanowski.tw4a.test;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;

import static android.support.test.espresso.IdlingPolicies.*;
import static pl.rbolanowski.tw4a.test.Constants.*;

public class AndroidTestCase extends Assert {

    static {
        setMasterPolicyTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
    }

    @Before public void setUpContext() {
        assertNotNull(getTargetContext());
    }

    protected static Context getTargetContext() {
        return InstrumentationRegistry.getTargetContext();
    }

}

