package pl.rbolanowski.tw4a;

import org.junit.Test;

import static org.junit.Assert.*;

public class TaskTest {

    @Test public void newTaskHasUnsetFields() {
        Task task = new Task();
        assertNull(task.uuid);
        assertNull(task.description);
        assertFalse(task.done);
        assertEquals(0.0f, task.urgency, Task.EPSILON);
    }

}

