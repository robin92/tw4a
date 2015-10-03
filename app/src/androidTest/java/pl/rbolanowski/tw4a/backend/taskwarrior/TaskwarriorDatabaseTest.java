package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import pl.rbolanowski.tw4a.test.AndroidTestCase;

@RunWith(AndroidJUnit4.class)
public class TaskwarriorDatabaseTest extends AndroidTestCase {

    private TaskwarriorDatabase mDatabase = new TaskwarriorDatabase();

    @Test public void selectsEmptyList() {
        assertTrue(mDatabase.select().isEmpty());
    }

}

