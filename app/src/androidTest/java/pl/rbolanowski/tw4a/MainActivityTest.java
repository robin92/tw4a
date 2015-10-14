package pl.rbolanowski.tw4a;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.junit.runner.RunWith;

import pl.rbolanowski.tw4a.test.AndroidTestCase;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.IdlingPolicies.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import static pl.rbolanowski.tw4a.test.Constants.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends AndroidTestCase {

    @Rule public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);
    private MainActivity mActivity;

    @Before public void setUp() {
        mActivity = mActivityRule.getActivity();
        assertNotNull(mActivity);
        setMasterPolicyTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
    }

    @Test public void activityLoadsData() {
        onView(withId(android.R.id.progress)).check(matches(isEnabled()));
        onView(withId(android.R.id.text1)).check(matches(isDisplayed()));
    }

    @After public void tearDown() {
        assertResourceReady("task");
    }

    private void assertResourceReady(String name) {
        File file = mActivity.getFileStreamPath(name);
        assertTrue("file " + file.getPath() + " doesn't exist", file.exists());
        assertTrue("file " + file.getPath() + " not executable", file.canExecute());
    }

}

