package pl.rbolanowski.tw4a.backend.taskwarrior;

import pl.rbolanowski.tw4a.Task;

public interface Translator {

    class ParserException extends Exception {}

    Task decode(String taskStr) throws ParserException;

}
