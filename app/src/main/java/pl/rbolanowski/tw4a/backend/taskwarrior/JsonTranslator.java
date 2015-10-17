package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.support.annotation.NonNull;
import android.util.JsonReader;
import java.io.IOException;
import java.io.StringReader;

import android.util.JsonToken;
import pl.rbolanowski.tw4a.Task;

public class JsonTranslator implements Translator {

    public static class MissingPropertyException extends ParserException {}

    public static class ValueException extends ParserException {}

    private JsonReader mReader;
    private Task mTask;

    @Override
    public Task decode(String taskStr) throws ParserException {
        reset(taskStr);
        parse();
        verify();
        return mTask;
    }

    private void reset(String taskStr) {
        mReader = new JsonReader(new StringReader(taskStr));
        mTask = null;
    }

    private void parse() throws ParserException {
        JsonParser parser = new JsonParser(mReader);
        try {
            mTask = parser.parse();
        }
        catch (IOException e) {
            throw new ParserException();
        }
    }

    private void verify() throws ParserException {
        if (mTask.uuid == null || mTask.description == null) {
            throw new MissingPropertyException();
        }
    }

}

interface VisitableNode {

    boolean canVisit(String name);

    void visit(Task task) throws IOException, JsonTranslator.ValueException;

}

class JsonParser {

    private JsonReader mReader;
    private Task mOutput;
    private VisitableNode[] mVisitables;

    public JsonParser(JsonReader reader) {
        mReader = reader;
        mOutput = new Task();
        mVisitables = new VisitableNode[] {
            new DescriptionNode(mReader),
            new UuidNode(mReader),
        };
    }

    public @NonNull Task parse() throws IOException, JsonTranslator.ValueException {
        readTask();
        return mOutput;
    }

    private void readTask() throws IOException, JsonTranslator.ValueException {
        mReader.beginObject();
        while (mReader.hasNext()) {
            String name = mReader.nextName();
            if (!visitNode(name)) {
                mReader.skipValue();
            }
        }
        mReader.endObject();
    }

    private boolean visitNode(String name) throws IOException, JsonTranslator.ValueException {
        for (VisitableNode node : mVisitables) {
            if (!node.canVisit(name)) continue;
            node.visit(mOutput);
            return true;
        }
        return false;
    }

}

abstract class StringNode implements VisitableNode {

    private JsonReader mReader;

    public StringNode(JsonReader reader) {
        mReader = reader;
    }

    @Override
    public void visit(Task task) throws IOException, JsonTranslator.ValueException {
        if (mReader.peek() != JsonToken.STRING) {
            throw new JsonTranslator.ValueException();
        }
        modify(task, mReader.nextString());
    }

    protected abstract void modify(@NonNull Task task, @NonNull String value);

}

class DescriptionNode extends StringNode {

    public DescriptionNode(JsonReader reader) {
        super(reader);
    }

    @Override
    public boolean canVisit(String name) {
        return "description".equals(name);
    }

    @Override
    protected void modify(@NonNull Task task, @NonNull String value) {
        task.description = value;
    }

}

class UuidNode extends StringNode {

    public UuidNode(JsonReader reader) {
        super(reader);
    }

    @Override
    public boolean canVisit(String name) {
        return "uuid".equals(name);
    }

    @Override
    protected void modify(@NonNull Task task, @NonNull String value) {
        task.uuid = value;
    }

}
