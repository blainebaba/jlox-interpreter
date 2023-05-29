package com.blaine.lox.interpreter;

import java.util.List;

public interface LoxCallable {
    Object call(Interpreter interpreter, List<Object> args);
    int paramSize();
}
