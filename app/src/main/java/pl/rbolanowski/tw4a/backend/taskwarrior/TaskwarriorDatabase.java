package pl.rbolanowski.tw4a.backend.taskwarrior;

import pl.rbolanowski.tw4a.Task;
import pl.rbolanowski.tw4a.backend.Database;

import java.util.List;
import java.util.LinkedList;

public class TaskwarriorDatabase implements Database {

    private static class Field {

        private static class Description {
        }

        static Description description;

    }

    private Taskwarrior mTaskwarrior;
    private Translator mTranslator;

    public TaskwarriorDatabase(Taskwarrior taskwarrior, Translator translator) {
        mTaskwarrior = taskwarrior;
        mTranslator = translator;
    }

    @Override
    public Task[] select() {
        LinkedList<Task> list = new LinkedList<>();
        translateTaskwarrior(mTaskwarrior.export(), list);
        return removeCompleted(list).toArray(new Task[] {});
    }

    private void translateTaskwarrior(Taskwarrior.Output output, List<Task> dest) {
        String data = output.stdout;
        if (data == null || data.isEmpty()) return;
        for (String line : data.split("\n")) {
            try {
                dest.add(mTranslator.decode(line));
            } catch (Translator.ParserException e) {
                continue;
            }
        }
    }

    private List<Task> removeCompleted(List<Task> tasks) {
        LinkedList<Task> completedTasks =  new LinkedList<>();
        for (Task task : tasks) {
            if (!task.done) continue;
            completedTasks.add(task);
        }
        tasks.removeAll(completedTasks);
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

    private static Taskwarrior.TaskStatus translateStatus(boolean done) {
        return done ? Taskwarrior.TaskStatus.Completed : Taskwarrior.TaskStatus.Pending;
    }

}
