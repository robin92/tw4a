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

public class TaskDialog extends RoboDialogFragment {

    public static interface OnTaskChangedListener {

        void onTaskChanged(Task task);

    }

    private OnTaskChangedListener mOnTaskChangedListener;
    private Task mTask;
    private boolean mEditDialog = false;

    public void setOnTaskChangedListener(OnTaskChangedListener listener) {
        mOnTaskChangedListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mEditDialog = true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_task_dialog, null);
        builder.setView(view);
        if (mEditDialog) {
            mTask = getArguments().getParcelable("current task");
            fillWithCurrentTask(view);
            builder.setTitle(R.string.edit_task_dialog_header);  
        }
        else {
           mTask = new Task();                
           builder.setTitle(R.string.add_task_dialog_header);
        }
        setButtons(builder, view);
        final AlertDialog dialog = builder.create();
        buttonEnabling(dialog, view);        
        return dialog;
    }

    private void fillWithCurrentTask(View view) {
        EditText description = (EditText) view.findViewById(R.id.new_task_description);
        description.setText(mTask.description);
        description.setSelection(description.getText().length());
    }

    private void setButtons(AlertDialog.Builder builder, final View view) {
        String positiveButtonName = getString(R.string.add);
        if (mEditDialog) {
            positiveButtonName = getString(R.string.save);
        } 
        builder.setPositiveButton(positiveButtonName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText description = (EditText) view.findViewById(R.id.new_task_description);
                mTask.description = description.getText().toString();
                if (mOnTaskChangedListener != null) {
                    mOnTaskChangedListener.onTaskChanged(mTask);
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                TaskDialog.this.getDialog().cancel();
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
