package pl.rbolanowski.tw4a;

import android.os.Bundle;
import android.view.*;

import roboguice.fragment.RoboFragment;

public class LoadingFragment extends RoboFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

}

