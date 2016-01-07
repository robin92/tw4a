package pl.rbolanowski.tw4a.backend;

import java.io.InputStream;
import java.io.IOException;

public interface Exporter {

    interface Handler {

        void onStreamReady(String name, InputStream inputStream) throws IOException;

    }

    void export(Handler handler) throws IOException;

}

