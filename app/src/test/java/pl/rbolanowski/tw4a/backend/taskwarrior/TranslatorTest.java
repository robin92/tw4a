package pl.rbolanowski.tw4a.backend.taskwarrior;

import org.junit.Test;
import pl.rbolanowski.tw4a.Task;

import static org.junit.Assert.*;

public class TranslatorTest {

    private Translator mTranslator = new Translator();

    @Test public void translatesNull() {
        assertTrue(mTranslator.translate(null) == null);
    }

    @Test public void translatesNotNullObjects() {
        InternalTask in = new InternalTask();
        assertTaskMatches(mTranslator.translate(in), null, null, false);

        in.uuid = "1234";
        in.description = "hello world";
        in.status = InternalTask.Status.Pending;
        assertTaskMatches(mTranslator.translate(in), "1234", "hello world", false);

        in.status = InternalTask.Status.Recurring;
        assertFalse(mTranslator.translate(in).done);

        in.status = InternalTask.Status.Completed;
        assertTrue(mTranslator.translate(in).done);
    }

    private static final void assertTaskMatches(Task task, String uuid, String description, Boolean done) {
        assertEquals(task.uuid, uuid);
        assertEquals(task.description, description);
        assertEquals(task.done, done);
    }

}

