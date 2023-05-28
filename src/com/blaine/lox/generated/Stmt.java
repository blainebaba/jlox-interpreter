// Generated File
package com.blaine.lox.generated;

import java.util.List;

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

}
