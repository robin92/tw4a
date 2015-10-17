package pl.rbolanowski.tw4a.backend.taskwarrior;

public interface Taskwarrior {

    class Output {

        public String stdout;
        public String stderr;

    }

    Output export();

}
