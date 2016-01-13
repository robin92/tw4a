package pl.rbolanowski.tw4a;

import android.content.Context;
import android.view.*;
import android.widget.*;

import java.util.*;

public class TaskAdapter extends BaseAdapter
    implements Filterable {

    private Context mContext;
    private int mResource;
    final private Vector<Task> mDataReference;
    private Vector<Task> mCurrentData;

    public TaskAdapter(Context context, int resource, Vector<Task> data) {
        mContext = context;
        mResource = resource;
        mDataReference = data;
        mCurrentData = new Vector<>(mDataReference);
    }

    @Override
    public int getCount() {
        return mCurrentData.size();
    }

    @Override
    public Task getItem(int position) {
        return mCurrentData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(mResource, parent, false);
        TextView description = (TextView) rowView.findViewById(android.R.id.text1);
        description.setText(getItem(position).description);
        return rowView;
    }

    @Override
    public Filter getFilter() {
        return new TaskFilter(this);
    }

    protected void restore() {
        mCurrentData.clear();
        mCurrentData.addAll(mDataReference);
    }

    protected void removeAll(Task... tasks) {
        for (Task task : tasks) mCurrentData.remove(task);
    }

}

class TaskFilter extends Filter {

    private TaskAdapter mAdapter;

    public TaskFilter(TaskAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        mAdapter.restore();  // filter out from the whole task collection
        Vector<Task> notMatched = new Vector<>();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            Task task = mAdapter.getItem(i);
            if (constraint == null || !match(constraint, task)) {
                notMatched.add(task);
            }
        }
        return makeResults(notMatched);
    }

    private static boolean match(CharSequence seq, Task task) {
        if (hasUpperCase(seq)) {
            return task.description.contains(seq);
        }
        else {
            return task.description.toLowerCase().contains(seq.toString().toLowerCase());
        }
    }

    private static boolean hasUpperCase(CharSequence seq) {
        for (int i = 0; i < seq.length(); ++i) {
            if (Character.isUpperCase(seq.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private FilterResults makeResults(Collection<Task> tasks) {
        FilterResults results = new FilterResults();
        results.count = tasks.size();
        results.values = tasks.toArray(new Task[0]);
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        handleClear(constraint, results);
        handleFilter(constraint, results);
        mAdapter.notifyDataSetChanged();
    }

    private void handleClear(Object constraint, FilterResults results) {
        if (constraint != null) return;
        mAdapter.restore();
    }

    private void handleFilter(Object constraint, FilterResults results) {
        if (constraint == null) return;
        mAdapter.removeAll((Task[]) results.values);
    }

}

