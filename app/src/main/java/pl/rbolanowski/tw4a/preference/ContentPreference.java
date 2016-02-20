package pl.rbolanowski.tw4a.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

public class ContentPreference extends Preference {

    private String mCurrent;

    public ContentPreference(Context context) {
        super(context);
    }

    public ContentPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentPreference(Context context, AttributeSet attrs, int styleDef) {
        super(context, attrs, styleDef);
    }

    public ContentPreference(Context context, AttributeSet attrs, int styleDef, int styleRes) {
        super(context, attrs, styleDef, styleRes);
    }

}

