package pl.rbolanowski.tw4a.backend.taskwarrior;

import java.util.List;
import java.util.LinkedList;

import pl.rbolanowski.tw4a.Task;
import pl.rbolanowski.tw4a.backend.Database;

public class TaskwarriorDatabase implements Database {

    @Override
    public List<Task> select() { return new TaskList(); }

}

class TaskList extends LinkedList<Task> {}

