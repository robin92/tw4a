package pl.rbolanowski.tw4a;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import java.io.*;
import java.util.zip.*;

import org.junit.*;
import org.junit.runner.RunWith;

import pl.rbolanowski.tw4a.test.FunctionalTest;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.*;
import static android.support.test.espresso.intent.matcher.IntentMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class ExportingTaskwarriorDataTest extends FunctionalTest {

    private static class Data {

        String name;
        String expected;
        String actual;

        public Data(String name, String expected) {
            this.name = name;
            this.expected = expected;
        }

    }

    @Rule public final IntentsTestRule<MainActivity> mTestRule = new IntentsTestRule<>(MainActivity.class);

    private Data[] mData = new Data[] {
        new Data("first.data", "first file content"),
        new Data("second.data", "second file content"),
        new Data("pending.data", "some pending data"),
        new Data("...  .._  .data", "strange file data"),
    };

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        writeData();
        shareData();
    }

    private void writeData() throws IOException {
        for (Data data : mData) {
            write(mContext.getFileStreamPath("taskdata"), data);
        }
    }

    private static void write(File dir, Data data) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(dir, data.name));
        try {
            fos.write(data.expected.getBytes());
        }
        finally {
            fos.close();
        }
    }

    private void shareData() {
        stubAllIntents(Activity.RESULT_OK, null);
        openActionBarOverflowOrOptionsMenu(mContext);
        onView(withText(R.string.export_internal_data)).perform(click());
    }

    private static void stubAllIntents(final int result, final Intent intent) {
        intending(any(Intent.class)).respondWith(
            new Instrumentation.ActivityResult(result, intent));
    }

    @Test public void chooserAppearsOnExport() {
        intended(allOf(
            hasAction(Intent.ACTION_CHOOSER),
            hasExtra(is(Intent.EXTRA_INTENT), hasAction(Intent.ACTION_SEND))
        ));
    }

    @Test public void taskwarriorDataIsAvailableInCache() throws Exception {
        extractExportedData(findExportedData(mTestRule.getActivity().getCacheDir()));
        assertDataCached();
    }

    private static File findExportedData(File dir) {
        for (String fileName : dir.list()) {
            if (fileName.endsWith(".zip")) return new File(dir, fileName);
        }
        return null;
    }

    private void extractExportedData(File zipFile) throws IOException {
        assertTrue("empty zip archive", zipFile.length() > 0);
        ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zipFile));
        try {
            ZipEntry currentEntry = null;
            while ( (currentEntry = inputStream.getNextEntry()) != null) {
                Data data = findDataByName(currentEntry.getName());
                data.actual = new String(Streams.read(inputStream).toByteArray());
            }
        }
        catch (EOFException e) {
            inputStream.close();
        }
        finally {
            inputStream.close();
        }
    }

    private Data findDataByName(String name) {
        for (Data data : mData) {
            if (data.name.equals(name)) return data;
        }
        return null;
    }

    private void assertDataCached() throws IOException {
        for (Data data : mData) {
            assertEquals(data.expected, data.actual);
        }
    }

    private String read(File file) throws IOException {
        return new String(Streams.read(new FileInputStream(file)).toByteArray());
    }

}

