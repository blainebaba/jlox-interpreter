package com.blaine.lox.interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable {

    public String className;
    public Map<String, LoxFunction> methods;
    public Environment env;

    public LoxClass(String className, List<LoxFunction> methods, Environment env) {
        this.className = className;
        this.methods = new HashMap<>();
        for (LoxFunction method : methods) {
            this.methods.put(method.funName, method);
        }
        this.env = env;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        LoxInstance instance = new LoxInstance(this);
        
        if (methods.containsKey("init")) {
            methods.get("init").bind(instance).call(interpreter, args);
        }
        return instance;
    }

    @Override
    public int paramSize() {
        if (methods.containsKey("init")) {
            return methods.get("init").paramSize();
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "class:" + className;
    }

    public LoxFunction getMethod(String methodName) {
        return methods.get(methodName);
    }
    
}
