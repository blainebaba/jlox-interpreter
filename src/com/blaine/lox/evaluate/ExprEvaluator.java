package com.blaine.lox.evaluate;

import com.blaine.lox.generated.Expr.Binary;
import com.blaine.lox.generated.Expr.Grouping;
import com.blaine.lox.generated.Expr.Literal;
import com.blaine.lox.generated.Expr.Unary;
import com.blaine.lox.Token;
import com.blaine.lox.Token.TokenType;
import com.blaine.lox.generated.ExprVisitor;

import static com.blaine.lox.Token.TokenType.*;

import java.util.Arrays;

public class ExprEvaluator implements ExprVisitor<Object> {

    @Override
    public Object visitBinary(Binary binary) {
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
                    checkBinaryOperandType(left, right, operator, Double.class, String.class);
                    if (left.getClass() != right.getClass()) {
                        throw new RuntimeError(String.format("Operands type not match for '%s'", operator.lexeme), operator.line, operator.column);
                    }
                    if (left.getClass() == Double.class) {
                        return (double)left + (double)right;
                    } else {
                        return (String)left + (String)right;
                    }
                case MINUS:
                    checkBinaryOperandType(left, right, operator, Double.class);
                    return (double)left - (double)right;
                case SLASH:
                    checkBinaryOperandType(left, right, operator, Double.class);
                    return (double)left / (double)right;
                case STAR:
                    checkBinaryOperandType(left, right, operator, Double.class);
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
                    checkBinaryOperandType(left, right, operator, Double.class);
                    return (double)left < (double)right;
                case GREATER:
                    checkBinaryOperandType(left, right, operator, Double.class);
                    return (double)left > (double)right;
                case LESS_EQUAL:
                    checkBinaryOperandType(left, right, operator, Double.class);
                    return (double)left <= (double)right;
                case GREATER_EQUAL:
                    checkBinaryOperandType(left, right, operator, Double.class);
                    return (double)left >= (double)right;
                default:
                    throw new RuntimeException();
            }
        }
    }

    @Override
    public Object visitUnary(Unary unary) {
        Object value = unary.expr.accept(this);
        Token op = unary.operator;
        // arithmetic
        if (op.type == MINUS) {
            checkUnaryOperandType(value, op, Double.class);
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
    public Object visitGrouping(Grouping grouping) {
        return grouping.expr.accept(this);
    }

    @Override
    public Object visitLiteral(Literal literal) {
        return literal.value;
    }

    private void checkBinaryOperandType(Object left, Object right, Token operator, Class<?> ... types) {
        if (left == null || right == null) {
            throw new RuntimeError(String.format("Can't use nil as operand of '%s'", operator.lexeme), operator.line, operator.column);
        }

        boolean leftMatch = Arrays.stream(types).anyMatch(type -> left.getClass().equals(type));
        boolean rightMatch = Arrays.stream(types).anyMatch(type -> right.getClass().equals(type));
        if (!leftMatch || !rightMatch) {
            throw new RuntimeError(String.format("Incorrect type of operand for '%s'", operator.lexeme), operator.line, operator.column);
        }
    }

    private void checkUnaryOperandType(Object value, Token operator, Class<?> ... types) {
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
}
