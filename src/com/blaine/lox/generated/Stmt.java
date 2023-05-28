// Generated File
package com.blaine.lox.generated;


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

}
