package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.support.annotation.NonNull;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

public class JsonParser implements Parser {

    public static class MissingPropertyException extends ParserException {}

    public static class ValueException extends ParserException {}

    private LinkedList<InternalTask> mTasks;
    private JsonReader mReader;
    private VisitableNode[] mVisitables;

    @Override
    public InternalTask[] parse(String taskStr) throws ParserException {
        reset(taskStr);
        tryParse();
        return mTasks.toArray(new InternalTask[0]);
    }

    private void reset(String taskStr) {
        mTasks = new LinkedList<>();
        mReader = new JsonReader(new StringReader(taskStr));
        mVisitables = new VisitableNode[] {
            new DescriptionNode(mReader),
            new UuidNode(mReader),
            new TaskStatusNode(mReader),
            new UrgencyNode(mReader),
        };
    }

    private void tryParse() throws ParserException {
        try {
            parse();
        }
        catch (IOException e) {
            throw new ParserException();
        }
    }

    private void parse() throws IOException, ParserException {
        mReader.beginArray();
        while (mReader.hasNext()) {
            mTasks.add(parseTask());
        }
        mReader.endArray();
    }

    private InternalTask parseTask() throws IOException, ParserException {
        InternalTask task = new InternalTask();
        readTask(task);
        verify(task);
        return task;
    }

    private void readTask(InternalTask dest) throws IOException, ValueException {
        mReader.beginObject();
        while (mReader.hasNext()) {
            String name = mReader.nextName();
            if (!visitNode(name, dest)) {
                mReader.skipValue();
            }
        }
        mReader.endObject();
    }

    private boolean visitNode(String name, InternalTask task) throws IOException, ValueException {
        for (VisitableNode node : mVisitables) {
            if (!node.canVisit(name)) continue;
            node.visit(task);
            return true;
        }
        return false;
    }

    private static void verify(InternalTask task) throws ParserException {
        if (task.uuid == null || task.description == null) {
            throw new MissingPropertyException();
        }
    }

}

interface VisitableNode {

    boolean canVisit(String name);

    void visit(InternalTask task) throws IOException, JsonParser.ValueException;

}

abstract class StringNode implements VisitableNode {

    private JsonReader mReader;

    public StringNode(JsonReader reader) {
        mReader = reader;
    }

    @Override
    public void visit(InternalTask task) throws IOException, JsonParser.ValueException {
        if (mReader.peek() != JsonToken.STRING) {
            throw new JsonParser.ValueException();
        }
        modify(task, mReader.nextString());
    }

    protected abstract void modify(@NonNull InternalTask task, @NonNull String value);

}

abstract class FloatNode implements VisitableNode {

    private JsonReader mReader;

    public FloatNode(JsonReader reader) {
        mReader = reader;
    }

    @Override
    public void visit(InternalTask task) throws IOException, JsonParser.ValueException {
        if (mReader.peek() != JsonToken.NUMBER) {
            throw new JsonParser.ValueException();
        }
        modify(task, (float) mReader.nextDouble());
    }

    protected abstract void modify(@NonNull InternalTask task, @NonNull float value);

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
    protected void modify(@NonNull InternalTask task, @NonNull String value) {
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
    protected void modify(@NonNull InternalTask task, @NonNull String value) {
        task.uuid = value;
    }

}

class TaskStatusNode implements VisitableNode {

    private JsonReader mReader;

    public TaskStatusNode(JsonReader reader) {
        mReader = reader;
    }

    @Override
    public boolean canVisit(String name) {
        return "status".equals(name);
    }

    @Override
    public void visit(InternalTask task) throws IOException, JsonParser.ValueException {
        task.status = InternalTask.Status.valueOf(firstLetterUpperCase(mReader.nextString()));
    }

    private static String firstLetterUpperCase(String value) {
        StringBuilder builder = new StringBuilder();
        builder.append(value.substring(0, 1).toUpperCase()).append(value.substring(1));
        return builder.toString();
    }

}

class UrgencyNode extends FloatNode {

    public UrgencyNode(JsonReader reader) {
        super(reader);
    }

    @Override
    public boolean canVisit(String name) {
        return "urgency".equals(name);
    }

    @Override
    protected void modify(@NonNull InternalTask task, @NonNull float value) {
        task.urgency = value;
    }

}

