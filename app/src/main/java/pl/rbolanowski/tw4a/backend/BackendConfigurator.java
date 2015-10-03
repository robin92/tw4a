package pl.rbolanowski.tw4a.backend;

public interface BackendConfigurator {

    abstract class BackendException extends Exception {}

    void configure() throws BackendException;

}
