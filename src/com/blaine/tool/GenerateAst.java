package com.blaine.tool;

import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Generate Abstract Syntax Tree classes. 
 */
public class GenerateAst {
    public static void main(String[] args) throws Exception {

        // defines expression
        defineAst("com/blaine/lox/generated", "Expr", Arrays.asList(
            "Binary:Expr left,Token operator,Expr right",
            "Unary:Token operator,Expr expr",
            "Grouping:Expr expr",
            "Literal:Object value"
        ));

        defineVisitors("com/blaine/lox/generated", "Expr", Arrays.asList(
            "Binary", "Unary", "Grouping", "Literal"
        ));
    }

    /**
     * create expression interface, along with one class for each expression type.
     */
    private static void defineAst(String packagePath, String interfaceName, List<String> classDefinitions) throws Exception {
        String folderPath = "src/" + packagePath;
        String outputFileName = Paths.get(folderPath).toAbsolutePath().toString() + "/" + interfaceName + ".java";
        PrintWriter writer = new PrintWriter(outputFileName, "utf-8");

        writer.println("// Generated File");
        writer.println("package " + packagePath.replace("/", ".") + ";");
        writer.println("");
        writer.println("import com.blaine.lox.Token;");
        writer.println("");
        writer.println("public interface " + interfaceName + " {");
        writer.println("");

        // visitor accept method
        writer.println(space(4) + "<R> R accept(ExprVisitor<R> visitor);");
        writer.println("");

        // sub classes
        for (String classDefString : classDefinitions) {
            String className = classDefString.split(":")[0];
            String paramList = classDefString.split(":")[1];
            String[] fields = paramList.split(",");

            writer.println(space(4) + "public static class " + className + " implements " + interfaceName + "{");
            writer.println("");
            // fields
            for (String field : fields) {
                writer.println(space(8) + "public final " + field + ";");
            }
            writer.println("");

            // constructor
            writer.println(space(8) + "public " + className + "(" + paramList + ") {");
            for (String field : fields) {
                String fieldClass = field.split(" ")[0];
                String fieldName = field.split(" ")[1];
                writer.println(space(12) + "this." + fieldName + " = " + fieldName + ";");
            }
            writer.println(space(8) + "}");
            writer.println("");

            // visitor accept method
            writer.println(space(8) + "@Override");
            writer.println(space(8) + "public <R> R accept(ExprVisitor<R> visitor){");
            writer.println(space(12) + "return visitor.visit" + className + "(this);");
            writer.println(space(8) + "}");

            writer.println(space(4) + "}");
            writer.println("");
        }
        writer.println("}");
        writer.close();
    }

    /**
     * Defines visitor (interface) on a target interface.
     */
    private static void defineVisitors(String packagePath, String interfaceName, List<String> classNames) throws Exception {
        String folderPath = "src/" + packagePath;
         String outputFileName = Paths.get(folderPath).toAbsolutePath().toString() + "/" + interfaceName + "Visitor.java";
        PrintWriter w = new PrintWriter(outputFileName, "utf-8");

        w.println("// Generated File");
        w.println("package " + packagePath.replace("/", ".") + ";");
        w.println("");
        for (String className : classNames) {
            w.println("import com.blaine.lox.generated.Expr." + className + ";");
        }
        w.println("");
        w.println("public interface " + interfaceName + "Visitor<R> {");
        w.println("");
        for (String className : classNames) {
            w.println(space(4) + "R visit" + className + "(" + className + " " + className.toLowerCase() + ");");
        }
        w.println("}");

        w.close();
    }

    private static String space(int num) {
        String s = "";
        for (int i = 0; i < num; i++) {
            s += " ";
        }
        return s;
    }
}
