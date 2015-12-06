package pl.rbolanowski.tw4a;

import android.app.Application;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.inject.*;
import org.junit.*;
import org.junit.runner.RunWith;
import roboguice.RoboGuice;

import pl.rbolanowski.tw4a.backend.*;
import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityConfiguringErrorTest extends RoboguiceTestCase {

    private static final class FakeException extends Configurator.BackendException {}

    @Rule public ActivityTestRule<MainActivity> mActivityRule = lazyActivityRule();

    private MainActivity mActivity;

    private BackendFactory mFactoryMock;
    private Configurator mConfiguratorMock;

    private class BackendModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(BackendFactory.class).toInstance(mFactoryMock);
        }

    }

    private static ActivityTestRule<MainActivity> lazyActivityRule() {
        return new ActivityTestRule<>(MainActivity.class, false, false);
    }

    @Before public void setUp() throws Exception {
        mConfiguratorMock = mock(Configurator.class);
        doThrow(FakeException.class).when(mConfiguratorMock).configure();

        mFactoryMock = mock(BackendFactory.class);
        when(mFactoryMock.newConfigurator()).thenReturn(mConfiguratorMock);

        overrideInjector(new BackendModule());
        startActivity();
        unlockScreen(mActivity);
    }

    private void startActivity() {
        mActivity = mActivityRule.launchActivity(null);
        assertNotNull(mActivity);
    }

    @Test public void informationOfConfiguringErrorIsDisplayed() {
        onView(withText(R.string.configure_error_title)).check(matches(isDisplayed()));
        onView(withText(R.string.configure_error_description)).check(matches(isDisplayed()));
    }

    @Test public void retriesConfiguration() throws Exception {
        onView(withId(android.R.id.button1)).perform(click());
        verify(mConfiguratorMock, times(2)).configure();
    }

}

class RoboguiceTestCase extends AndroidMockitoTestCase {

    private Application mApp = (Application) getTargetContext().getApplicationContext();

    public void overrideInjector(Module module) {
        RoboGuice.overrideApplicationInjector(mApp, module);
    }

    @After public void tearDown() {
        RoboGuice.destroyInjector(mApp);
    }

}

