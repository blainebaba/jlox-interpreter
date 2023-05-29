package com.blaine.lox.interpreter;

import com.blaine.lox.Token;

// use exception to implement function return
public class ReturnThrowable extends RuntimeException {

    public Object returnValue;
    public Token token;

    public ReturnThrowable(Object returnValue, Token token) {
        this.returnValue = returnValue;
    }
}
