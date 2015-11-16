package pl.rbolanowski.tw4a.test;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.view.WindowManager;

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

    protected void unlockScreen(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            }
        });
    }

    protected static Context getTargetContext() {
        return InstrumentationRegistry.getTargetContext();
    }

}

