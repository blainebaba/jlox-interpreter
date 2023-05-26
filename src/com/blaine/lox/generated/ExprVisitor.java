// Generated File
package com.blaine.lox.generated;

import com.blaine.lox.generated.Expr.Binary;
import com.blaine.lox.generated.Expr.Unary;
import com.blaine.lox.generated.Expr.Grouping;
import com.blaine.lox.generated.Expr.Literal;

public interface ExprVisitor<R> {

    R visitBinary(Binary binary);
    R visitUnary(Unary unary);
    R visitGrouping(Grouping grouping);
    R visitLiteral(Literal literal);
}
