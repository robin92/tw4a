package pl.rbolanowski.tw4a.backend.taskwarrior;

import org.junit.Test;

import static org.junit.Assert.*;
import static pl.rbolanowski.tw4a.Task.EPSILON;

public class InternalTaskTest {

    @Test public void newTaskHasUnsetFields() {
        InternalTask task = new InternalTask();
        assertNull(task.uuid);
        assertNull(task.description);
        assertNull(task.status);
        assertEquals(0.0f, task.urgency, EPSILON);
    }

}

