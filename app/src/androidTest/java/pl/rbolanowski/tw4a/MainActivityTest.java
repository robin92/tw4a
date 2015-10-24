package pl.rbolanowski.tw4a;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.junit.runner.RunWith;

import pl.rbolanowski.tw4a.backend.Database;
import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.IdlingPolicies.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import static pl.rbolanowski.tw4a.test.Constants.*;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends AndroidMockitoTestCase {

    @Rule public ActivityTestRule<MainActivity> mActivityRule;
    private MainActivity mActivity;
    private Task[] mTasks = new Task[3];

    public MainActivityTest() {
        configureTasks();
        mActivityRule = new ActivityTestRule<>(MainActivity.class);
    }

    private void configureTasks() {
        for (int i = 0; i < mTasks.length; i++) {
            mTasks[i] = new Task();
            mTasks[i].uuid = Integer.toString(i);
            mTasks[i].description = "task" + Integer.toString(i);
        }
        Database database = mock(Database.class);
        when(database.select()).thenReturn(mTasks);
        DatabaseProvider.getInstance().setDatabase(database);      
    }

    @Before public void setUp() {
        mActivity = mActivityRule.getActivity();
        assertNotNull(mActivity);
        setMasterPolicyTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
    }

    @Test public void activityLoadsData() {
        onView(withId(android.R.id.progress)).check(matches(isEnabled()));
        onView(withId(android.R.id.list)).check(matches(isDisplayed()));
    }

    @Test public void listViewShowsTasks() {
        onView(withId(android.R.id.list)).check(matches(isDisplayed()));
        ListView list = (ListView) mActivity.findViewById(android.R.id.list);
        assertEquals(mTasks.length, list.getChildCount());
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

