// Generated File
package com.blaine.lox.generated;

import com.blaine.lox.generated.Expr.BinaryExpr;
import com.blaine.lox.generated.Expr.UnaryExpr;
import com.blaine.lox.generated.Expr.GroupingExpr;
import com.blaine.lox.generated.Expr.LiteralExpr;

public interface ExprVisitor<R> {

    R visitBinaryExpr(BinaryExpr binaryexpr);
    R visitUnaryExpr(UnaryExpr unaryexpr);
    R visitGroupingExpr(GroupingExpr groupingexpr);
    R visitLiteralExpr(LiteralExpr literalexpr);
}
