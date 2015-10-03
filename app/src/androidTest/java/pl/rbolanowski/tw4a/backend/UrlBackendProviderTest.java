package pl.rbolanowski.tw4a.backend;

import android.support.test.runner.AndroidJUnit4;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;

import org.junit.*;
import org.junit.runner.RunWith;

import pl.rbolanowski.tw4a.StreamUtil;
import pl.rbolanowski.tw4a.test.AndroidMockitoTestCase;

import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class UrlBackendProviderTest extends AndroidMockitoTestCase {

    private static final String CONTENT = "hello world";

    private UrlBackendProvider mProvider;
    private URLConnection mConnectionMock;
    private StreamUtil mStreams = new StreamUtil();

    @Before public void setUp() throws Exception {
        configureConnection();
        configureProvider();
    }

    private void configureConnection() throws Exception {
        mConnectionMock = mock(URLConnection.class);
        when(mConnectionMock.getInputStream()).thenReturn(new ByteArrayInputStream(CONTENT.getBytes()));
    }

    private void configureProvider() throws Exception {
        URLStreamHandler handler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) throws IOException {
                return mConnectionMock;
            }
        };
        URL fakeUrl = new URL("http", "dummy.url.com", 80, "/with/some/stuff.any", handler);
        mProvider = new UrlBackendProvider(fakeUrl);
    }

    @Test public void returnsStreamFromUrl() throws Exception {
        String actual = new String(mStreams.read(mProvider.getInputStream()).toByteArray());
        assertEquals(CONTENT, actual);
        verify(mConnectionMock, atLeast(1)).getInputStream();
    }

}
