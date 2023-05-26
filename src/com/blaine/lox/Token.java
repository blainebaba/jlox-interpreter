package com.blaine.lox;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Token {

    public static enum TokenType {
        // symbols 
        LEFT_BRACE("{"), RIGHT_BRACE("}"), LEFT_PAREN("("), RIGHT_PAREN(")"), 
        COMMA(","), SEMICOLON(";"), EQUAL("="), EXCLAM("!"), 
        PLUS("+"), MINUS("-"), STAR("*"), SLASH("/"), 
        LESS_EQUAL("<="), GREATER_EQUAL(">="), NOT_EQUAL("!="), EQUAL_EQAUL("=="), GREATER(">"), LESSER("<"),

        // keywords
        FOR("for"), WHILE("while"), IF("if"), ELSE("else"), 
        TRUE("true"), FALSE("false"), AND("and"), OR("or"), 
        VAR("var"), NIL("nil"), PRINT("print"), 
        FUN("fun"), RETURN("return"), 
        CLASS("class"), SUPER("super"), THIS("this"), 

        // literals
        IDENTIFIER, STRING, NUMBER;

        public static final List<TokenType> SYMBOLS;
        public static final Map<String, TokenType> KEYWORDS;

        static {
            Comparator<TokenType> lexemeLengthComparator = new Comparator<Token.TokenType>() {
                @Override
                public int compare(TokenType o1, TokenType o2) {
                    return o2.lexeme.length() - o1.lexeme.length();
                }
            };
            SYMBOLS = Arrays.stream(TokenType.values())
                .filter(TokenType::isSymbol)
                .sorted(lexemeLengthComparator)
                .collect(Collectors.toList());
            KEYWORDS = Arrays.stream(TokenType.values())
                .filter(TokenType::isKeyword)
                .collect(Collectors.toMap(type -> type.lexeme, type -> type));
        }


        public final String lexeme;

        TokenType(String lexeme) {
            this.lexeme = lexeme;
        }

        TokenType() {
            this.lexeme = null;
        }

        boolean match(char ch) {
            return lexeme != null && lexeme.length() == 1 && lexeme.charAt(0) == ch;
        }

        boolean match(String str) {
            return lexeme != null && lexeme.equals(str);
        }

        boolean isKeyword() {
            return lexeme != null && Utils.isAlpha(lexeme.charAt(0));
        }

        boolean isSymbol() {
            return lexeme != null && !Utils.isAlpha(lexeme.charAt(0));
        }
    }

    public TokenType type;
    public Object literalValue;
    public String lexeme;
    // position info, for error message
    public int line;
    public int column;

    public Token(TokenType type, int line, int column, String lexeme, Object literalValue) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.lexeme = lexeme;
        this.literalValue = literalValue;
    }

    public Token(TokenType type, int line, int column, String lexeme) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.lexeme = lexeme;
    }
}
