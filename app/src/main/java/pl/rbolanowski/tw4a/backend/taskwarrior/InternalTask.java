package pl.rbolanowski.tw4a.backend.taskwarrior;

public class InternalTask {

    enum Status {

        Pending ("pending"),
        Recurring ("recurring"),
        Completed ("completed");

        private String mRepr;

        Status(String repr) {
            mRepr = repr;
        }

        @Override
        public String toString() { return mRepr; }

    }

    public String uuid;
    public String description;
    public Status status;

}

