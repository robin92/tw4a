package pl.rbolanowski.tw4a;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import java.io.File;

import org.junit.*;
import org.junit.runner.RunWith;

import pl.rbolanowski.tw4a.test.AndroidTestCase;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityConfiguringTest extends AndroidTestCase {

    @Rule public ActivityTestRule<MainActivity> mActivityRule = lazyActivityRule();

    private MainActivity mActivity;

    private static ActivityTestRule<MainActivity> lazyActivityRule() {
        return new ActivityTestRule<>(MainActivity.class, false, false);
    }

    @Before public void setUp() throws Exception {
        startActivity();
        unlockScreen(mActivity);
    }

    private void startActivity() {
        mActivity = mActivityRule.launchActivity(null);
        assertNotNull(mActivity);
    }

    @Test public void activityLoadsData() {
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
        assertResourceReady("task");
    }

    @Test public void noTasksLoaded() {
        onView(allOf(
                withId(android.R.id.empty),
                withText(R.string.list_empty)))
            .check(matches(isDisplayed()));
    }

    private void assertResourceReady(String name) {
        File file = mActivity.getFileStreamPath(name);
        assertTrue("file " + file.getPath() + " doesn't exist", file.exists());
        assertTrue("file " + file.getPath() + " not executable", file.canExecute());
    }

}
