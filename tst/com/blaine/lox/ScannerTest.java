package com.blaine.lox;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;

import com.blaine.lox.Token.TokenType;

public class ScannerTest {

    @Test
    public void testScanSymbols() {
        for (TokenType type : TokenType.SYMBOLS) {
            Scanner scanner = new Scanner(type.lexeme);
            List<Token> tokens = scanner.scan();
            assertEquals(1, tokens.size());
            assertEquals(type.lexeme, tokens.get(0).lexeme);
            assertEquals(null, tokens.get(0).literalValue);
            assertEquals(type, tokens.get(0).type);
        }
    }

    @Test
    public void testScanKeywords() {
        for (TokenType type : TokenType.KEYWORDS.values()) {
            Scanner scanner = new Scanner(type.lexeme);
            List<Token> tokens = scanner.scan();
            assertEquals(1, tokens.size());
            assertEquals(type.lexeme, tokens.get(0).lexeme);
            assertEquals(null, tokens.get(0).literalValue);
            assertEquals(type, tokens.get(0).type);
        }
    }

    @Test
    public void testScanIdentifier() {
        String input = "abc";
        Scanner scanner = new Scanner(input);
        List<Token> tokens = scanner.scan();
        assertEquals(1, tokens.size());
        assertEquals(input, tokens.get(0).lexeme);
        assertEquals(input, tokens.get(0).literalValue);
        assertEquals(TokenType.IDENTIFIER, tokens.get(0).type);
    }

    @Test
    public void testScanNumber() {
        String input = "123 123.456";
        Scanner scanner = new Scanner(input);
        List<Token> tokens = scanner.scan();
        assertEquals(2, tokens.size());
        assertEquals("123", tokens.get(0).lexeme);
        assertEquals(123.0, tokens.get(0).literalValue);
        assertEquals(TokenType.NUMBER, tokens.get(0).type);
        assertEquals("123.456", tokens.get(1).lexeme);
        assertEquals(123.456, tokens.get(1).literalValue);
    }

    @Test
    public void testScanString() {
        String input = "\"abc\"";
        Scanner scanner = new Scanner(input);
        List<Token> tokens = scanner.scan();
        assertEquals(1, tokens.size());
        assertEquals(input, tokens.get(0).lexeme);
        assertEquals("abc", tokens.get(0).literalValue);
        assertEquals(TokenType.STRING, tokens.get(0).type);
    }

    @Test
    public void testScanSingleLineComment() {
        String input = "// comment";
        Scanner scanner = new Scanner(input);
        List<Token> tokens = scanner.scan();
        assertEquals(0, tokens.size());
    }

    @Test
    public void testScanFile1() throws Exception {
        String input = Utils.readFile(getClass(), "ScannerTestInput1.txt");
        Scanner scanner = new Scanner(input);
        List<Token> tokens = scanner.scan();
        assertEquals(5, tokens.size());
        assertEquals(TokenType.VAR, tokens.get(0).type);
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type);
        assertEquals(TokenType.EQUAL, tokens.get(2).type);
        assertEquals(TokenType.NUMBER, tokens.get(3).type);
        assertEquals(TokenType.SEMICOLON, tokens.get(4).type);
        assertEquals(2, tokens.get(4).line);
        assertEquals(10, tokens.get(4).column);
    }
}
