package pl.rbolanowski.tw4a.backend.taskwarrior;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UrlTaskwarriorProvider implements TaskwarriorProvider {

    private URL mUrl;

    public UrlTaskwarriorProvider(URL url) {
        mUrl = url;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mUrl.openStream();
    }

}
