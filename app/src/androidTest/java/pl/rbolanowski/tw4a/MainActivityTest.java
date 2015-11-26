package pl.rbolanowski.tw4a;

import android.content.Context;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;
import android.content.pm.ActivityInfo;

import java.io.File;

import org.junit.*;
import org.junit.runner.RunWith;

import pl.rbolanowski.tw4a.backend.*;
import pl.rbolanowski.tw4a.backend.taskwarrior.TaskwarriorBackendFactory;
import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import static org.hamcrest.Matchers.*;

import static pl.rbolanowski.tw4a.test.matchers.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends AndroidMockitoTestCase {

    private static final int TOTAL_TASK_COUNT = 3;

    @Rule public ActivityTestRule<MainActivity> mActivityRule = lazyActivityRule();

    private Context mContext;
    private BackendFactory mBackend;    // cant inject (no context yet)
    private MainActivity mActivity;

    private static ActivityTestRule<MainActivity> lazyActivityRule() {
        return new ActivityTestRule<>(MainActivity.class, false, false);
    }

    @Before public void setUp() throws Exception {
        mContext = getTargetContext();
        createBackend();
        configureBackend();
        populateDatabase();
        startActivity();
        unlockScreen(mActivity);
    }

    private void createBackend() throws  Exception {
        mBackend = new TaskwarriorBackendFactory(mContext);
    }

    private void configureBackend() throws Exception {
        mBackend.newConfigurator().configure();
    }

    private void populateDatabase() throws Exception{
        Database database = mBackend.newDatabase();
        for (int i = 0; i < TOTAL_TASK_COUNT; i++) {
            Task task = new Task();
            task.description = String.format("dummy task, number %d", i);
            database.insert(task);
        }
    }

    private void startActivity() {
        mActivity = mActivityRule.launchActivity(null);
        assertNotNull(mActivity);
    }

    @Test public void listViewShowsTasks() {
        onView(withId(android.R.id.list))
            .check(matches(allOf(
                isDisplayed(),
                withListSize(TOTAL_TASK_COUNT)
            )));
    }

    @Test public void changesOrientation() {
        swapOrientation(Orientation.Portrait);
        listViewShowsTasks();
        swapOrientation(Orientation.Landscape);
        listViewShowsTasks();
    }

    private void swapOrientation(Orientation orientation) {
        mActivity.setRequestedOrientation(orientation.getRaw());
    }

    @Test public void contextMenuVisibleAferLongClick() {
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(0).perform(longClick());
        onView(withText(R.string.menu_done)).check(matches(isDisplayed()));
        onView(withText(R.string.menu_edit)).check(matches(isDisplayed()));
    }

    @Test public void enableDisableButtonsInDialog() {
        onView(withId(R.id.add_button)).perform(click());
        onView(withText(R.string.cancel)).check(matches(isEnabled()));
        onView(withText(R.string.add)).check(matches(not(isEnabled())));

        onView(withId(R.id.new_task_description)).perform(typeText(" "), closeSoftKeyboard());
        onView(withText(R.string.add)).check(matches(not(isEnabled())));

        onView(withId(R.id.new_task_description)).perform(typeText("A"), closeSoftKeyboard());
        onView(withText(R.string.add)).check(matches(isEnabled()));

        onView(withId(R.id.new_task_description)).perform(clearText(), closeSoftKeyboard());
        onView(withText(R.string.add)).check(matches(not(isEnabled())));
    }

    @Test public void afterAddNewTaskListIsLonger() {
        addTaskWithDialog("task n");
        onView(withId(android.R.id.list)).check(matches(withListSize(TOTAL_TASK_COUNT + 1)));
    }

    @Test public void settingTaskAsDoneRemovesIt() {
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(0).perform(longClick());
        onView(withText(R.string.menu_done)).perform(click());
        onView(withId(android.R.id.list)).check(matches(withListSize(TOTAL_TASK_COUNT - 1)));
    }

    @Test public void searchWithActionBar() {
        onView(withId(R.id.action_search)).perform(typeText("2"), submit());
        onView(withId(android.R.id.list)).check(matches(withListSize(1)));

        onView(isAssignableFrom(EditText.class)).perform(clearText(), submit(), closeSoftKeyboard());
        onView(withId(android.R.id.list)).check(matches(withListSize(TOTAL_TASK_COUNT)));
    }

    @Test public void searchWithNoResultsDisplaysEmptyView() {
        onView(withId(android.R.id.empty)).check(matches(not(isDisplayed())));
        onView(withId(R.id.action_search)).perform(typeText("not present"), submit(), closeSoftKeyboard());
        onView(allOf(withId(android.R.id.empty), withText(R.string.list_empty))).check(matches(isDisplayed()));
    }

    @Test public void searchWithActionBarAfterAddingTask() {
        final String description = "new task";
        addTaskWithDialog(description);
        onView(withId(R.id.action_search)).perform(typeText(description), submit(), closeSoftKeyboard());
        onView(withId(android.R.id.list)).check(matches(withListSize(1)));
    }

    private static void addTaskWithDialog(String text) {
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.new_task_description)).perform(typeText("new task"), closeSoftKeyboard());
        onView(withText(R.string.add)).perform(click());
    }

    @After public void clearTaskData() {
        clearDirectory(mContext.getFileStreamPath("taskdata"));
    }

    private static void clearDirectory(File dir) {
        for (String child : dir.list()) {
            new File(dir, child).delete();
        }
    }

    private static ViewAction submit() {
        final int btnSubmit = 66;
        return pressKey(btnSubmit);
    }

}

enum Orientation {

    Portrait  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT),
    Landscape (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    private int mRaw;

    Orientation(int value) {
        mRaw = value;
    }

    public int getRaw() {
        return mRaw;
    }

}

