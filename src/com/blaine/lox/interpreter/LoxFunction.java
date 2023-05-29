package com.blaine.lox.interpreter;

import java.util.List;

import com.blaine.lox.generated.Stmt;

// java representation of lox function.
public class LoxFunction implements LoxCallable {
    public String funName;
    public List<String> paramNames;
    public List<Stmt> stmts;
    public Environment envClosure;

    public LoxFunction(String funName, List<String> params, List<Stmt> stmts, Environment env) {
        this.funName = funName;
        this.paramNames = params;
        this.stmts = stmts;
        this.envClosure = env;
    }

    @Override
    public int paramSize() {
        return paramNames.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        Environment funEnv = new Environment(envClosure);

        for (int i = 0; i < args.size(); i ++) {
            String varName = paramNames.get(i);
            Object value = args.get(i);
            funEnv.declareVar(varName, value);
        }

        try {
            for (Stmt stmt : stmts) {
                interpreter.execute(stmt, funEnv);
            }
            return null;
        } catch (ReturnThrowable e) {
            return e.returnValue;
        }
    }
}
