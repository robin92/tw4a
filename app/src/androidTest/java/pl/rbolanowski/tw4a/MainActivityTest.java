package pl.rbolanowski.tw4a;

import android.app.Application;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;

import java.io.File;

import com.google.inject.AbstractModule;
import org.junit.*;
import org.junit.runner.RunWith;
import roboguice.RoboGuice;

import pl.rbolanowski.tw4a.backend.*;
import pl.rbolanowski.tw4a.backend.taskwarrior.TaskwarriorBackendFactory;
import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends AndroidMockitoTestCase {

    private class Module extends AbstractModule {

        @Override
        protected void configure() {
            bind(BackendFactory.class).toInstance(mFactoryMock);
        }

    }

    @Rule public ActivityTestRule<MainActivity> mActivityRule = lazyActivityRule();

    private Task[] mTasks = new Task[3];
    private Database mDatabaseMock;
    private BackendFactory mFactoryMock;
    private MainActivity mActivity;

    private static ActivityTestRule<MainActivity> lazyActivityRule() {
        return new ActivityTestRule<>(MainActivity.class, false, false);
    }

    private void overrideModules() {
        Application app = (Application) getTargetContext().getApplicationContext();
        RoboGuice.overrideApplicationInjector(app, new Module());
    }

    private void configureDatabase() {
        mDatabaseMock = mock(Database.class);
        when(mDatabaseMock.select()).thenReturn(mTasks);
    }

    private void configureFactory() {
        BackendFactory factory = new TaskwarriorBackendFactory(getTargetContext());
        mFactoryMock = mock(BackendFactory.class);
        when(mFactoryMock.newConfigurator()).thenReturn(factory.newConfigurator());
        when(mFactoryMock.newDatabase()).thenReturn(mDatabaseMock);
    }

    private void populateTasks() {
        for (int i = 0; i < mTasks.length; i++) {
            mTasks[i] = new Task();
            mTasks[i].uuid = Integer.toString(i);
            mTasks[i].description = "task" + Integer.toString(i);
        }
    }

    @Before public void setUp() {
        populateTasks();
        configureDatabase();
        configureFactory();
        overrideModules();
        startActivity();
    }

    private void startActivity() {
        mActivity = mActivityRule.launchActivity(null);
        assertNotNull(mActivity);
    }

    @Test public void activityLoadsData() {
        onView(withId(android.R.id.progress)).check(matches(isEnabled()));
        onView(withId(android.R.id.list)).check(matches(isDisplayed()));
    }

    @Test public void listViewShowsTasks() {
        final int listId = android.R.id.list;
        onView(withId(listId)).check(matches(isDisplayed()));
        ListView list = (ListView) mActivity.findViewById(listId);
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
