package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.rbolanowski.tw4a.test.AndroidTestCase;

import java.util.regex.Pattern;

@RunWith(AndroidJUnit4.class)
public class NativeTaskwarriorTest extends AndroidTestCase {

    @BeforeClass public static void configureBackend() throws Exception {
        new TaskwarriorBackendFactory(getTargetContext()).newConfigurator().configure();
    }

    private NativeTaskwarrior mTaskwarrior;

    @Before public void setupTaskwarrior() {
        mTaskwarrior = new NativeTaskwarrior(getTargetContext());
    }

    @After public void clearDataDirectory() {
        mTaskwarrior.clear();
    }

    @Test public void putsTasks() {
        NativeTaskwarrior.Output outputs[] = new NativeTaskwarrior.Output[] {
            mTaskwarrior.put("first"),
            mTaskwarrior.put("second"),
        };
        assertTrue(matchPattern(Pattern.compile("Created task 1"), outputs[0].stdout));
        assertTrue(matchPattern(Pattern.compile("Created task 2"), outputs[1].stdout));
    }

    @Test public void exportsData() throws Exception{
        mTaskwarrior.put("some task");
        mTaskwarrior.put("other task");
        String[] output = mTaskwarrior.export().stdout.split("\n");
        assertTrue(matchPattern(Pattern.compile("\"description\":\"some task\""), output[0]));
        assertTrue(matchPattern(Pattern.compile("\"description\":\"other task\""), output[1]));
    }

    private static boolean matchPattern(Pattern pattern, String value) {
        return pattern.matcher(value).find();
    }

}
