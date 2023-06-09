package com.blaine.lox.parser;

import com.blaine.lox.generated.Expr.AssignExpr;
import com.blaine.lox.generated.Expr.BinaryExpr;
import com.blaine.lox.generated.Expr.CallExpr;
import com.blaine.lox.generated.Expr.GetExpr;
import com.blaine.lox.generated.Expr.GroupingExpr;
import com.blaine.lox.generated.Expr.LiteralExpr;
import com.blaine.lox.generated.Expr.SetExpr;
import com.blaine.lox.generated.Expr.SuperExpr;
import com.blaine.lox.generated.Expr.ThisExpr;
import com.blaine.lox.generated.Expr.UnaryExpr;
import com.blaine.lox.generated.Expr.VariableExpr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.blaine.lox.Token;
import com.blaine.lox.generated.Expr;
import com.blaine.lox.generated.ExprVisitor;
import com.blaine.lox.generated.Stmt;
import com.blaine.lox.generated.Stmt.BlockStmt;
import com.blaine.lox.generated.Stmt.ClassStmt;
import com.blaine.lox.generated.Stmt.DecFunStmt;
import com.blaine.lox.generated.Stmt.DeclareStmt;
import com.blaine.lox.generated.Stmt.ExpressionStmt;
import com.blaine.lox.generated.Stmt.IfStmt;
import com.blaine.lox.generated.Stmt.PrintStmt;
import com.blaine.lox.generated.Stmt.ReturnStmt;
import com.blaine.lox.generated.Stmt.WhileStmt;
import com.blaine.lox.interpreter.Interpreter;
import com.blaine.lox.generated.StmtVisitor;

// resolve variable references, and some other static analysis
// 
// Variable reference is done by calculating scope levels differences between definition and reference.
// the way scope changes here must be same as interpreter in order for it to work.
public class VarResolver implements ExprVisitor<Void>, StmtVisitor<Void> {

    // bool value indicates whether this var is ready to be used.
    private Stack<Map<String,Boolean>> scopes;
    private Interpreter interpreter;
    private Stack<FunType> funType;
    private Stack<ClassType> classType;

    // current function context, use this to identify invalid "return" and "this" usage.
    public static enum FunType {
        NONE, FUNCTION, METHOD, INITIALIZER
    }

    public static enum ClassType {
        // SUPER: a class with super
        NONE, CLASS, SUPER
    }

    public VarResolver(Interpreter interpreter) {
        this.interpreter = interpreter;

        this.scopes = new Stack<>();
        // init root scope
        // root scope contains built-in vars, also may contains previously defined vars in REPL mode.
        Map<String, Boolean> rootScope = new HashMap<>();
        for (String globalVar : interpreter.getRootEnv().listLocalVars()) {
            rootScope.put(globalVar, true);
        }
        this.scopes.push(rootScope);
        funType = new Stack<>();
        classType = new Stack<>();
    }

    public void resolve(List<Stmt> stmts) {
        funType.push(FunType.NONE);
        classType.push(ClassType.NONE);
        for (Stmt stmt : stmts) {
            stmt.accept(this);
        }
        classType.pop();
        funType.pop();
     }

    public void resolveExpression(Expr expr) {
        expr.accept(this);
    }

    //////////////////
    // Helper Methods
    //////////////////

    private void declareVar(String varName) {
        if (scopes.peek().containsKey(varName)) {
            throw new ParserError("Duplicate local variable definition.");
        }
        scopes.peek().put(varName, false);
    }

    private void defineVar(String varName) {
        scopes.peek().put(varName, true);
    }

    // calcualte scopes diff between var definition and usage.
    private int calculateDiff(String varName) {
        for (int i = scopes.size() -1; i >=0; i --) {
            if (scopes.get(i).containsKey(varName)) {
                if (scopes.get(i).get(varName)) {
                    return scopes.size() - 1 - i;
                } else {
                    // var not ready, in "var a = a;" cases.
                    throw new ParserError("Can not access variable in its initializer.");
                }
            }
        }
        throw new ParserError(String.format("Undefined variable '%s'", varName));
    }

    /////////////////
    // Statements
    /////////////////

    @Override
    public Void visitExpressionStmt(ExpressionStmt stmt) {
        stmt.expression.accept(this);
        return null;
    }

    @Override
    public Void visitPrintStmt(PrintStmt stmt) {
        stmt.expression.accept(this);
        return null;
    }

    @Override
    public Void visitDeclareStmt(DeclareStmt stmt) {
        declareVar(stmt.varName);
        if (stmt.expression != null) {
            stmt.expression.accept(this);
        }
        defineVar(stmt.varName);
        return null;
    }

    @Override
    public Void visitDecFunStmt(DecFunStmt funStmt) {
        funType.push(FunType.FUNCTION);
        resolveFunction(funStmt);
        funType.pop();
        return null;
    }

    private void resolveFunction(DecFunStmt funStmt) {
        String funName = (String)funStmt.funName.literalValue;
        defineVar(funName);
        
        scopes.push(new HashMap<>());
        for (Token param: funStmt.params) {
            defineVar((String)param.literalValue);
        }
        for (Stmt stmt : funStmt.stmts) {
            stmt.accept(this);
        }
        scopes.pop();
    }

    @Override
    public Void visitClassStmt(ClassStmt klass) {
        defineVar((String)klass.name.literalValue);

        ClassType type = ClassType.CLASS;
        if (klass.superClass != null) {
            klass.superClass.accept(this);
            type = ClassType.SUPER;
        }

        // add additional scope for methods
        scopes.push(new HashMap<>());
        classType.push(type);

        for (DecFunStmt funStmt : klass.methods) {
            if (funStmt.funName.equals("init")) {
                funType.push(FunType.INITIALIZER);
            } else {
                funType.push(FunType.METHOD);
            }
            resolveFunction(funStmt);
            funType.pop();
        }

        classType.pop();
        scopes.pop();

        return null;
    }

    @Override
    public Void visitBlockStmt(BlockStmt block) {
        scopes.push(new HashMap<>());
        for (Stmt stmt : block.stmts) {
            stmt.accept(this);
        }
        scopes.pop();
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmt stmt) {
        stmt.cond.accept(this);
        stmt.ifClause.accept(this);
        if (stmt.elseClause != null) stmt.elseClause.accept(this);

        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmt stmt) {
        stmt.cond.accept(this);
        stmt.stmt.accept(this);
        return null;
    }

    @Override
    public Void visitReturnStmt(ReturnStmt stmt) {
        if (funType.peek() == FunType.NONE) {
            throw new ParserError("Can't use 'return' outside of function.", stmt.token.line, stmt.token.column);
        } else if (funType.peek() == FunType.INITIALIZER) {
            throw new ParserError("Can't use 'return' in initializer.", stmt.token.line, stmt.token.column);
        }
        if (stmt.value != null) {
            stmt.value.accept(this);
        }
        return null;
    }

    /////////////////
    // Expressions 
    /////////////////

    @Override
    public Void visitBinaryExpr(BinaryExpr expr) {
        expr.left.accept(this);
        expr.right.accept(this);
        return null;
    }

    @Override
    public Void visitUnaryExpr(UnaryExpr unary) {
        unary.expr.accept(this);
        return null;
    }

    @Override
    public Void visitGroupingExpr(GroupingExpr group) {
        group.expr.accept(this);
        return null;
    }

    @Override
    public Void visitLiteralExpr(LiteralExpr literalexpr) {
        return null;
    }

    @Override
    public Void visitVariableExpr(VariableExpr varExpr) {
        int diff;
        if (varExpr.varName.equals("this")) {
            // always fixed.
            diff = 1;
            if (funType.peek() != FunType.METHOD && funType.peek() != FunType.INITIALIZER) {
                throw new ParserError("Can't use 'this' out side of methods.", varExpr.token.line, varExpr.token.column);
            }
        } else {
            diff = calculateDiff(varExpr.varName);
        }
        interpreter.storeVarResolution(varExpr, diff);
        return null;
    }

    @Override
    public Void visitAssignExpr(AssignExpr expr) {
        int diff = calculateDiff(expr.varName);
        interpreter.storeVarResolution(expr, diff);

        expr.expr.accept(this);
        return null;
    }

    @Override
    public Void visitCallExpr(CallExpr call) {
        call.fun.accept(this);
        for (Expr arg : call.args) {
            arg.accept(this);
        }
        return null;
    }

    // properties belong to instance so it doesn't need resolve.
    @Override
    public Void visitGetExpr(GetExpr get) {
        get.obj.accept(this);
        return null;
    }


    @Override
    public Void visitSetExpr(SetExpr set) {
        set.obj.accept(this);
        set.value.accept(this);
        return null;
    }

    @Override
    public Void visitThisExpr(ThisExpr expr) {
        if (classType.peek() == ClassType.NONE) {
            throw new ParserError("Can't use 'this' outside of class.", expr.token.line, expr.token.column);
        }
        // fix value
        interpreter.storeVarResolution(expr, 1);
        return null;
    }

    @Override
    public Void visitSuperExpr(SuperExpr expr) {
        if (classType.peek() == ClassType.NONE) {
            throw new ParserError("Can't use 'super' outside of class.", expr.token.line, expr.token.column);
        }
        if (classType.peek() == ClassType.CLASS) {
            throw new ParserError("Can't use 'super' on class without super class.", expr.token.line, expr.token.column);
        }
        // fix value
        interpreter.storeVarResolution(expr, 2);
        return null;
    }
}
