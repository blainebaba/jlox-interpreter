package com.blaine.lox.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blaine.lox.Token;

/**
 * Runtime environment. With resolver resolves var references, we can access vars confidently in runtime.
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

    public Object evaluateVar(String varName, Token var) {
        return vars.get(varName);
    }

    public boolean localContainsVar(String name) {
        return vars.containsKey(name);
    }

    public void assignVar(String varName, Object value, Token var) {
        vars.put(varName, value);
    }

    // get local vars, for testing
    public Object getVar(String varName) {
        return vars.get(varName);
    }

    public List<String> listLocalVars() {
        return new ArrayList<>(vars.keySet());
    }
}
