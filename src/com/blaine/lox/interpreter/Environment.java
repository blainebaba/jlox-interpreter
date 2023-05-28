package com.blaine.lox.interpreter;

import java.util.HashMap;
import java.util.Map;

/**
 * Runtime environment.
 */
public class Environment {

    private Map<String, Object> globalVariables;

    public Environment() {
        globalVariables = new HashMap<>();
    }

    public void declareGlobalVar(String name, Object initValue) {
        globalVariables.put(name, initValue);
    }

    public Object evaluateGlobalVar(String name) {
        return globalVariables.get(name);
    }

}
