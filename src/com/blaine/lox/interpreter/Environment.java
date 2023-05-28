package com.blaine.lox.interpreter;

import java.util.HashMap;
import java.util.Map;

import com.blaine.lox.Token;

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

    public Object evaluateGlobalVar(String varName, Token var) {
        if (!containsGlobalVar(varName)) {
            throw new RuntimeError(String.format("Undefined variable '%s'", varName), var.line, var.column);
        }
        return globalVariables.get(varName);
    }

    public boolean containsGlobalVar(String name) {
        return globalVariables.containsKey(name);
    }

    public void assignGlobalVar(String varName, Object value, Token var) {
        if (!containsGlobalVar(varName)) {
            throw new RuntimeError(String.format("Undefined variable '%s'", varName), var.line, var.column);
        }
        globalVariables.put(varName, value);
    }

    // read global variable, without checking existance
    public Object getGlobalVar(String varName) {
        return globalVariables.get(varName);
    }

}
