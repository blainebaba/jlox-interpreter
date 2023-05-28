package com.blaine.lox.interpreter;

import com.blaine.lox.generated.Expr.AssignExpr;
import com.blaine.lox.generated.Expr.BinaryExpr;
import com.blaine.lox.generated.Expr.GroupingExpr;
import com.blaine.lox.generated.Expr.LiteralExpr;
import com.blaine.lox.generated.Expr.UnaryExpr;
import com.blaine.lox.generated.Expr.VariableExpr;
import com.blaine.lox.generated.Stmt.DeclareStmt;
import com.blaine.lox.generated.Stmt.ExpressionStmt;
import com.blaine.lox.generated.Stmt.PrintStmt;
import com.blaine.lox.Token;
import com.blaine.lox.Token.TokenType;
import com.blaine.lox.generated.ExprVisitor;
import com.blaine.lox.generated.Stmt;
import com.blaine.lox.generated.StmtVisitor;

import static com.blaine.lox.Token.TokenType.*;

import java.util.Arrays;

public class Interpreter implements ExprVisitor<Object>, StmtVisitor<Void> {

    private Environment env;

    public Interpreter() {
        this.env = new Environment();
    }

    public void execute(Stmt stmt) {
        stmt.accept(this);
    }

    ///////////////
    // Expressions
    ///////////////

    @Override
    public Object visitAssignExpr(AssignExpr assign) {
        Object value = assign.expr.accept(this);
        env.assignGlobalVar(assign.varName, value, assign.var);
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
    public Object visitGroupingExpr(GroupingExpr group) {
        return group.expr.accept(this);
    }

    @Override
    public Object visitLiteralExpr(LiteralExpr literal) {
        return literal.value;
    }

    @Override
    public Object visitVariableExpr(VariableExpr var) {
        return env.evaluateGlobalVar(var.varName, var.token);
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
        env.declareGlobalVar(stmt.varName, initValue);
        return null;
    }

    Environment getEnv() {
        return env;
    }
}
