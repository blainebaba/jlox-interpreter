// Generated File
package com.blaine.lox.generated;

import com.blaine.lox.generated.Stmt.ExpressionStmt;
import com.blaine.lox.generated.Stmt.PrintStmt;
import com.blaine.lox.generated.Stmt.DeclareStmt;

public interface StmtVisitor<R> {

    R visitExpressionStmt(ExpressionStmt expressionstmt);
    R visitPrintStmt(PrintStmt printstmt);
    R visitDeclareStmt(DeclareStmt declarestmt);
}
