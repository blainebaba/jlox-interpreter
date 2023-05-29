package com.blaine.lox.interpreter;

import com.blaine.lox.generated.Expr.AssignExpr;
import com.blaine.lox.generated.Expr.BinaryExpr;
import com.blaine.lox.generated.Expr.CallExpr;
import com.blaine.lox.generated.Expr.GroupingExpr;
import com.blaine.lox.generated.Expr.LiteralExpr;
import com.blaine.lox.generated.Expr.UnaryExpr;
import com.blaine.lox.generated.Expr.VariableExpr;
import com.blaine.lox.generated.Stmt.BlockStmt;
import com.blaine.lox.generated.Stmt.DecFunStmt;
import com.blaine.lox.generated.Stmt.DeclareStmt;
import com.blaine.lox.generated.Stmt.ExpressionStmt;
import com.blaine.lox.generated.Stmt.IfStmt;
import com.blaine.lox.generated.Stmt.PrintStmt;
import com.blaine.lox.generated.Stmt.ReturnStmt;
import com.blaine.lox.generated.Stmt.WhileStmt;
import com.blaine.lox.Token;
import com.blaine.lox.Token.TokenType;
import com.blaine.lox.generated.Expr;
import com.blaine.lox.generated.ExprVisitor;
import com.blaine.lox.generated.Stmt;
import com.blaine.lox.generated.StmtVisitor;

import static com.blaine.lox.Token.TokenType.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Interpreter implements ExprVisitor<Object>, StmtVisitor<Void> {

    // root env
    private Environment rootEnv;
    private Environment curEnv;

    public Interpreter() {
        this.rootEnv = new Environment(null);
        this.curEnv = rootEnv;
    }

    public void execute(Stmt stmt) {
        stmt.accept(this);
    }

    // execute statment using given env
    public void execute(Stmt stmt, Environment env) {
        // preserve env
        // it is single threaded so it is fine.
        Environment oldEnv = curEnv;
        curEnv = env;

        try {
            stmt.accept(this);
        } finally {
            // restore env
            curEnv = oldEnv;
        }

    }

    ///////////////
    // Expressions
    ///////////////

    @Override
    public Object visitAssignExpr(AssignExpr assign) {
        Object value = assign.expr.accept(this);
        curEnv.assignVar(assign.varName, value, assign.var);
        return value;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binary) {
        Token operator = binary.operator;
        TokenType opType = operator.type;

        // AND and OR are special because they don't always evaluate both operands.
        if (opType == AND) {
            boolean left = toBoolean(binary.left.accept(this));
            return left ? toBoolean(binary.right.accept(this)) : false;
        }else if (opType == OR) {
            boolean left = toBoolean(binary.left.accept(this));
            return left ? true : toBoolean(binary.right.accept(this));
        } 

        // others
        {
            Object left = binary.left.accept(this);
            Object right = binary.right.accept(this);

            switch (opType) {
                case PLUS:
                    checkBinaryExprOperandType(left, right, operator, Double.class, String.class);
                    if (left.getClass() != right.getClass()) {
                        throw new RuntimeError(String.format("Operands type not match for '%s'", operator.lexeme), operator.line, operator.column);
                    }
                    if (left.getClass() == Double.class) {
                        return (double)left + (double)right;
                    } else {
                        return (String)left + (String)right;
                    }
                case MINUS:
                    checkBinaryExprOperandType(left, right, operator, Double.class);
                    return (double)left - (double)right;
                case SLASH:
                    checkBinaryExprOperandType(left, right, operator, Double.class);
                    return (double)left / (double)right;
                case STAR:
                    checkBinaryExprOperandType(left, right, operator, Double.class);
                    return (double)left * (double)right;

                case EQUAL_EQAUL:
                    if (left == null && right == null) return true;
                    if (left == null || right == null) return false;
                    return left.equals(right);
                case NOT_EQUAL:
                    if (left == null && right == null) return false;
                    if (left == null || right == null) return true;
                    return !left.equals(right);

                case LESSER:
                    checkBinaryExprOperandType(left, right, operator, Double.class);
                    return (double)left < (double)right;
                case GREATER:
                    checkBinaryExprOperandType(left, right, operator, Double.class);
                    return (double)left > (double)right;
                case LESS_EQUAL:
                    checkBinaryExprOperandType(left, right, operator, Double.class);
                    return (double)left <= (double)right;
                case GREATER_EQUAL:
                    checkBinaryExprOperandType(left, right, operator, Double.class);
                    return (double)left >= (double)right;
                default:
                    throw new RuntimeException();
            }
        }
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unary) {
        Object value = unary.expr.accept(this);
        Token op = unary.operator;
        // arithmetic
        if (op.type == MINUS) {
            checkUnaryExprOperandType(value, op, Double.class);
            return -(Double)value;
        }
        // logical
        else if (op.type == EXCLAM) {
            return !(Boolean)toBoolean(value);
        }

        // exception of interpreter
        throw new RuntimeException();
    }

    @Override
    public Object visitCallExpr(CallExpr call) {
        // this should evaluate to a Callable
        Object fun = call.fun.accept(this);
        Token callToken = call.call;

        if (!(fun instanceof LoxCallable)) {
            throw new RuntimeError("Call a non-function value.", callToken.line, callToken.column);
        }

        List<Object> args = new ArrayList<>();
        for (Expr arg : call.args) {
            args.add(arg.accept(this));
        }

        LoxCallable funCallable = (LoxCallable)fun;

        if (args.size() != funCallable.paramSize()) {
            throw new RuntimeError("Function call with incorrect number of arguments.", callToken.line, callToken.column);
        }

        return ((LoxCallable)fun).call(this, args);
    }

    @Override
    public Object visitGroupingExpr(GroupingExpr group) {
        return group.expr.accept(this);
    }

    @Override
    public Object visitLiteralExpr(LiteralExpr literal) {
        return literal.value;
    }

    @Override
    public Object visitVariableExpr(VariableExpr var) {
        return curEnv.evaluateVar(var.varName, var.token);
    }

    private void checkBinaryExprOperandType(Object left, Object right, Token operator, Class<?> ... types) {
        if (left == null || right == null) {
            throw new RuntimeError(String.format("Can't use nil as operand of '%s'", operator.lexeme), operator.line, operator.column);
        }

        boolean leftMatch = Arrays.stream(types).anyMatch(type -> left.getClass().equals(type));
        boolean rightMatch = Arrays.stream(types).anyMatch(type -> right.getClass().equals(type));
        if (!leftMatch || !rightMatch) {
            throw new RuntimeError(String.format("Incorrect type of operand for '%s'", operator.lexeme), operator.line, operator.column);
        }
    }

    private void checkUnaryExprOperandType(Object value, Token operator, Class<?> ... types) {
        if (value == null) {
            throw new RuntimeError(String.format("Can't use nil as operand of '%s'", operator.lexeme), operator.line, operator.column);
        }

        boolean isMatched = Arrays.stream(types).anyMatch(type -> value.getClass().equals(type));
        if (!isMatched) {
            throw new RuntimeError(String.format("Incorrect type of operand for '%s'", operator.lexeme), operator.line, operator.column);
        }
    }

    // implicit convert to boolean if necessary.
    // false and nil are false, otherwise true.
    private boolean toBoolean(Object obj) {
        if (obj == null) return false;
        if (obj.getClass().equals(Boolean.class))  return (Boolean)obj;
        return true;
    }

    ////////////
    // statements
    ///////////

    @Override
    public Void visitExpressionStmt(ExpressionStmt stmt) {
        // ignore result
        stmt.expression.accept(this);
        return null;
    }

    @Override
    public Void visitPrintStmt(PrintStmt stmt) {
        Object result = stmt.expression.accept(this);
        System.out.println(result);
        return null;
    }

    @Override
    public Void visitDeclareStmt(DeclareStmt stmt) {
        Object initValue = null;
        if (stmt.expression != null) {
            initValue = stmt.expression.accept(this);
        }
        curEnv.declareVar(stmt.varName, initValue);
        return null;
    }

    @Override
    public Void visitDecFunStmt(DecFunStmt declare) {
        String funName = (String)declare.funName.literalValue;
        List<String> params = declare.params.stream().map(t -> (String)t.literalValue).collect(Collectors.toList());
        LoxFunction function = new LoxFunction(funName, params, declare.stmts, curEnv);
        curEnv.declareVar(funName, function);
        return null;
    }

    @Override
    public Void visitBlockStmt(BlockStmt blockstmt) {
        // TODO lexical scope
        pushEnv();
        for (Stmt stmt : blockstmt.stmts) {
            stmt.accept(this);
        }
        popEnv();
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmt ifstmt) {
        if (toBoolean(ifstmt.cond.accept(this))) {
            ifstmt.ifClause.accept(this);
        }
        else if (ifstmt.elseClause != null) {
            ifstmt.elseClause.accept(this);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmt whilestmt) {
        while (toBoolean(whilestmt.cond.accept(this))) {
            whilestmt.stmt.accept(this);
        }
        return null;
    }

    @Override
    public Void visitReturnStmt(ReturnStmt stmt) {
        Object value = null;
        if (stmt.value != null) {
            value = stmt.value.accept(this);
        }
        throw new ReturnThrowable(value, stmt.token);
    }

    ///////////
    // Helper methods 
    ///////////

    Environment getEnv() {
        return curEnv;
    }

    // push one more level of Env
    private void pushEnv() {
        Environment env = new Environment(curEnv);
        curEnv = env;
    }
    
    // back to previous env level
    private void popEnv() {
        curEnv = curEnv.outer;
    }
}
