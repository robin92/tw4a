package pl.rbolanowski.tw4a;

import android.os.Bundle;
import android.view.*;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class ErrorFragment
    extends RoboFragment
    implements View.OnClickListener {

    public interface OnRetryListener {

        void onRetry();

    }

    @InjectView(android.R.id.button1) private View mButton;
    private OnRetryListener mOnRetryListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_error, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mOnRetryListener == null) return;
        mOnRetryListener.onRetry();
    }

    public void setOnRetryListener(OnRetryListener listener) {
        mOnRetryListener = listener;
    }

}

