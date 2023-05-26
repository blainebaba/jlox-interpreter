// Generated File
package com.blaine.lox.generated;

import com.blaine.lox.Token;

public interface Expr {

    <R> R accept(ExprVisitor<R> visitor);

    public static class Binary implements Expr{

        public final Expr left;
        public final Token operator;
        public final Expr right;

        public Binary(Expr left,Token operator,Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitBinary(this);
        }
    }

    public static class Unary implements Expr{

        public final Token operator;
        public final Expr expr;

        public Unary(Token operator,Expr expr) {
            this.operator = operator;
            this.expr = expr;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitUnary(this);
        }
    }

    public static class Grouping implements Expr{

        public final Expr expr;

        public Grouping(Expr expr) {
            this.expr = expr;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitGrouping(this);
        }
    }

    public static class Literal implements Expr{

        public final Object value;

        public Literal(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitLiteral(this);
        }
    }

}
