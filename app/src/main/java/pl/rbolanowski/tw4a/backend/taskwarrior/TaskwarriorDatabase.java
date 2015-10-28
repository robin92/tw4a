package pl.rbolanowski.tw4a.backend.taskwarrior;

import pl.rbolanowski.tw4a.Task;
import pl.rbolanowski.tw4a.backend.Database;

import java.util.List;
import java.util.LinkedList;

public class TaskwarriorDatabase implements Database {

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
        return list.toArray(new Task[] {});
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

}
