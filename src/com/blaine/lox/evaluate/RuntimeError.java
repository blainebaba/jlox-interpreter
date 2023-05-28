package com.blaine.lox.evaluate;

// Inform a RuntimeError in lox script. This is different from RuntimeException which is an internal error.
public class RuntimeError extends RuntimeException {
    public String msg;
    public int line;
    public int col;
    public RuntimeError(String msg, int line, int col) {
        this.msg = msg;
        this.line = line;
        this.col = col;
    }
}
