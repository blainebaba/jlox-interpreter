package com.blaine.lox;

public class ScannerError extends RuntimeException {
    public ScannerError(String msg, int lineNumber, int columnNumber) {
        super(String.format("line %d column %d, %s", lineNumber, columnNumber, msg));
    }
}