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
        private boolean isControlFlow; // NYTT: Håller koll på om det är for/if/while

        public TempVarsAndCodeLine(String modifiedLine, List<String> tempVariableDeclarations, List<String> tempVariableNames, boolean isControlFlow) {
            this.modifiedLine = modifiedLine;
            this.tempVariableDeclarations = tempVariableDeclarations;
            this.tempVariableNames = tempVariableNames;
            this.isControlFlow = isControlFlow;
        }

        public String getModifiedLine() { return modifiedLine; }
        public List<String> getTempVariableDeclarations() { return tempVariableDeclarations; }
        public List<String> getTempVariableNames() { return tempVariableNames; }

        public String getDebugStringExpression() {
            String escapedLine = modifiedLine.replace("\\", "\\\\").replace("\"", "\\\"");
            String debugExpr = "\"" + escapedLine + "\"";
            
            // 1. Ersätt de skapade temp-variablerna (om det finns några)
            for (String varName : tempVariableNames) {
                debugExpr = debugExpr.replace(varName, "\" + " + varName + " + \"");
            }

            // 2. NYTT: Om det är if/while/for, extrahera variabler från jämförelsen och injicera
            if (isControlFlow) {
                String firstWord = modifiedLine.trim().split("\\W+", 2)[0];
                
                if (firstWord.equals("for")) {
                    int firstSemi = debugExpr.indexOf(';');
                    int secondSemi = debugExpr.indexOf(';', firstSemi + 1);
                    if (firstSemi != -1 && secondSemi != -1) {
                        String part1 = debugExpr.substring(0, firstSemi + 1);
                        String part2 = debugExpr.substring(firstSemi + 1, secondSemi); // Jämförelsen
                        String part3 = debugExpr.substring(secondSemi);
                        
                        debugExpr = part1 + injectVariablesIntoString(part2) + part3;
                    }
                } else if (firstWord.equals("if") || firstWord.equals("while")) {
                    int firstParen = debugExpr.indexOf('(');
                    int lastParen = debugExpr.lastIndexOf(')');
                    if (firstParen != -1 && lastParen != -1 && firstParen < lastParen) {
                        String part1 = debugExpr.substring(0, firstParen + 1);
                        String part2 = debugExpr.substring(firstParen + 1, lastParen); // Jämförelsen
                        String part3 = debugExpr.substring(lastParen);
                        
                        debugExpr = part1 + injectVariablesIntoString(part2) + part3;
                    }
                }
            }
            
            // Städa upp tomma strängkonkateneringar
            debugExpr = debugExpr.replace(" + \"\"", "");
            if (debugExpr.startsWith("\"\" + ")) debugExpr = debugExpr.substring(5);
            return debugExpr;
        }

        // Hjälpmetod för att injicera värden istället för variabelnamn i en specifik delsträng
        private String injectVariablesIntoString(String codePart) {
            Matcher m = Pattern.compile("\\b[a-zA-Z_]\\w*\\b").matcher(codePart);
            // Ignorera nyckelord och vanliga metodnamn som vi inte vill skriva ut värdet av
            List<String> ignoreList = List.of("true", "false", "null", "int", "boolean", "double", "float", "long", "String", "new", "equals", "length", "size");
            
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String word = m.group();
                if (!ignoreList.contains(word)) {
                    // Citat-magi: "bryter" strängen, lägger in variabeln, och öppnar strängen igen
                    m.appendReplacement(sb, Matcher.quoteReplacement("\" + " + word + " + \""));
                } else {
                    m.appendReplacement(sb, word);
                }
            }
            m.appendTail(sb);
            return sb.toString();
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
        
        Matcher indentMatcher = Pattern.compile("^\\s*").matcher(javaLine);
        String indentation = indentMatcher.find() ? indentMatcher.group() : "";
        String workingLine = javaLine.trim();

        String[] tokens = workingLine.split("\\W+", 2);
        String firstWord = tokens.length > 0 ? tokens[0] : "";
        
        List<String> controlFlowKeywords = List.of("for", "while", "if", "else", "switch", "catch", "do");
        if (controlFlowKeywords.contains(firstWord)) {
            // NYTT: Skicka med "true" som sista parameter för att markera att det är ett kontrollflöde
            return new TempVarsAndCodeLine(javaLine, tempDecls, tempNames, true);
        }

        // 1. Hantera tilldelningar
        if (workingLine.contains("=") && !workingLine.contains("==")) {
            String[] parts = workingLine.split("=", 2);
            String leftSide = parts[0].trim();
            String rightSide = parts[1].trim();

            if (rightSide.endsWith(";")) rightSide = rightSide.substring(0, rightSide.length() - 1);

            if (isLiteral(rightSide)) {
                return new TempVarsAndCodeLine(javaLine, tempDecls, tempNames, false);
            }

            String tempName = getNextTemp(lineNumber);
            tempDecls.add(indentation + "var " + tempName + " = " + rightSide + ";");
            tempNames.add(tempName);
            
            return new TempVarsAndCodeLine(indentation + leftSide + " = " + tempName + ";", tempDecls, tempNames, false);
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
                        tempDecls.add(indentation + "var " + tempName + " = " + arg + ";");
                        tempNames.add(tempName);
                        newArgs.add(tempName);
                    }
                }
            }

            String newFuncCall = indentation + methodName + "(" + String.join(", ", newArgs) + ");";
            return new TempVarsAndCodeLine(newFuncCall, tempDecls, tempNames, false);
        }

        return new TempVarsAndCodeLine(javaLine, tempDecls, tempNames, false);
    }

    private String getNextTemp(int lineNumber) {
        return "temp_" + lineNumber + "_" + (tempCounter++);
    }

    // --- Testkörning ---
    public static void main(String[] args) {
        ExpressionExtractor extractor = new ExpressionExtractor();
        
        System.out.println("--- Test 1: For-loop ---");
        TempVarsAndCodeLine res1 = extractor.extractExpressions("for(int i = 0; i < limit; i++) {", 10);
        System.out.println("Original: " + res1.getModifiedLine());
        System.out.println("Debug:    " + res1.getDebugStringExpression());

        System.out.println("\n--- Test 2: If-sats ---");
        TempVarsAndCodeLine res2 = extractor.extractExpressions("    if (age >= minAge && isValid) {", 11);
        System.out.println("Original: " + res2.getModifiedLine());
        System.out.println("Debug:    " + res2.getDebugStringExpression());
        
        System.out.println("\n--- Test 3: While-loop ---");
        TempVarsAndCodeLine res3 = extractor.extractExpressions("while(count < 10) {", 12);
        System.out.println("Original: " + res3.getModifiedLine());
        System.out.println("Debug:    " + res3.getDebugStringExpression());
    }
}