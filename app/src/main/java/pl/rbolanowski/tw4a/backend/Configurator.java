package pl.rbolanowski.tw4a.backend;

public interface Configurator {

    abstract class BackendException extends Exception {}

    void configure() throws BackendException;

}
