package com.blaine.lox;

import com.blaine.lox.generated.ExprVisitor;
import com.blaine.lox.generated.Expr.AssignExpr;
import com.blaine.lox.generated.Expr.BinaryExpr;
import com.blaine.lox.generated.Expr.CallExpr;
import com.blaine.lox.generated.Expr.GroupingExpr;
import com.blaine.lox.generated.Expr.LiteralExpr;
import com.blaine.lox.generated.Expr.UnaryExpr;
import com.blaine.lox.generated.Expr.VariableExpr;

/**
 * Format Expr into a not quite pretty string.
 */
public class NotPrettyAstPrinter implements ExprVisitor<String> {

    @Override
    public String visitBinaryExpr(BinaryExpr binary) {
        return parantheses(binary.left.accept(this) + " " + binary.operator.lexeme + " " + binary.right.accept(this));
    }

    @Override
    public String visitUnaryExpr(UnaryExpr unaryexpr) {
        return parantheses(unaryexpr.operator.lexeme + " " + unaryexpr.expr.accept(this));
    }

    @Override
    public String visitGroupingExpr(GroupingExpr groupingexpr) {
        return parantheses(groupingexpr.expr.accept(this));
    }

    @Override
    public String visitLiteralExpr(LiteralExpr literalexpr) {
        return literalexpr.value.toString();
    }

    @Override
    public String visitVariableExpr(VariableExpr var) {
        return var.varName;
    }

    @Override
    public String visitAssignExpr(AssignExpr assignexpr) {
        return parantheses(assignexpr.varName + " = " + assignexpr.expr.accept(this));
    }

    @Override
    public String visitCallExpr(CallExpr call) {
        // TODO
        return "";
    }

    private String parantheses(String inner) {
        return "(" + inner + ")";
    }

}
