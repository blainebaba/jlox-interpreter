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

        public LiteralExpr(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitLiteralExpr(this);
        }
    }

}
