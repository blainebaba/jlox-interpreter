// Generated File
package com.blaine.lox.generated;

import com.blaine.lox.generated.Stmt.ExpressionStmt;
import com.blaine.lox.generated.Stmt.PrintStmt;
import com.blaine.lox.generated.Stmt.DeclareStmt;
import com.blaine.lox.generated.Stmt.BlockStmt;
import com.blaine.lox.generated.Stmt.IfStmt;
import com.blaine.lox.generated.Stmt.WhileStmt;

public interface StmtVisitor<R> {

    R visitExpressionStmt(ExpressionStmt expressionstmt);
    R visitPrintStmt(PrintStmt printstmt);
    R visitDeclareStmt(DeclareStmt declarestmt);
    R visitBlockStmt(BlockStmt blockstmt);
    R visitIfStmt(IfStmt ifstmt);
    R visitWhileStmt(WhileStmt whilestmt);
}
