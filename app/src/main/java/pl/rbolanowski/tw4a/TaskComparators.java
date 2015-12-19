package pl.rbolanowski.tw4a;

import java.util.Comparator;

public class TaskComparators {

    private TaskComparators() {}

    /**
     * Compares by urgency in ascending order.
     */
    public static Comparator<Task> byUrgency() {
        return new Comparator<Task>() {
            @Override
            public int compare(Task lhs, Task rhs) {
                return Float.compare(lhs.urgency, rhs.urgency);
            }
        };
    }

    public static <T> Comparator<T> reverse(final Comparator<T> comparator) {
        return new Comparator<T>() {
            @Override
            public int compare(T lhs, T rhs) {
                return -comparator.compare(lhs, rhs);
            }
        };
    }

}

