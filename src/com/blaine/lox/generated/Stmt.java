// Generated File
package com.blaine.lox.generated;

import java.util.List;
import com.blaine.lox.Token;
import com.blaine.lox.generated.Expr.VariableExpr;

public interface Stmt {

    <R> R accept(StmtVisitor<R> visitor);

    public static class ExpressionStmt implements Stmt{

        public final Expr expression;

        public ExpressionStmt(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor){
            return visitor.visitExpressionStmt(this);
        }
    }

    public static class PrintStmt implements Stmt{

        public final Expr expression;

        public PrintStmt(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor){
            return visitor.visitPrintStmt(this);
        }
    }

    public static class DeclareStmt implements Stmt{

        public final String varName;
        public final Expr expression;

        public DeclareStmt(String varName,Expr expression) {
            this.varName = varName;
            this.expression = expression;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor){
            return visitor.visitDeclareStmt(this);
        }
    }

    public static class DecFunStmt implements Stmt{

        public final Token funName;
        public final List<Token> params;
        public final List<Stmt> stmts;

        public DecFunStmt(Token funName,List<Token> params,List<Stmt> stmts) {
            this.funName = funName;
            this.params = params;
            this.stmts = stmts;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor){
            return visitor.visitDecFunStmt(this);
        }
    }

    public static class BlockStmt implements Stmt{

        public final List<Stmt> stmts;

        public BlockStmt(List<Stmt> stmts) {
            this.stmts = stmts;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor){
            return visitor.visitBlockStmt(this);
        }
    }

    public static class IfStmt implements Stmt{

        public final Expr cond;
        public final Stmt ifClause;
        public final Stmt elseClause;

        public IfStmt(Expr cond,Stmt ifClause,Stmt elseClause) {
            this.cond = cond;
            this.ifClause = ifClause;
            this.elseClause = elseClause;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor){
            return visitor.visitIfStmt(this);
        }
    }

    public static class WhileStmt implements Stmt{

        public final Expr cond;
        public final Stmt stmt;

        public WhileStmt(Expr cond,Stmt stmt) {
            this.cond = cond;
            this.stmt = stmt;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor){
            return visitor.visitWhileStmt(this);
        }
    }

    public static class ReturnStmt implements Stmt{

        public final Expr value;
        public final Token token;

        public ReturnStmt(Expr value,Token token) {
            this.value = value;
            this.token = token;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor){
            return visitor.visitReturnStmt(this);
        }
    }

    public static class ClassStmt implements Stmt{

        public final Token name;
        public final VariableExpr superClass;
        public final List<DecFunStmt> methods;

        public ClassStmt(Token name,VariableExpr superClass,List<DecFunStmt> methods) {
            this.name = name;
            this.superClass = superClass;
            this.methods = methods;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor){
            return visitor.visitClassStmt(this);
        }
    }

}
