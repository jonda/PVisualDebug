package PVisualTool;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionExtractor {
    private int tempCounter = 1;

    // ... (TempVarsAndCodeLine är oförändrad från tidigare) ...
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
        String modifiedLine = javaLine.trim();

        // --- NY FIX: Kolla det första ordet på raden ---
        // Delar strängen vid första icke-ord-tecknet (t.ex. mellanslag eller parentes)
        String[] tokens = modifiedLine.split("\\W+", 2); 
        String firstWord = tokens.length > 0 ? tokens[0] : "";
        
        // Lista på ord vi inte ska röra eftersom de styr kodens flöde
        List<String> controlFlowKeywords = List.of("for", "while", "if", "else", "switch", "catch", "do");
        if (controlFlowKeywords.contains(firstWord)) {
            // Returnera raden exakt som den är, utan att skapa temporära variabler
            return new TempVarsAndCodeLine(modifiedLine, tempDecls, tempNames);
        }

        // 1. Hantera tilldelningar
        if (modifiedLine.contains("=") && !modifiedLine.contains("==")) {
            String[] parts = modifiedLine.split("=", 2);
            String leftSide = parts[0].trim();
            String rightSide = parts[1].trim();

            if (rightSide.endsWith(";")) rightSide = rightSide.substring(0, rightSide.length() - 1);

            if (isLiteral(rightSide)) {
                return new TempVarsAndCodeLine(modifiedLine, tempDecls, tempNames);
            }

            String tempName = getNextTemp(lineNumber);
            tempDecls.add("var " + tempName + " = " + rightSide + ";");
            tempNames.add(tempName);
            
            return new TempVarsAndCodeLine(leftSide + " = " + tempName + ";", tempDecls, tempNames);
        }

        // 2. Hantera funktionsanrop
        Pattern funcPattern = Pattern.compile("([a-zA-Z0-9_]+)\\((.*)\\);?");
        Matcher matcher = funcPattern.matcher(modifiedLine);

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
                        tempDecls.add("var " + tempName + " = " + arg + ";");
                        tempNames.add(tempName);
                        newArgs.add(tempName);
                    }
                }
            }

            String newFuncCall = methodName + "(" + String.join(", ", newArgs) + ");";
            return new TempVarsAndCodeLine(newFuncCall, tempDecls, tempNames);
        }

        return new TempVarsAndCodeLine(modifiedLine, tempDecls, tempNames);
    }

    private String getNextTemp(int lineNumber) {
        return "temp_" + lineNumber + "_" + (tempCounter++);
    }

    // --- Testkörning ---
    public static void main(String[] args) {
        ExpressionExtractor extractor = new ExpressionExtractor();
        
        // Testar din bugg-rapport!
        String forLoopLine = "for (int i=0; i < 10; i++) {";
        TempVarsAndCodeLine res = extractor.extractExpressions(forLoopLine, 11);
        
        System.out.println("--- Rad 11 (For-loop) ---");
        System.out.println("Körbar kod:\n" + res.toString());
        System.out.println("Antal temp-variabler skapade: " + res.getTempVariableNames().size());
    }
}