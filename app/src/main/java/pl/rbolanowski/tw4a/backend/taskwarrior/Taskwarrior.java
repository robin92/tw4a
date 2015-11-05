package pl.rbolanowski.tw4a.backend.taskwarrior;

import android.support.annotation.NonNull;

public interface Taskwarrior {

    enum TaskStatus {

        Pending ("pending"),
        Completed ("completed");

        private String mRepr;

        TaskStatus(String repr) {
            mRepr = repr;
        }

        @Override
        public String toString() { return mRepr; }

    }

    class Output {

        public String stdout;
        public String stderr;

    }

    Output export();

    Output put(@NonNull String description);

    /**
     * Modifies description of task with given UUID.
     */
    Output modify(@NonNull String uuid, String description, TaskStatus status);

}
