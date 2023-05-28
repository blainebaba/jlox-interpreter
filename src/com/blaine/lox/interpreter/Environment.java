package com.blaine.lox.interpreter;

import java.util.HashMap;
import java.util.Map;

import com.blaine.lox.Token;

/**
 * Runtime environment.
 */
public class Environment {

    // env of outer scope
    public final Environment outer;

    private Map<String, Object> vars = new HashMap<>();

    public Environment(Environment outer) {
        this.outer = outer;
    }

    public void declareVar(String name, Object initValue) {
        vars.put(name, initValue);
    }

    // if current env doesn't have, go to outer scope.
    public Object evaluateVar(String varName, Token var) {
        if (localContainsVar(varName)) {
            return vars.get(varName);
        }

        if (outer != null) {
            return outer.evaluateVar(varName, var);
        }

        throw new RuntimeError(String.format("Undefined variable '%s'", varName), var.line, var.column);
    }

    // if current scope has variable
    public boolean localContainsVar(String name) {
        return vars.containsKey(name);
    }

    public void assignVar(String varName, Object value, Token var) {
        if (localContainsVar(varName)) {
            vars.put(varName, value);
            return;
        }

        if (outer != null) {
            outer.assignVar(varName, value, var);
            return;
        }

        throw new RuntimeError(String.format("Undefined variable '%s'", varName), var.line, var.column);
    }

    // read variable, without checking existance
    public Object getVar(String varName) {
        if (localContainsVar(varName)) {
            return vars.get(varName);
        }

        if (outer != null) {
            return outer.getVar(varName);
        }

        return null;
    }
}
