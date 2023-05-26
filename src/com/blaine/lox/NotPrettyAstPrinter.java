package com.blaine.lox;

import com.blaine.lox.Token.TokenType;
import com.blaine.lox.generated.ExprVisitor;
import com.blaine.lox.generated.Expr.Binary;
import com.blaine.lox.generated.Expr.Grouping;
import com.blaine.lox.generated.Expr.Literal;
import com.blaine.lox.generated.Expr.Unary;

/**
 * Format Expr into a not quite pretty string.
 */
public class NotPrettyAstPrinter implements ExprVisitor<String> {

    @Override
    public String visitBinary(Binary binary) {
        return parantheses(binary.left.accept(this) + " " + binary.operator.lexeme + " " + binary.right.accept(this));
    }

    @Override
    public String visitUnary(Unary unary) {
        return parantheses(unary.operator.lexeme + " " + unary.expr.accept(this));
    }

    @Override
    public String visitGrouping(Grouping grouping) {
        return parantheses(grouping.expr.accept(this));
    }

    @Override
    public String visitLiteral(Literal literal) {
        return literal.value.toString();
    }

    private String parantheses(String inner) {
        return "(" + inner + ")";
    }

    // Test
    public static void main(String[] args) {
        // 1 + 2 * 3
        Binary binary1 = new Binary(new Literal(2), new Token(TokenType.STAR, 0, 0, "*"), new Literal("3"));
        Binary binary2 = new Binary(new Literal(1), new Token(TokenType.PLUS, 0, 0, "+"), binary1);

        NotPrettyAstPrinter printer = new NotPrettyAstPrinter();
        String result = binary2.accept(printer);
        System.out.println(result);
    }
}
