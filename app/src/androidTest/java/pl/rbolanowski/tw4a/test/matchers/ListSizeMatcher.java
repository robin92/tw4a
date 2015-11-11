package pl.rbolanowski.tw4a.test.matchers;

import android.view.View;
import android.widget.ListView;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;

class ListSizeMatcher extends TypeSafeMatcher<View> {

    private final Integer mExpectedSize;
    private Integer mActualSize;

    public ListSizeMatcher(int size) {
        mExpectedSize = size;
    }

    @Override
    public boolean matchesSafely(final View view) {
        mActualSize = ((ListView) view).getChildCount();
        return mActualSize == mExpectedSize;
    }

    @Override
    public void describeTo(final Description description) {
        description
            .appendText("ListView should have ")
            .appendValue(mExpectedSize)
            .appendText(" items, but was ")
            .appendValue(mActualSize);
    }

}
