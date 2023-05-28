package com.blaine.lox.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.blaine.lox.Token;
import com.blaine.lox.Token.TokenType;
import com.blaine.lox.generated.Expr;
import com.blaine.lox.generated.Stmt;

import static com.blaine.lox.Token.TokenType.*;

/**
 * Entrance of parser. Storing variables of parsing process.
 */
public class Parser {

    private List<Token> tokens;
    // stores all errors found
    private List<ParserError> parserErrors;
    // index of current token
    private int cur;

    private ExprParser exprParser;
    private StmtParser stmtParser;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.cur = 0;

        this.exprParser = new ExprParser(this);
        this.stmtParser = new StmtParser(this);

        this.parserErrors = new ArrayList<>();
    }

    // parse the whole program
    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while(!isEnd()) {
            try {
                statements.add(parseStatement());
            } catch(ParserError e) {
                parserErrors.add(e);
                errorRecovery();
            }
        }
        return statements;
    }

    public Stmt parseStatement() {
        return stmtParser.statement();
    }

    public Expr parseExpression() {
        return exprParser.expression();
    }

    public List<ParserError> getErrors() {
        return parserErrors;
    }

    private void errorRecovery() {
        while(!isEnd()) {
            // skip current statement
            if (peek(SEMICOLON)) {
                consume();
                return;
            } 
            consume();
        }
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

    // similar to peek, but instead of on cur token, looks far ahead.
    boolean peekFar(int shift, TokenType ... expectedTypes) {
        if (cur + shift >= tokens.size()) {
            return false;
        }
        List<TokenType> expectedTypeList = Arrays.asList(expectedTypes);
        if (expectedTypeList.contains(tokens.get(cur + shift).type)) {
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
            if (isEnd()) {
                throw new ParserError("Reaching end of file unexpected.");
            } else {
                // TODO: better error msg to help identify error.
                throw new ParserError(String.format("Unexpected token '%s'", cur().lexeme), cur().line, cur().column);
            }
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
