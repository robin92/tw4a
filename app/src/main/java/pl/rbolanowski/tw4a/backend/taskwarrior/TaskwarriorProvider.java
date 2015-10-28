package pl.rbolanowski.tw4a.backend.taskwarrior;

import java.io.IOException;
import java.io.InputStream;

public interface TaskwarriorProvider {

    InputStream getInputStream() throws IOException;

}
