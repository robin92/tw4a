package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.support.test.runner.AndroidJUnit4;

import java.io.InputStream;

import com.google.inject.Inject;
import org.junit.*;
import org.junit.runner.RunWith;

import pl.rbolanowski.tw4a.test.AndroidTestCase;

@RunWith(AndroidJUnit4.class)
public class AssetTaskwarriorProviderTest extends AndroidTestCase {

    private AssetTaskwarriorProvider mProvider = new AssetTaskwarriorProvider(getTargetContext());

    @Test public void getsNotEmptyInputStream() throws Exception {
        InputStream stream = mProvider.getInputStream();
        assertNotNull(stream);
        try {
            assertTrue(stream.available() > 0);
        }
        finally {
            stream.close();
        }
    }

}

