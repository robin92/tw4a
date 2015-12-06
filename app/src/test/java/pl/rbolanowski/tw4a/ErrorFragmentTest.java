package pl.rbolanowski.tw4a;

import android.view.View;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ErrorFragmentTest {

    private ErrorFragment mFragment = new ErrorFragment();

    @Test public void supportsNullListener() {
        mFragment.setOnRetryListener(null);
        mFragment.onClick(null);
    }

    @Test public void registersListener() {
        ErrorFragment.OnRetryListener listener = mock(ErrorFragment.OnRetryListener.class);

        mFragment.setOnRetryListener(listener);
        verify(listener, times(0)).onRetry();

        mFragment.onClick(null);
        verify(listener, times(1)).onRetry();
    }

}

