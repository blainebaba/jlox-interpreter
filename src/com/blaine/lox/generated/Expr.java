// Generated File
package com.blaine.lox.generated;

import com.blaine.lox.Token;

public interface Expr {

    <R> R accept(ExprVisitor<R> visitor);

    public static class BinaryExpr implements Expr{

        public final Expr left;
        public final Token operator;
        public final Expr right;

        public BinaryExpr(Expr left,Token operator,Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitBinaryExpr(this);
        }
    }

    public static class UnaryExpr implements Expr{

        public final Token operator;
        public final Expr expr;

        public UnaryExpr(Token operator,Expr expr) {
            this.operator = operator;
            this.expr = expr;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitUnaryExpr(this);
        }
    }

    public static class GroupingExpr implements Expr{

        public final Expr expr;

        public GroupingExpr(Expr expr) {
            this.expr = expr;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitGroupingExpr(this);
        }
    }

    public static class LiteralExpr implements Expr{

        public final Object value;
        public final Token token;

        public LiteralExpr(Object value,Token token) {
            this.value = value;
            this.token = token;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitLiteralExpr(this);
        }
    }

    public static class VariableExpr implements Expr{

        public final String varName;
        public final Token token;

        public VariableExpr(String varName,Token token) {
            this.varName = varName;
            this.token = token;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitVariableExpr(this);
        }
    }

    public static class AssignExpr implements Expr{

        public final String varName;
        public final Token var;
        public final Token equal;
        public final Expr expr;

        public AssignExpr(String varName,Token var,Token equal,Expr expr) {
            this.varName = varName;
            this.var = var;
            this.equal = equal;
            this.expr = expr;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitAssignExpr(this);
        }
    }

}
