package pl.rbolanowski.tw4a.backend;

public interface BackendFactory {

    Configurator newConfigurator();

    Database newDatabase();

}
