package pl.rbolanowski.tw4a;

import java.util.Comparator;
import org.junit.Test;

import static org.junit.Assert.*;
import static pl.rbolanowski.tw4a.TaskComparators.byUrgency;
import static pl.rbolanowski.tw4a.TaskComparators.reverse;

public class TaskComparatorsTest {

    @Test(expected = NullPointerException.class)
    public void byUrgencyHandlesNull() {
        byUrgency().compare(null, null);
    }

    @Test public void byUrgencyComparesInAscendingOrder() {
        testComparator(byUrgency(), makeTask(13.231f), makeTask(3.12f));
    }

    @Test public void reversesComparator() {
        testComparator(reverse(byUrgency()), makeTask(3.12f), makeTask(13.231f));
    }

    private static void testComparator(final Comparator<Task> comparator, Task one, Task two) {
        assertEquals(0, comparator.compare(one, one));
        assertEquals(1, comparator.compare(one, two));
        assertEquals(-1, comparator.compare(two, one));
    }

    private static Task makeTask(float urgency) {
        Task task = new Task();
        task.urgency = urgency;
        return task;
    }

}

