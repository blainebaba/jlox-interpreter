package com.blaine.lox.interpreter;

// Inform a RuntimeError in lox script. This is different from RuntimeException which is an internal error.
public class RuntimeError extends RuntimeException {

    private String msg;
    private int line;
    private int col;

    public RuntimeError(String msg, int line, int col) {
        this.msg = msg;
        this.line = line;
        this.col = col;
    }

    public String toString() {
        String loc = (line > 0 && col > 0) ? String.format("line %d col %d, ", line, col) : "";
        return String.format("Runtime Error, %s%s",loc, msg);
    }
}
