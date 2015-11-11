package pl.rbolanowski.tw4a;

import android.test.mock.MockContext;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static org.mockito.Mockito.*;

public class TaskAdapterTest extends AndroidMockitoTestCase {

    private static final int LAYOUT = R.layout.task_list_element;
    private MockContext mContext;

    @Before public void setUp() {
        mContext = mock(MockContext.class);
    }

    @Test public void constructs() {
        TaskAdapter adapter;

        adapter = new TaskAdapter(mContext, LAYOUT, new ArrayList<Task>());
        assertEquals(0, adapter.getCount());

        adapter = new TaskAdapter(mContext, LAYOUT, makeTasks(2));
        assertEquals(2, adapter.getCount());
    }

    @Test public void add() {
        TaskAdapter adapter = new TaskAdapter(mContext, LAYOUT, new ArrayList<Task>());

        adapter.add(new Task());
        assertEquals(1, adapter.getCount());

        adapter.addAll(new Task(), new Task(), new Task());
        assertEquals(4, adapter.getCount());
    }

    @Test public void clear() {
        TaskAdapter adapter = new TaskAdapter(mContext, LAYOUT, makeTasks(2));
        adapter.clear();
        assertEquals(0, adapter.getCount());
    }

    private static ArrayList<Task> makeTasks(int size) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (int i = 0; i < size; i++) tasks.add(new Task());
        return tasks;
    }

}
