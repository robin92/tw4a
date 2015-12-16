package pl.rbolanowski.tw4a.backend.taskwarrior;

import org.junit.Test;

import pl.rbolanowski.tw4a.Task;
import pl.rbolanowski.tw4a.backend.Database;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TaskwarriorDatabaseTest {

    private Parser mParser = mock(Parser.class);
    private Taskwarrior mTaskwarrior = mock(Taskwarrior.class);
    private Translator mTranslator = mock(Translator.class);
    private TaskwarriorDatabase mDatabase = new TaskwarriorDatabase(mParser, mTaskwarrior, mTranslator);
    private Task mTask = new Task();

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
        verify(mTaskwarrior, atLeastOnce()).modify(mTask.uuid, mTask.description, InternalTask.Status.Pending);

        mTask.done = true;
        mDatabase.update(mTask);
        verify(mTaskwarrior, atLeastOnce()).modify(mTask.uuid, mTask.description, InternalTask.Status.Completed);
    }

    @Test public void selectsZeroTasksOnParsingError() throws Exception {
        when(mTaskwarrior.export()).thenReturn(new Taskwarrior.Output());
        when(mParser.parse(anyString())).thenThrow(Parser.ParserException.class);
        assertEquals(0, mDatabase.select().length);
    }

    @Test public void selectsZeroTasks() throws Exception {
        when(mTaskwarrior.export()).thenReturn(makeOutput("[]"));
        when(mParser.parse("[]")).thenReturn(new InternalTask[0]);
        assertEquals(0, mDatabase.select().length);
    }

    @Test public void selectsOnlyPendingTasks() throws Exception {
        configure(internalTask(InternalTask.Status.Completed), null);
        assertEquals(0, mDatabase.select().length);

        configure(internalTask(InternalTask.Status.Recurring), null);
        assertEquals(0, mDatabase.select().length);

        configure(internalTask(InternalTask.Status.Pending), mTask);
        assertEquals(1, mDatabase.select().length);
    }

    @Test public void selectsTasks() throws Exception {
        final int tasksCount = 3;
        configure(tasksCount, internalTask(InternalTask.Status.Pending), mTask);
        assertEquals(tasksCount, mDatabase.select().length);
    }

    private static InternalTask internalTask(InternalTask.Status status) {
        InternalTask task = new InternalTask();
        task.status = status;
        return task;
    }

    private Taskwarrior.Output makeOutput(String stdout) {
        Taskwarrior.Output output = new Taskwarrior.Output();
        output.stdout = stdout;
        return output;
    }

    private void configure(InternalTask in, Task out) throws Exception {
        configure(1, in, out);
    }

    private void configure(int count, InternalTask in, Task out) throws Exception {
        InternalTask[] parsed = new InternalTask[count];
        for (int i = 0; i < parsed.length; i++) parsed[i] = in;
        when(mTaskwarrior.export()).thenReturn(makeOutput("1\n2\n3\n"));
        when(mParser.parse(anyString())).thenReturn(parsed);
        when(mTranslator.translate(in)).thenReturn(out);
    }

}

