package com.blaine.lox.interpreter;

import java.util.List;

public class LoxClass implements LoxCallable {

    public String className;
    public List<LoxFunction> methods;
    private Environment envClosure;

    public LoxClass(String className, List<LoxFunction> methods, Environment env) {
        this.className = className;
        this.methods = methods;
        this.envClosure = env;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        return new LoxInstance(this);
    }

    @Override
    public int paramSize() {
        return 0;
    }

    @Override
    public String toString() {
        return "class:" + className;
    }
    
}
