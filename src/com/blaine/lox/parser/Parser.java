package com.blaine.lox.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.blaine.lox.Token;
import com.blaine.lox.Token.TokenType;
import com.blaine.lox.generated.Expr;
import com.blaine.lox.generated.Stmt;

/**
 * Entrance of parser. Storing variables of parsing process.
 */
public class Parser {

    private List<Token> tokens;
    // index of current token
    private int cur;

    private ExprParser exprParser;
    private StmtParser stmtParser;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.cur = 0;

        this.exprParser = new ExprParser(this);
        this.stmtParser = new StmtParser(this);
    }

    // parse the whole program
    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while(!isEnd()) {
            statements.add(parseStatement());
        }
        return statements;
    }

    public Stmt parseStatement() {
        return stmtParser.statement();
    }

    public Expr parseExpression() {
        return exprParser.expression();
    }

    /////////////////
    // Helper methods
    /////////////////

    // return true if current token matches given type, otherwise return false
    // similar to match method, but not consumes token
    boolean peek(TokenType ... expectedTypes) {
        List<TokenType> expectedTypeList = Arrays.asList(expectedTypes);
        if (!isEnd() && expectedTypeList.contains(cur().type)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check whether current token matches given types, consume token if type matches.
     * @param types
     * @return matched token
     */
    Token match(TokenType ... expectedTypes) {
        List<TokenType> expectedTypeList = Arrays.asList(expectedTypes);
        if (!isEnd() && expectedTypeList.contains(cur().type)) {
            return consume();
        } else {
            throw new ParserError();
        }
    }

    // consume and return current token, move cur pointer to next token
    Token consume() {
        return tokens.get(cur++);
    }

    public boolean isEnd() {
        return cur >= tokens.size();
    }

    // return current token
    Token cur() {
        return tokens.get(cur);
    }
}
