// Generated File
package com.blaine.lox.generated;

import com.blaine.lox.Token;
import java.util.List;

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

    public static class CallExpr implements Expr{

        public final Expr fun;
        public final Token call;
        public final List<Expr> args;

        public CallExpr(Expr fun,Token call,List<Expr> args) {
            this.fun = fun;
            this.call = call;
            this.args = args;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitCallExpr(this);
        }
    }

    public static class GetExpr implements Expr{

        public final Expr obj;
        public final Token dot;
        public final Token field;

        public GetExpr(Expr obj,Token dot,Token field) {
            this.obj = obj;
            this.dot = dot;
            this.field = field;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitGetExpr(this);
        }
    }

    public static class SetExpr implements Expr{

        public final Expr obj;
        public final Token dot;
        public final Token field;
        public final Token equal;
        public final Expr value;

        public SetExpr(Expr obj,Token dot,Token field,Token equal,Expr value) {
            this.obj = obj;
            this.dot = dot;
            this.field = field;
            this.equal = equal;
            this.value = value;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitSetExpr(this);
        }
    }

    public static class ThisExpr implements Expr{

        public final Token token;

        public ThisExpr(Token token) {
            this.token = token;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitThisExpr(this);
        }
    }

    public static class SuperExpr implements Expr{

        public final Token token;
        public final Token method;

        public SuperExpr(Token token,Token method) {
            this.token = token;
            this.method = method;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor){
            return visitor.visitSuperExpr(this);
        }
    }

}
