package pl.rbolanowski.tw4a.backend;

import android.support.annotation.NonNull;

import pl.rbolanowski.tw4a.Task;

public interface Database {
    
    @NonNull Task[] select();

}

