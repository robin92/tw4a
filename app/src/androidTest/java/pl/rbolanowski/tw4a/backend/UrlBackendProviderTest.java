package pl.rbolanowski.tw4a.backend;

import android.test.AndroidTestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;

import pl.rbolanowski.tw4a.StreamUtil;

import static org.mockito.Mockito.*;

public class UrlBackendProviderTest extends AndroidTestCase {

    private static final String CONTENT = "hello world";

    private UrlBackendProvider mProvider;
    private URLConnection mConnectionMock;
    private StreamUtil mStreams = new StreamUtil();

    @Override
    protected void setUp() throws Exception {
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

    public void testReturnsStreamFromUrl() throws Exception {
        String actual = new String(mStreams.readAndClose(mProvider.getInputStream()).toByteArray());
        assertEquals(CONTENT, actual);
        verify(mConnectionMock, atLeast(1)).getInputStream();
    }

}
