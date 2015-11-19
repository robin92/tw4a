package pl.rbolanowski.tw4a;

import android.os.Bundle;
import android.view.*;

import roboguice.fragment.RoboFragment;

public class EmptyFragment extends RoboFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }

}

