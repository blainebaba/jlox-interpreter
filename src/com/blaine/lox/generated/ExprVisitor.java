// Generated File
package com.blaine.lox.generated;

import com.blaine.lox.generated.Expr.BinaryExpr;
import com.blaine.lox.generated.Expr.UnaryExpr;
import com.blaine.lox.generated.Expr.GroupingExpr;
import com.blaine.lox.generated.Expr.LiteralExpr;
import com.blaine.lox.generated.Expr.VariableExpr;
import com.blaine.lox.generated.Expr.AssignExpr;
import com.blaine.lox.generated.Expr.CallExpr;

public interface ExprVisitor<R> {

    R visitBinaryExpr(BinaryExpr binaryexpr);
    R visitUnaryExpr(UnaryExpr unaryexpr);
    R visitGroupingExpr(GroupingExpr groupingexpr);
    R visitLiteralExpr(LiteralExpr literalexpr);
    R visitVariableExpr(VariableExpr variableexpr);
    R visitAssignExpr(AssignExpr assignexpr);
    R visitCallExpr(CallExpr callexpr);
}
