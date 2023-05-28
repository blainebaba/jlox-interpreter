package com.blaine.lox;

import static com.blaine.lox.Token.TokenType.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.blaine.lox.Token.TokenType;

public class Scanner {
    private String script;
    private List<Token> tokens;

    // start of current token
    private int start;
    // end of current token, it is also points to token that is currently expecting.
    private int end;

    // line number of current token
    private int lineNumber;
    // column of current token
    private int columnNumber;

    public Scanner(String script) {
        this.script = script;
        this.tokens = new ArrayList<>();
        lineNumber = 1;
        columnNumber = 1;
        start = 0;
        end = 0;
    }
    
    public List<Token> scan() {

        while (end < script.length()) {
            columnNumber += end - start; 
            start = end;
            scanOneToken();
        }
        return tokens;
    }

    private void scanOneToken() {
        char ch = script.charAt(end);
        // identifier
        if (Utils.isAlpha(ch)) {
            advance(1);
            while (!reachEnd() && Utils.isAlphaNumber(script.charAt(end))) {
                advance(1);
            }
            String identifier = script.substring(start, end);
            if (!matchKeywords(identifier)) {
                addToken(IDENTIFIER, identifier);
            }
            return;
        } 

        // string literal
        if (match('"')) {
            while (!reachEnd() && !peek('"') && !peek('\n')) {
                advance(1);
            }
            if (match('"')) {
                addToken(STRING, script.substring(start+1, end-1));
            } else {
                throw new ScannerError("Expects double quote(\").", lineNumber, columnNumber);
            }
            return;
        }

        // number literal
        if (Utils.isNumber(ch)) {
            advance(1);
            while (!reachEnd() && Utils.isNumber(script.charAt(end))) {
                advance(1);
            }
            if (match('.')) {
                while (!reachEnd() && Utils.isNumber(script.charAt(end))) {
                    advance(1);
                }
            }
            addToken(NUMBER, Double.parseDouble(script.substring(start, end)));
            return;
        }

        // single line comment
        if (match("//")) {
            // consume everything untill newline
            while (!reachEnd() && !peek('\n')) {
                advance(1);
            }
            return;
        }

        // symbols
        for (TokenType type : TokenType.SYMBOLS) {
            if (match(type.lexeme)) {
                addToken(type);
                return;
            }
        }

        // empty tokens
        if (Arrays.asList('\n', '\r', '\t', ' ').contains(ch)) {
            if (ch == '\n') {
                lineNumber ++;
                columnNumber = 0;
            } else if (ch == '\t') {
                // assume tab consume 4 columns
                columnNumber += 3;
            }
            advance(1);
            return;
        }

        // matched nothing, error 
        throw new ScannerError(String.format("Unknown token '%s'.", Character.toString(ch)), lineNumber, columnNumber);
    }

    // match string with keyword, add token and return true if keyword matched 
    private boolean matchKeywords(String str) {
        if (TokenType.KEYWORDS.containsKey(str)) {
            addToken(TokenType.KEYWORDS.get(str));
            return true;
        }
        return false;
    }

    private Token addToken(TokenType type) {
        return addToken(type, null);
    }

    private Token addToken(TokenType type, Object literal) {
        String lexeme = script.substring(start, end);
        Token token = new Token(type, lineNumber, columnNumber, lexeme, literal);
        tokens.add(token);
        return token;
    }

    // move end pointer x, means include next x chars into current token.
    private void advance(int x)  {
        end += x;
    }

    // match lexeme, advance end pointer if matched.
    private boolean match(String lexeme) {
        int n = lexeme.length();
        if (end + n > script.length()) {
            return false;
        }
        if (script.substring(end, end + n).equals(lexeme)) {
            end += n;
            return true;
        }
        return false;
    }

    private boolean match(char ch) {
        return match(new String(new char[]{ch}));
    }

    // similar to match, but not advance end pointer
    private boolean peek(String lexeme) {
        int n = lexeme.length();
        if (end + n > script.length()) {
            return false;
        }
        if (script.substring(end, end + n).equals(lexeme)) {
            return true;
        }
        return false;
    }

    private boolean peek(char ch) {
        return peek(new String(new char[]{ch}));
    }

    private boolean reachEnd() {
        return end >= script.length();
    }
}
