package pl.rbolanowski.tw4a.backend.taskwarrior;

import com.google.inject.AbstractModule;

import pl.rbolanowski.tw4a.backend.BackendFactory;

public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(BackendFactory.class).to(TaskwarriorBackendFactory.class);
    }

}
