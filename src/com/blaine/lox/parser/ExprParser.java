package com.blaine.lox.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.blaine.lox.Token;
import com.blaine.lox.Token.TokenType;
import com.blaine.lox.generated.Expr;
import com.blaine.lox.generated.Expr.AssignExpr;
import com.blaine.lox.generated.Expr.BinaryExpr;
import com.blaine.lox.generated.Expr.CallExpr;
import com.blaine.lox.generated.Expr.GroupingExpr;
import com.blaine.lox.generated.Expr.LiteralExpr;
import com.blaine.lox.generated.Expr.UnaryExpr;
import com.blaine.lox.generated.Expr.VariableExpr;

import static com.blaine.lox.Token.TokenType.*;

/**
 * This is basically translating parsing rules into code.
 */
public class ExprParser {

    private Parser p;

    public ExprParser(Parser parser) {
        this.p = parser;
    }

    // entrance of parsing expression 
    public Expr expression() {
        return assignTerm();
    }

    public Expr assignTerm() {
        Expr left = orTerm();
        if (p.peek(EQUAL)) {
            if (left.getClass().equals(VariableExpr.class)) {
                VariableExpr var = (VariableExpr)left;
                Token equal = p.consume();
                Expr right = assignTerm();
                return new AssignExpr(var.varName, var.token, equal, right);
            } else {
                Token equal = p.cur();
                throw new ParserError("Invalid assignment target.", equal.line, equal.column);
            }
        }
        return left;
    }

    private Expr orTerm() {
        return leftBinaryExprRule(this::andTerm, OR);
    }

    private Expr andTerm() {
        return leftBinaryExprRule(this::equalTerm, AND);
    }

    private Expr equalTerm() {
        return leftBinaryExprRule(this::compareTerm, EQUAL_EQAUL, NOT_EQUAL);
    }

    private Expr compareTerm() {
        return leftBinaryExprRule(this::addTerm, LESSER, GREATER, LESS_EQUAL, GREATER_EQUAL);
    }

    private Expr addTerm() {
        return leftBinaryExprRule(this::mulTerm, PLUS, MINUS);
    }

    private Expr mulTerm() {
        return leftBinaryExprRule(this::unaryexprTerm, STAR, SLASH);
    }

    private Expr unaryexprTerm() {
        if (p.peek(MINUS, EXCLAM)) {
            return new UnaryExpr(p.consume(), unaryexprTerm());
        } else {
            return callTerm();
        }
    }

    private Expr callTerm() {
        Expr primary = primary();
        if (p.peek(LEFT_PAREN)) {
            Token call = p.match(LEFT_PAREN);

            List<Expr> args = new ArrayList<>();
            if (p.peek(RIGHT_PAREN)) {
                p.match(RIGHT_PAREN);
            } else {
                args.add(p.parseExpression());
                while (p.peek(COMMA)) {
                    p.match(COMMA);
                    args.add(p.parseExpression());
                }
                p.match(RIGHT_PAREN);
            }

            return new CallExpr(primary, call, args);
        } else {
            return primary;
        }
    }

    private Expr primary() {
        if (p.peek(IDENTIFIER)) {
            Token var = p.consume();
            return new VariableExpr((String)var.literalValue, var);
        } else if (p.peek(NUMBER, STRING)) {
            Token token = p.consume();
            return new LiteralExpr(token.literalValue, token);
        } else if (p.peek(TRUE)) {
            Token token = p.consume();
            return new LiteralExpr(true, token);
        } else if (p.peek(FALSE)) {
            Token token = p.consume();
            return new LiteralExpr(false, token);
        } else if (p.peek(LEFT_PAREN)){
            p.consume();
            Expr expr = expression();
            p.match(RIGHT_PAREN);
            return new GroupingExpr(expr);
        } else if (p.peek(NIL)) {
            Token token = p.consume();
            return new LiteralExpr(null, token);
        } else {
            if (p.isEnd()) {
                throw new ParserError("Unexpected end of file.");
            } else {
                throw new ParserError(String.format("Unexpected token '%s'", p.cur().lexeme), p.cur().line, p.cur().column);
            }
        }
    }

    /**
     * left associates binaryexpr operator rule
     */
    private Expr leftBinaryExprRule(Supplier<Expr> nextRule, TokenType ... ops) {
        Expr expr = nextRule.get();
        while (p.peek(ops)) {
            Token operator = p.consume();
            expr = new BinaryExpr(expr, operator, nextRule.get());
        }
        return expr;
    }
}
