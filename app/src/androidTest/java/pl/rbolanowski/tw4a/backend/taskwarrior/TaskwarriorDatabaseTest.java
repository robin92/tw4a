package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.rbolanowski.tw4a.Task;
import pl.rbolanowski.tw4a.test.AndroidTestCase;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class TaskwarriorDatabaseTest extends AndroidTestCase {

    private Taskwarrior mTaskwarrior;
    private Translator mTranslator;
    private TaskwarriorDatabase mDatabase;
    private Task mTask = new Task();

    @Before public void configureDatabase() {
        mTaskwarrior = mock(Taskwarrior.class);
        mTranslator = mock(Translator.class);
        mDatabase = new TaskwarriorDatabase(mTaskwarrior, mTranslator);
    }

    @Test public void selectsZeroTasksOnParsingError() throws Exception {
        when(mTaskwarrior.export()).thenReturn(new Taskwarrior.Output());
        when(mTranslator.decode(anyString())).thenThrow(Translator.ParserException.class);
        assertEquals(0, mDatabase.select().length);
    }

    @Test public void selectsZeroTasks() throws Exception {
        when(mTaskwarrior.export()).thenReturn(makeOutput(""));
        assertEquals(0, mDatabase.select().length);
    }

    @Test public void selectsAllTasks() throws Exception {
        configure(mTask);
        assertEquals(3, mDatabase.select().length);
    }

    private Taskwarrior.Output makeOutput(String stdout) {
        Taskwarrior.Output output = new Taskwarrior.Output();
        output.stdout = stdout;
        return output;
    }

    private void configure(Task task) throws Exception {
        when(mTaskwarrior.export()).thenReturn(makeOutput("1\n2\n3\n"));
        when(mTranslator.decode(anyString())).thenReturn(task);
    }

}

