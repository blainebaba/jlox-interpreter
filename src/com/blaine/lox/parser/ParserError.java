package com.blaine.lox.parser;

public class ParserError extends RuntimeException {
    private String msg;
    private int line;
    private int col;
    
    public ParserError(String msg, int line, int col) {
        this.msg = msg;
        this.line = line;
        this.col = col;
    }

    public ParserError(String msg) {
        this.msg = msg;
        this.line = 0;
        this.col = 0;
    }

    public String toString() {
        String loc = (line > 0 && col > 0) ? String.format("line %d col %d, ", line, col) : "";
        return String.format("Parsing Error, %s%s",loc, msg);
    }
}