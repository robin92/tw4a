package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.rbolanowski.tw4a.Task;
import pl.rbolanowski.tw4a.backend.Database;
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

    @Test(expected = Database.AlreadyStoredException.class)
    public void insertingElementWithUuidCausesException() throws Exception {
        mTask.uuid = "some uuid";
        mDatabase.insert(mTask);
    }

    @Test(expected = Database.IncompleteArgumentException.class)
    public void insertingElementWithEmptyDescriptionCausesDetailedException() throws Exception {
        mDatabase.insert(mTask);
    }

    @Test public void insertsTask() throws Exception {
        mTask.description = "this is some task";
        mDatabase.insert(mTask);
        verify(mTaskwarrior, atLeastOnce()).put(mTask.description);
    }

    @Test(expected = Database.NotStoredException.class)
    public void updatingTaskWithoutUuidThrowsException() throws Exception {
        mDatabase.update(mTask);
    }

    @Test public void updatesTask() throws Exception {
        mTask.uuid = "1234";
        mTask.description = "updated!";

        mDatabase.update(mTask);
        verify(mTaskwarrior, atLeastOnce()).modify(mTask.uuid, mTask.description, Taskwarrior.TaskStatus.Pending);

        mTask.done = true;
        mDatabase.update(mTask);
        verify(mTaskwarrior, atLeastOnce()).modify(mTask.uuid, mTask.description, Taskwarrior.TaskStatus.Completed);
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

    @Test public void selectsOnlyPendingTasks() throws Exception {
        mTask.done = true;
        configure(mTask);
        assertEquals(0, mDatabase.select().length);
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

