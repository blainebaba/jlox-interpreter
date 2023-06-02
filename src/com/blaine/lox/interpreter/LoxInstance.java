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
        if (fields.containsKey(name)) {
            return fields.get(name);
        }

        LoxFunction method = (LoxFunction)this.klass.getMethod(name);
        if (method != null) {
            // add additional scope to include "this"
            return method.bind(this);
        } else {
            return null;
        }
    }

    public void set(String name, Object value) {
        fields.put(name, value);
    }
}
