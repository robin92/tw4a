package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.rbolanowski.tw4a.Task;
import pl.rbolanowski.tw4a.test.AndroidTestCase;

@RunWith(AndroidJUnit4.class)
public class JsonTranslatorTest extends AndroidTestCase {

    private static final String INPUT = "{\"id\":1,\"entry\":\"20151024T102451Z\",\"modified\":\"20151024T102451Z\",\"status\":\"pending\",\"uuid\":\"uuid-value\",\"urgency\":0,\"description\":\"some task\"}";
    private static final String COMPLETED_TASK_INPUT = "{\"id\":1,\"status\":\"completed\",\"uuid\":\"uuid-value\",\"urgency\":0,\"description\":\"some task\"}";

    @Test(expected = NullPointerException.class)
    public void reportsErrorOnNullInput() throws Exception {
        decode(null);
    }

    @Test(expected = JsonTranslator.ParserException.class)
    public void reportsErrorOnNonJsonInput() throws Exception {
        decode("some text");
    }

    @Test(expected = JsonTranslator.MissingPropertyException.class)
    public void reportsErrorOnMissingUuidField() throws Exception {
        decode(removeProperty(INPUT, "uuid"));
    }

    @Test(expected = JsonTranslator.MissingPropertyException.class)
    public void reportsErrorOnMissingDescriptionField() throws Exception {
        decode(removeProperty(INPUT, "description"));
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

    @Test(expected = JsonTranslator.ValueException.class)
    public void reportsErrorWhenUuidIsNotString() throws Exception {
       decode(INPUT.replace("\"uuid\":\"uuid-value\"", "\"uuid\": 12"));
    }

    @Test(expected = JsonTranslator.ValueException.class)
    public void reportsErrorWhenDescriptionIsNotString() throws Exception {
        decode(INPUT.replace("\"description\":\"some task\"", "\"description\": 666"));
    }

    @Test public void translatesJsonToTask() throws Exception {
        Task task = decode(INPUT);
        assertEquals("uuid-value", task.uuid);
        assertEquals("some task", task.description);
        assertFalse(task.done);

        task = decode(COMPLETED_TASK_INPUT);
        assertTrue(task.done);
    }

    private Task decode(String any) throws Exception {
        JsonTranslator translator = new JsonTranslator();
        return translator.decode(any);
    }

}
