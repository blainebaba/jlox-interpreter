package com.blaine.lox;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {
    public static boolean isNumber(char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static boolean isAlpha(char ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
    }

    public static boolean isAlphaNumber(char ch) {
        return isNumber(ch) || isAlpha(ch);
    }

    public static String readFile(Class<?> clazz, String filePath) throws IOException {
        try {
            URI uri  = clazz.getResource(filePath).toURI();
            return new String(Files.readAllBytes(Paths.get(uri)));
        }
        catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

}
