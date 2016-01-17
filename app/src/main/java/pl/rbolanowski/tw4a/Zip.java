package pl.rbolanowski.tw4a;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.*;

import static pl.rbolanowski.tw4a.Streams.*;

public class Zip {

    public static class EntryHint {

        String name;
        InputStream inputStream;

        public EntryHint(String name, InputStream inputStream) {
            this.name = name;
            this.inputStream = inputStream;
        }

    }

    public interface UnzipHandler {

        void onEntry(ZipEntry entry, InputStream inputStream) throws IOException;

    }

    private static class UnzipHandlerImpl implements UnzipHandler {

        private HashMap<String, byte[]> mData = new HashMap<>();

        @Override
        public void onEntry(ZipEntry entry, InputStream inputStream) throws IOException {
            mData.put(entry.getName(), read(inputStream).toByteArray());
        }

    }

    private Zip() {}

    public static void zip(OutputStream outputStream, EntryHint... entryHints) throws IOException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        for (EntryHint entryHint : entryHints) {
            zip(zipOutputStream, entryHint);
        }
    }

    private static void zip(ZipOutputStream zipOutputStream, EntryHint entryHint) throws IOException {
        try {
            zipOutputStream.putNextEntry(new ZipEntry(entryHint.name));
            copy(entryHint.inputStream, zipOutputStream);
        }
        finally {
            zipOutputStream.closeEntry();
            entryHint.inputStream.close();
        }
    }

    public static Map<String, byte[]> unzip(InputStream inputStream) throws IOException {
        UnzipHandlerImpl handler = new UnzipHandlerImpl();
        unzip(inputStream, handler);
        return handler.mData;
    }

    public static void unzip(InputStream inputStream, UnzipHandler handler) throws IOException {
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        readZipFully(zipInputStream, handler);
    }

    private static void readZipFully(ZipInputStream inputStream, UnzipHandler handler) throws IOException {
        ZipEntry currentEntry = null;
        while ( (currentEntry = nextEntry(inputStream)) != null) {
            handleEntry(currentEntry, inputStream, handler);
        }
    }

    private static ZipEntry nextEntry(ZipInputStream inputStream) throws IOException {
        ZipEntry nextEntry = null;
        try {
            nextEntry = inputStream.getNextEntry();
        }
        catch (EOFException eof) {}
        finally {
            return nextEntry;
        }
    }

    private static void handleEntry(ZipEntry entry, ZipInputStream inputStream, UnzipHandler handler) throws IOException {
        try {
            handler.onEntry(entry, inputStream);
        }
        finally {
            inputStream.closeEntry();
        }
    }

}

