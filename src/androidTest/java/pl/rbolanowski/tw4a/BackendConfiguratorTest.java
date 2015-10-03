package pl.rbolanowski.tw4a;

import android.test.mock.MockContext;

import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static org.mockito.Mockito.*;

public class BackendConfiguratorTest extends AndroidMockitoTestCase {

    private MockContext mContext;
    private BackendConfigurator mConfigurator;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = mock(MockContext.class);
        mConfigurator = new BackendConfigurator(mContext);
        assertNotNull(mContext, mConfigurator);
    }

    public void testGetsPathToBackendBinary() {
        verify(mContext).getFileStreamPath("task");
    }

}
 
