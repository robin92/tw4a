package pl.rbolanowski.tw4a.backend;

import java.io.IOException;
import java.io.InputStream;

public interface BackendProvider {

    InputStream getInputStream() throws IOException;

}
