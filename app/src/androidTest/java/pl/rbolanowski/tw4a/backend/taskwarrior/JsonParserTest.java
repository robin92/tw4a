package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.rbolanowski.tw4a.test.AndroidTestCase;

import static pl.rbolanowski.tw4a.Task.EPSILON;

@RunWith(AndroidJUnit4.class)
public class JsonParserTest extends AndroidTestCase {

    private static final String INPUT = "{\"id\":1,\"entry\":\"20151024T102451Z\",\"modified\":\"20151024T102451Z\",\"status\":\"pending\",\"uuid\":\"uuid-value\",\"urgency\":0,\"description\":\"some task\"}";

    private static String generateTaskInput(String statusStr) {
        return String.format(
            "{\"id\":1,\"status\":\"%s\",\"uuid\":\"uuid-value\",\"urgency\":0,\"description\":\"some task\"}",
            statusStr);
    }

    private static String array(String... elements) {
        StringBuilder builder = new StringBuilder("[\n");
        for (int i = 0; i < elements.length; i++) {
            if (i != 0) builder.append(",\n");
            builder.append(elements[i]);
        }
        builder.append("\n]");
        return builder.toString();
    }

    @Test(expected = NullPointerException.class)
    public void reportsErrorOnNullInput() throws Exception {
        decode(null);
    }

    @Test(expected = JsonParser.ParserException.class)
    public void reportsErrorOnNonJsonInput() throws Exception {
        decode("some text");
    }

    @Test(expected = JsonParser.MissingPropertyException.class)
    public void reportsErrorOnMissingUuidField() throws Exception {
        decode(array(removeProperty(INPUT, "uuid")));
    }

    @Test(expected = JsonParser.MissingPropertyException.class)
    public void reportsErrorOnMissingDescriptionField() throws Exception {
        decode(array(removeProperty(INPUT, "description")));
    }

    private static String removeProperty(String text, String property) {
        return text.replace(buildPropertyName(property), buildPropertyName("not-", property));
    }

    private static String buildPropertyName(String prefix, String property) {
        return new StringBuilder()
            .append('"')
            .append(prefix)
            .append(property)
            .append('"')
            .toString();
    }

    private static String buildPropertyName(String property) {
        return buildPropertyName("", property);
    }

    @Test(expected = JsonParser.ValueException.class)
    public void reportsErrorWhenUuidIsNotString() throws Exception {
       decode(array(INPUT.replace("\"uuid\":\"uuid-value\"", "\"uuid\": 12")));
    }

    @Test(expected = JsonParser.ValueException.class)
    public void reportsErrorWhenDescriptionIsNotString() throws Exception {
        decode(array(INPUT.replace("\"description\":\"some task\"", "\"description\": 666")));
    }

    @Test public void parsesSimpleFields() throws Exception {
        InternalTask task = decode(array(INPUT))[0];
        assertEquals("uuid-value", task.uuid);
        assertEquals("some task", task.description);
        assertEquals(0.0f, task.urgency, EPSILON);
        assertEquals(InternalTask.Status.Pending, task.status);
    }

    @Test public void parsesStatuses() throws Exception {
        InternalTask task = decode(array(INPUT))[0];
        assertEquals(InternalTask.Status.Pending, task.status);

        task = decode(array(generateTaskInput("completed")))[0];
        assertEquals(InternalTask.Status.Completed, task.status);

        task = decode(array(generateTaskInput("recurring")))[0];
        assertEquals(InternalTask.Status.Recurring, task.status);
    }

    @Test public void parsesUrgency() throws Exception {
        InternalTask task = decode(array(INPUT))[0];
        assertEquals(0.0f, task.urgency, EPSILON);

        task = decode(array(INPUT.replace("\"urgency\":0", "\"urgency\":0.0")))[0];
        assertEquals(0.0f, task.urgency, EPSILON);

        task = decode(array(INPUT.replace("\"urgency\":0", "\"urgency\":12.3456789")))[0];
        assertEquals(12.3456f, task.urgency, EPSILON);

        task = decode(array(INPUT.replace("\"urgency\":0", "\"urgency\":-12.3456789")))[0];
        assertEquals(-12.3456f, task.urgency, EPSILON);
    }

    @Test public void parsesArrayOfTasks() throws Exception {
        InternalTask[] tasks = decode(array(
            INPUT.replace("\"uuid\":\"uuid-value\"", "\"uuid\": \"abc666\""),
            INPUT,
            INPUT.replace("\"uuid\":\"uuid-value\"", "\"uuid\": \"def777\"")
        ));
        assertEquals(3, tasks.length);
        assertEquals("abc666", tasks[0].uuid);
        assertEquals("uuid-value", tasks[1].uuid);
        assertEquals("def777", tasks[2].uuid);
    }

    private InternalTask[] decode(String any) throws Exception {
        System.err.println("decoding\n" + any);
        return new JsonParser().parse(any);
    }

}

