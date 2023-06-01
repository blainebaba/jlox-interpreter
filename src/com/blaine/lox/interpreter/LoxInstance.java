package com.blaine.lox.interpreter;

import java.util.HashMap;
import java.util.Map;

public class LoxInstance {

    public LoxClass klass;
    private Map<String, Object> fields;

    public LoxInstance(LoxClass klass) {
        this.klass = klass;
        fields = new HashMap<>();
    }

    @Override
    public String toString() {
        return "instace:" + klass.className;
    }

    public Object get(String name) {
        // TODO could also be methods
        return fields.get(name);
    }

    public void set(String name, Object value) {
        // TODO could also be methods?
        fields.put(name, value);
    }
}
