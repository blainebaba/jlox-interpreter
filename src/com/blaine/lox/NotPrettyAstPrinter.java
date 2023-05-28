package com.blaine.lox;

import com.blaine.lox.Token.TokenType;
import com.blaine.lox.generated.ExprVisitor;
import com.blaine.lox.generated.Expr.BinaryExpr;
import com.blaine.lox.generated.Expr.GroupingExpr;
import com.blaine.lox.generated.Expr.LiteralExpr;
import com.blaine.lox.generated.Expr.UnaryExpr;

/**
 * Format Expr into a not quite pretty string.
 */
public class NotPrettyAstPrinter implements ExprVisitor<String> {

    @Override
    public String visitBinaryExpr(BinaryExpr binaryexpr) {
        return parantheses(binaryexpr.left.accept(this) + " " + binaryexpr.operator.lexeme + " " + binaryexpr.right.accept(this));
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

    private String parantheses(String inner) {
        return "(" + inner + ")";
    }

    // Test
    public static void main(String[] args) {
        // 1 + 2 * 3
        BinaryExpr binaryexpr1 = new BinaryExpr(new LiteralExpr(2), new Token(TokenType.STAR, 0, 0, "*"), new LiteralExpr("3"));
        BinaryExpr binaryexpr2 = new BinaryExpr(new LiteralExpr(1), new Token(TokenType.PLUS, 0, 0, "+"), binaryexpr1);

        NotPrettyAstPrinter printer = new NotPrettyAstPrinter();
        String result = binaryexpr2.accept(printer);
        System.out.println(result);
    }
}
