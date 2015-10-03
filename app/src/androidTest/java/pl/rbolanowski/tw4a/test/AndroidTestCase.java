package pl.rbolanowski.tw4a.test;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;

public class AndroidTestCase extends Assert {

    private Context mTargetContext;

    @Before public void setUpContext() {
        mTargetContext = InstrumentationRegistry.getTargetContext();
        assertNotNull(mTargetContext);
    }

    protected Context getTargetContext() {
        return mTargetContext;
    }

}

