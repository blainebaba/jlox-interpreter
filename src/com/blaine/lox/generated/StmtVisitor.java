// Generated File
package com.blaine.lox.generated;

import com.blaine.lox.generated.Stmt.ExpressionStmt;
import com.blaine.lox.generated.Stmt.PrintStmt;
import com.blaine.lox.generated.Stmt.DeclareStmt;
import com.blaine.lox.generated.Stmt.DecFunStmt;
import com.blaine.lox.generated.Stmt.BlockStmt;
import com.blaine.lox.generated.Stmt.IfStmt;
import com.blaine.lox.generated.Stmt.WhileStmt;
import com.blaine.lox.generated.Stmt.ReturnStmt;
import com.blaine.lox.generated.Stmt.ClassStmt;

public interface StmtVisitor<R> {

    R visitExpressionStmt(ExpressionStmt expressionstmt);
    R visitPrintStmt(PrintStmt printstmt);
    R visitDeclareStmt(DeclareStmt declarestmt);
    R visitDecFunStmt(DecFunStmt decfunstmt);
    R visitBlockStmt(BlockStmt blockstmt);
    R visitIfStmt(IfStmt ifstmt);
    R visitWhileStmt(WhileStmt whilestmt);
    R visitReturnStmt(ReturnStmt returnstmt);
    R visitClassStmt(ClassStmt classstmt);
}
