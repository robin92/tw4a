package pl.rbolanowski.tw4a;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.*;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.inject.Inject;
import roboguice.fragment.*;

import pl.rbolanowski.tw4a.backend.*;

public class AddTaskDialog extends RoboDialogFragment {

    @Inject private BackendFactory mBackend;
    private TaskAdapter mTaskAdapter;

    static public AddTaskDialog newInstance(TaskAdapter taskAdapter) {
        AddTaskDialog instance = new AddTaskDialog();
        instance.mTaskAdapter = taskAdapter;
        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_task_dialog, null);
        builder.setView(view);
        builder.setTitle(R.string.add_task_dialog_header);   
        setButtons(builder, view);
        final AlertDialog dialog = builder.create();
        buttonEnabling(dialog, view);
        return dialog;
    }

    private void setButtons(AlertDialog.Builder builder, final View view) {
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText description = (EditText) view.findViewById(R.id.new_task_description);
                Task task = new Task();
                task.description = description.getText().toString();
                task.done = false;
                Database database = mBackend.newDatabase();
                try {
                    database.insert(task);
                    mTaskAdapter.clear();
                    mTaskAdapter.addAll(database.select());
                } catch (Database.AlreadyStoredException e) {
                    // IMPOSSIBLE CASE
                } catch (Database.IncompleteArgumentException e) {
                    // IMPOSSIBLE CASE
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddTaskDialog.this.getDialog().cancel();
            }
        });

    }

    private void buttonEnabling(final AlertDialog dialog, View view) {
        EditText description = (EditText) view.findViewById(R.id.new_task_description);
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 || s.toString().trim().length() == 0) {
                    dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });  
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
            }      
        });
    }
}
