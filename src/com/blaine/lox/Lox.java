package com.blaine.lox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.blaine.lox.generated.Stmt;
import com.blaine.lox.interpreter.Interpreter;
import com.blaine.lox.interpreter.ReturnThrowable;
import com.blaine.lox.interpreter.RuntimeError;
import com.blaine.lox.parser.Parser;
import com.blaine.lox.parser.ParserError;
import com.blaine.lox.parser.VarResolver;

class Lox {
    public static void main(String[] args) throws Exception {
        if (args.length > 1) {
            System.out.println("usage: jlox [script]");
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        Interpreter interpreter = new Interpreter();
        run(new String(bytes, Charset.defaultCharset()), interpreter);
    }

    private static void runPrompt() throws Exception {
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader bufferReader = new BufferedReader(reader); 

        // interpreter stores states, create first.
        Interpreter interpreter = new Interpreter();

        while (true) {
            System.out.print("> ");
            String line = bufferReader.readLine();
            if (line == null) break;
            run(line, interpreter);
        }
    }

    private static void run(String script, Interpreter interpreter) {
        Scanner scanner = new Scanner(script);
        List<Token> tokens = scanner.scan();

        // print tokens
        // printTokens(tokens);

        // parse
        Parser parser = new Parser(tokens);
        List<Stmt> stmts = parser.parse();
        if (parser.getErrors().size() > 0) {
            for (ParserError error : parser.getErrors()) {
                System.out.println(error.toString());
            }
            return;
        }

        // resolve references
        VarResolver resolver = new VarResolver(interpreter);
        resolver.resolve(stmts);

        // execute
        try {
            try {
                for (Stmt stmt : stmts) {
                    interpreter.execute(stmt);
                }
            } catch (ReturnThrowable e) {
                throw new RuntimeError("Return statement outside function.", e.token.line, e.token.column);
            }
        } catch (RuntimeError e) {
            System.out.println(e.toString());
        }
    }

     static void printTokens(List<Token> tokens) {
        String str = tokens.stream()
            .map(token -> (token.type.name()))
            .collect(Collectors.joining(" "));
        System.out.println(str);
    }
}