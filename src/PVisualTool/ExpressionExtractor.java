package PVisualTool;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionExtractor {
    private int tempCounter = 1;

    public static class TempVarsAndCodeLine {
        private String modifiedLine;
        private List<String> tempVariableDeclarations;
        private List<String> tempVariableNames;

        public TempVarsAndCodeLine(String modifiedLine, List<String> tempVariableDeclarations, List<String> tempVariableNames) {
            this.modifiedLine = modifiedLine;
            this.tempVariableDeclarations = tempVariableDeclarations;
            this.tempVariableNames = tempVariableNames;
        }

        public String getModifiedLine() { return modifiedLine; }
        public List<String> getTempVariableDeclarations() { return tempVariableDeclarations; }
        public List<String> getTempVariableNames() { return tempVariableNames; }

        public String getDebugStringExpression() {
            String escapedLine = modifiedLine.replace("\\", "\\\\").replace("\"", "\\\"");
            String debugExpr = "\"" + escapedLine + "\"";
            for (String varName : tempVariableNames) {
                debugExpr = debugExpr.replace(varName, "\" + " + varName + " + \"");
            }
            debugExpr = debugExpr.replace(" + \"\"", "");
            if (debugExpr.startsWith("\"\" + ")) debugExpr = debugExpr.substring(5);
            return debugExpr;
        }

        public String getPrintStatement() {
            return "System.out.println(" + getDebugStringExpression() + ");";
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (String decl : tempVariableDeclarations) {
                sb.append(decl).append("\n");
            }
            sb.append(modifiedLine);
            return sb.toString();
        }
    }

    private boolean isLiteral(String expr) {
        expr = expr.trim();
        if (expr.equals("true") || expr.equals("false") || expr.equals("null")) return true;
        if (expr.startsWith("\"") && expr.endsWith("\"")) return true;
        if (expr.startsWith("'") && expr.endsWith("'")) return true;
        if (expr.matches("-?\\d+(\\.\\d+)?([fFdDlL])?")) return true;
        return false;
    }

    public TempVarsAndCodeLine extractExpressions(String javaLine, int lineNumber) {
        List<String> tempDecls = new ArrayList<>();
        List<String> tempNames = new ArrayList<>();
        
        // --- NY LOGIK: Fånga indenteringen ---
        Matcher indentMatcher = Pattern.compile("^\\s*").matcher(javaLine);
        String indentation = indentMatcher.find() ? indentMatcher.group() : "";
        
        // Trimmad version för vår analys
        String workingLine = javaLine.trim();

        String[] tokens = workingLine.split("\\W+", 2);
        String firstWord = tokens.length > 0 ? tokens[0] : "";
        
        List<String> controlFlowKeywords = List.of("for", "while", "if", "else", "switch", "catch", "do");
        if (controlFlowKeywords.contains(firstWord)) {
            // Returnera originalraden omodifierad (behåller indentering)
            return new TempVarsAndCodeLine(javaLine, tempDecls, tempNames);
        }

        // 1. Hantera tilldelningar
        if (workingLine.contains("=") && !workingLine.contains("==")) {
            String[] parts = workingLine.split("=", 2);
            String leftSide = parts[0].trim();
            String rightSide = parts[1].trim();

            if (rightSide.endsWith(";")) rightSide = rightSide.substring(0, rightSide.length() - 1);

            if (isLiteral(rightSide)) {
                return new TempVarsAndCodeLine(javaLine, tempDecls, tempNames);
            }

            String tempName = getNextTemp(lineNumber);
            // Lägg till indenteringen på deklarationen
            tempDecls.add(indentation + "var " + tempName + " = " + rightSide + ";");
            tempNames.add(tempName);
            
            // Lägg till indenteringen på den modifierade raden
            return new TempVarsAndCodeLine(indentation + leftSide + " = " + tempName + ";", tempDecls, tempNames);
        }

        // 2. Hantera funktionsanrop
        Pattern funcPattern = Pattern.compile("([a-zA-Z0-9_]+)\\((.*)\\);?");
        Matcher matcher = funcPattern.matcher(workingLine);

        if (matcher.find()) {
            String methodName = matcher.group(1);
            String arguments = matcher.group(2);

            String[] args = arguments.split(",");
            List<String> newArgs = new ArrayList<>();

            for (String arg : args) {
                arg = arg.trim();
                if (!arg.isEmpty()) {
                    if (isLiteral(arg)) {
                        newArgs.add(arg); 
                    } else {
                        String tempName = getNextTemp(lineNumber);
                        // Indentera temp-variablerna
                        tempDecls.add(indentation + "var " + tempName + " = " + arg + ";");
                        tempNames.add(tempName);
                        newArgs.add(tempName);
                    }
                }
            }

            // Indentera själva anropet
            String newFuncCall = indentation + methodName + "(" + String.join(", ", newArgs) + ");";
            return new TempVarsAndCodeLine(newFuncCall, tempDecls, tempNames);
        }

        // Returnera originalraden (inklusive space) om vi inte matchar något
        return new TempVarsAndCodeLine(javaLine, tempDecls, tempNames);
    }

    private String getNextTemp(int lineNumber) {
        return "temp_" + lineNumber + "_" + (tempCounter++);
    }

    // --- Testkörning ---
    public static void main(String[] args) {
        ExpressionExtractor extractor = new ExpressionExtractor();
        
        // En indenterad kodrad (t.ex. med 8 inledande mellanslag)
        String indentedLine = "        calculate(a + b, c / 2);";
        TempVarsAndCodeLine res = extractor.extractExpressions(indentedLine, 42);
        
        System.out.println("--- Rad 42 med indentering ---");
        System.out.println("Körbar kod:\n" + res.toString());
        System.out.println("\nEnbart modiferad rad:\n" + res.getModifiedLine());
    }
}