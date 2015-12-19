package pl.rbolanowski.tw4a.backend.taskwarrior;

import pl.rbolanowski.tw4a.Task;

public class Translator {

    public Task translate(InternalTask in) {
        Task out = null;
        if (in != null) {
            out = new Task();
            out.uuid = in.uuid;
            out.description = in.description;
            out.done = in.status == InternalTask.Status.Completed;
            out.urgency = in.urgency;
        }
        return out;
    }

}

