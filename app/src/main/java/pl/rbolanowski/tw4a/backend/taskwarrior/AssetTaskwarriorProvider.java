package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class AssetTaskwarriorProvider implements TaskwarriorProvider {

    private static final String NATIVE_BINARY = "task-2.5.0";

    private Context mContext;

    public AssetTaskwarriorProvider(Context context) {
        mContext = context;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mContext.getAssets().open(NATIVE_BINARY);
    }

}

