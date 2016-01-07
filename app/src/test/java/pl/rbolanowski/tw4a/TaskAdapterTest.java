package pl.rbolanowski.tw4a;

import android.test.mock.MockContext;

import java.util.Vector;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TaskAdapterTest {

    private static final int LAYOUT = 12;
    private MockContext mContext = mock(MockContext.class);
    private Vector<Task> mTasks = new Vector<>();

    @Test public void constructs() {
        assertEquals(0, new TaskAdapter(mContext, LAYOUT, mTasks).getCount());
    }

    @Test public void changesInDataAreNotWatched() {
        mTasks.add(new Task());

        TaskAdapter adapter = new TaskAdapter(mContext, LAYOUT, mTasks);
        assertEquals(1, adapter.getCount());

        mTasks.addAll(makeTasks(3));
        adapter.notifyDataSetChanged();
        assertEquals(1, adapter.getCount());

        mTasks.clear();
        adapter.notifyDataSetChanged();
        assertEquals(1, adapter.getCount());
    }

    @Test public void getItemIdReturnsPosition() {
        TaskAdapter adapter = new TaskAdapter(mContext, LAYOUT, mTasks);
        assertEquals(0, adapter.getItemId(0));
        assertEquals(1, adapter.getItemId(1));
        assertEquals(666, adapter.getItemId(666));
    }

    @Test public void filters() {
        mTasks.addAll(makeTasks(5));
        TaskAdapter adapter = new TaskAdapter(mContext, LAYOUT, mTasks);

        filter(adapter, "desc");
        assertEquals(5, adapter.getCount());

        filter(adapter, "Desc");
        assertEquals(0, adapter.getCount());

        filter(adapter, "1");
        assertEquals(1, adapter.getCount());

        filter(adapter, "2");
        assertEquals(1, adapter.getCount());

        filter(adapter, "22");
        assertEquals(0, adapter.getCount());

        filter(adapter, "6");
        assertEquals(0, adapter.getCount());

        filter(adapter, null);
        assertEquals(5, adapter.getCount());
    }

    private static void filter(TaskAdapter adapter, CharSequence query) {
        TaskFilter filter = (TaskFilter) adapter.getFilter();
        filter.publishResults(query, filter.performFiltering(query));
    }

    private static Vector<Task> makeTasks(int size) {
        Vector<Task> tasks = new Vector<>();
        for (int i = 0; i < size; i++) {
            Task task = new Task();
            task.description = "desc number " + Integer.toString(i);
            tasks.add(task);
        }
        return tasks;
    }

}

