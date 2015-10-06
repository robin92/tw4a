package pl.rbolanowski.tw4a.backend;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UrlBackendProvider implements BackendProvider {

    private URL mUrl;

    public UrlBackendProvider(URL url) {
        mUrl = url;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mUrl.openStream();
    }

}
