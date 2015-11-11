package pl.rbolanowski.tw4a.test.matchers;

import android.view.View;

import org.hamcrest.Matcher;

public class Matchers {

    public static Matcher<View> withListSize(int expectedSize) {
        return new ListSizeMatcher(expectedSize);
    }

}
