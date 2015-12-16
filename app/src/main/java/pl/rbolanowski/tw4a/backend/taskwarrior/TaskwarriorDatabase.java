package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.util.Log;

import java.util.*;

import pl.rbolanowski.tw4a.Task;
import pl.rbolanowski.tw4a.backend.Database;

public class TaskwarriorDatabase implements Database {

    private static final String LOG_TAG = TaskwarriorDatabase.class.getSimpleName();

    private static class Field {

        private static class Description {}

        static Description description;

    }

    private Parser mParser;
    private Taskwarrior mTaskwarrior;
    private Translator mTranslator;

    public TaskwarriorDatabase(Parser parser, Taskwarrior taskwarrior, Translator translator) {
        mParser = parser;
        mTaskwarrior = taskwarrior;
        mTranslator = translator;
    }

    @Override
    public Task[] select() {
        Taskwarrior.Output output = mTaskwarrior.export();
        List<InternalTask> internalTasks = only(parse(output.stdout), InternalTask.Status.Pending);
        return translateAll(internalTasks);
    }

    private List<InternalTask> parse(String str) {
        InternalTask tasks[] = new InternalTask[0];
        try {
            tasks = mParser.parse(str);
        }
        catch (Parser.ParserException e) {
            Log.w(LOG_TAG, "parsing failed, returning empty task list");
            Log.d(LOG_TAG, e.toString());
        }
        finally {
            return new LinkedList<>(Arrays.asList(tasks));
        }
    }

    private static List<InternalTask> only(List<InternalTask> elements, InternalTask.Status status) {
        Iterator<InternalTask> iter = elements.listIterator();
        while (iter.hasNext()) {
            InternalTask elem = iter.next();
            if (elem.status != status) {
                iter.remove();
            }
        }
        return elements;
    }

    private Task[] translateAll(List<InternalTask> internalTasks) {
        Task tasks[] = new Task[internalTasks.size()];
        int k = 0;
        for (InternalTask in : internalTasks) {
            tasks[k++] = mTranslator.translate(in);
        }
        return tasks;
    }

    @Override
    public void insert(Task task) throws AlreadyStoredException, IncompleteArgumentException {
        if (task.uuid != null) throw new AlreadyStoredException();
        require(task.description, Field.description);
        mTaskwarrior.put(task.description);
    }

    private void require(String value, Field.Description tag) throws IncompleteArgumentException {
        require(value != null);
    }

    private void require(boolean value) throws IncompleteArgumentException {
        if (!value) {
            throw new IncompleteArgumentException();
        }
    }

    @Override
    public void update(Task task) throws NotStoredException {
        if (task.uuid == null) throw new NotStoredException();
        mTaskwarrior.modify(task.uuid, task.description, translateStatus(task.done));
    }

    private static InternalTask.Status translateStatus(boolean done) {
        return done ? InternalTask.Status.Completed : InternalTask.Status.Pending;
    }

}
