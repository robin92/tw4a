package pl.rbolanowski.tw4a.backend.taskwarrior;

public interface Parser {

    class ParserException extends Exception {}

    InternalTask[] parse(String taskStr) throws ParserException;

}

