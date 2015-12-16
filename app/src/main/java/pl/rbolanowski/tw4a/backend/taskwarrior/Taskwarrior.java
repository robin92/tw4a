package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.support.annotation.NonNull;

public interface Taskwarrior {

    class Output {

        public String stdout;
        public String stderr;

    }

    Output export();

    Output put(@NonNull String description);

    /**
     * Modifies description of task with given UUID.
     */
    Output modify(@NonNull String uuid, String description, InternalTask.Status status);

}
