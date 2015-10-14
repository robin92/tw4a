package pl.rbolanowski.tw4a.backend;

import android.support.annotation.NonNull;

import java.util.List;

import pl.rbolanowski.tw4a.Task;

public interface Database {
    
    @NonNull List<Task> select();

}

