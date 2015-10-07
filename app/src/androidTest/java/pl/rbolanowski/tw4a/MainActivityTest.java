package pl.rbolanowski.tw4a;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule public ActivityTestRule<MainActivity> mActivityRyle = new ActivityTestRule<>(MainActivity.class);

    @Test public void activityLoadsData() {
        onView(withId(android.R.id.progress)).check(matches(isEnabled()));
        onView(withId(android.R.id.text1)).check(matches(isDisplayed()));
    }

}
