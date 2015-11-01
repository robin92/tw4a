package pl.rbolanowski.tw4a.backend;

import android.support.annotation.NonNull;

import pl.rbolanowski.tw4a.Task;

public interface Database {

    class AlreadyStoredException extends Exception {}

    class IncompleteArgumentException extends Exception {}

    @NonNull Task[] select();

    void insert(Task task) throws AlreadyStoredException, IncompleteArgumentException;

}

