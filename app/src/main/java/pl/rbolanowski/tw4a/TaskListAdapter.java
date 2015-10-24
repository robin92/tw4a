package pl.rbolanowski.tw4a;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TaskListAdapter extends ArrayAdapter<Task> {

    private Context mContext;
    private int mResource;
  
    public TaskListAdapter(Context context, int resource, Task[] tasks) {
        super(context, resource, tasks);
        this.mContext = context;
        this.mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(mResource, parent, false);
        TextView description = (TextView) rowView.findViewById(android.R.id.text1);
        description.setText(getItem(position).description);
        return rowView;
    }
}
