package PVisualTool;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionExtractor {

    private int tempCounter = 1;

    public static class TempVarsAndCodeLine {

        // ... (Samma innehåll som i föregående version) ...
        private String modifiedLine;
        private List<String> tempVariableDeclarations;
        private List<String> tempVariableNames;
        private List<String> stringTempNames;
        private boolean isControlFlow;

        public TempVarsAndCodeLine(String modifiedLine, List<String> tempVariableDeclarations,
                List<String> tempVariableNames, List<String> stringTempNames, boolean isControlFlow) {
            this.modifiedLine = modifiedLine;
            this.tempVariableDeclarations = tempVariableDeclarations;
            this.tempVariableNames = tempVariableNames;
            this.stringTempNames = stringTempNames;
            this.isControlFlow = isControlFlow;
        }

        public String getModifiedLine() {
            return modifiedLine;
        }

        public List<String> getTempVariableDeclarations() {
            return tempVariableDeclarations;
        }

        public List<String> getTempVariableNames() {
            return tempVariableNames;
        }

        public String getDebugStringExpression() {
            String escapedLine = modifiedLine.replace("\\", "\\\\").replace("\"", "\\\"");
            String debugExpr = "\"" + escapedLine + "\"";

            for (String varName : tempVariableNames) {
                if (stringTempNames != null && stringTempNames.contains(varName)) {
                    debugExpr = debugExpr.replaceAll("\\b" + varName + "\\b",
                            Matcher.quoteReplacement("\\\"\" + " + varName + " + \"\\\""));
                } else {
                    debugExpr = debugExpr.replaceAll("\\b" + varName + "\\b",
                            Matcher.quoteReplacement("\" + " + varName + " + \""));
                }
            }

            if (isControlFlow) {
                String firstWord = modifiedLine.trim().split("\\W+", 2)[0];

                if (firstWord.equals("for")) {
                    int firstSemi = debugExpr.indexOf(';');
                    int secondSemi = debugExpr.indexOf(';', firstSemi + 1);
                    if (firstSemi != -1 && secondSemi != -1) {
                        String part1 = debugExpr.substring(0, firstSemi + 1);
                        String part2 = debugExpr.substring(firstSemi + 1, secondSemi);
                        String part3 = debugExpr.substring(secondSemi);
                        debugExpr = part1 + injectVariablesIntoString(part2) + part3;
                    }
                } else if (firstWord.equals("if") || firstWord.equals("while")) {
                    int firstParen = debugExpr.indexOf('(');
                    int lastParen = debugExpr.lastIndexOf(')');
                    if (firstParen != -1 && lastParen != -1 && firstParen < lastParen) {
                        String part1 = debugExpr.substring(0, firstParen + 1);
                        String part2 = debugExpr.substring(firstParen + 1, lastParen);
                        String part3 = debugExpr.substring(lastParen);
                        debugExpr = part1 + injectVariablesIntoString(part2) + part3;
                    }
                }
            }

            debugExpr = debugExpr.replace(" + \"\"", "");
            if (debugExpr.startsWith("\"\" + ")) {
                debugExpr = debugExpr.substring(5);
            }
            return debugExpr;
        }

        private String injectVariablesIntoString(String codePart) {
            // NYTT REGEX: Fångar ord, men ÄVEN punkt-notation (.length) och array-indexering ([i])
            // Den negativa lookaheaden i slutet (?!\s*[\(\.\[]) hindrar den från att råka stanna
            // för tidigt om det egentligen är ett metodanrop (t.ex. obj.method()).
            String regex = "\\b[a-zA-Z_]\\w*(?:\\.\\w+|\\[[^\\]]+\\])*\\b(?!\\s*[\\(\\.\\[])";
            Matcher m = Pattern.compile(regex).matcher(codePart);

            List<String> ignoreList = List.of("true", "false", "null", "int", "boolean", "double", "float", "long", "String", "new", "equals", "length", "size");

            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String word = m.group();
                if (!ignoreList.contains(word)) {
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
        if (expr.equals("true") || expr.equals("false") || expr.equals("null")) {
            return true;
        }
        if (expr.startsWith("\"") && expr.endsWith("\"")) {
            return true;
        }
        if (expr.startsWith("'") && expr.endsWith("'")) {
            return true;
        }
        if (expr.matches("-?\\d+(\\.\\d+)?([fFdDlL])?")) {
            return true;
        }

        // --- NY KOLL: Ignorera nakna array-initieringar ---
        if (expr.startsWith("{") && expr.endsWith("}")) {
            return true;
        }

        return false;
    }

    private boolean containsIgnoredTerm(String expr) {
        List<String> blackList = List.of("JOptionPane", "Scanner", "System.in", "System.out");
        for (String ignored : blackList) {
            if (expr.contains(ignored)) {
                return true;
            }
        }
        return false;
    }

    private String[] splitTopLevelAssignment(String line) {
        int depth = 0;
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"' && (i == 0 || line.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            }
            if (inQuotes) {
                continue;
            }

            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if (c == '=' && depth == 0) {
                if (i > 0) {
                    char prev = line.charAt(i - 1);
                    if (prev == '=' || prev == '!' || prev == '<' || prev == '>') {
                        continue;
                    }
                }
                if (i < line.length() - 1 && line.charAt(i + 1) == '=') {
                    continue;
                }

                String leftSide = line.substring(0, i + 1);
                String rightSide = line.substring(i + 1);
                return new String[]{leftSide, rightSide};
            }
        }
        return null;
    }

    private List<String> splitArguments(String argsString) {
        List<String> args = new ArrayList<>();
        int parenDepth = 0;
        boolean inQuotes = false;
        StringBuilder currentArg = new StringBuilder();

        for (int i = 0; i < argsString.length(); i++) {
            char c = argsString.charAt(i);

            if (c == '"' && (i == 0 || argsString.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            }

            if (!inQuotes) {
                if (c == '(') {
                    parenDepth++;
                } else if (c == ')') {
                    parenDepth--;
                } else if (c == ',' && parenDepth == 0) {
                    args.add(currentArg.toString().trim());
                    currentArg.setLength(0);
                    continue;
                }
            }
            currentArg.append(c);
        }
        if (currentArg.length() > 0) {
            args.add(currentArg.toString().trim());
        }
        return args;
    }

    // --- NY METOD: Extraherar typen från en deklaration! ---
    private String extractTypeFromDeclaration(String leftSide) {
        // Rensa bort operatörer för att bara få kvar deklarationen ("int a =" -> "int a")
        String cleanLeft = leftSide.replace("=", "").replace("+", "").replace("-", "").replace("*", "").replace("/", "").trim();
        String[] parts = cleanLeft.split("\\s+");
        if (parts.length >= 2) {
            // Det näst sista ordet är nästan alltid typen (t.ex. "public static final float hastighet" -> "float")
            return parts[parts.length - 2];
        }
        return null; // Om det bara stod t.ex. "hastighet =", dvs ingen typ deklarerad
    }

    public TempVarsAndCodeLine extractExpressions(String javaLine, int lineNumber) {
        List<String> tempDecls = new ArrayList<>();
        List<String> tempNames = new ArrayList<>();
        List<String> stringTempNames = new ArrayList<>();

        Matcher indentMatcher = Pattern.compile("^\\s*").matcher(javaLine);
        String indentation = indentMatcher.find() ? indentMatcher.group() : "";
        String workingLine = javaLine.trim();

        if (workingLine.startsWith("//") || workingLine.startsWith("/*") || workingLine.startsWith("*")) {
            return new TempVarsAndCodeLine(javaLine, tempDecls, tempNames, stringTempNames, false);
        }

        String[] tokens = workingLine.split("\\W+", 2);
        String firstWord = tokens.length > 0 ? tokens[0] : "";

        List<String> controlFlowKeywords = List.of("for", "while", "if", "else", "switch", "catch", "do");
        if (controlFlowKeywords.contains(firstWord)) {
            return new TempVarsAndCodeLine(javaLine, tempDecls, tempNames, stringTempNames, true);
        }

        List<String> declarationKeywords = List.of("public", "private", "protected", "void", "class", "interface", "abstract");
        if (declarationKeywords.contains(firstWord)) {
            return new TempVarsAndCodeLine(javaLine, tempDecls, tempNames, stringTempNames, false);
        }

        String[] assignParts = splitTopLevelAssignment(workingLine);
        if (assignParts != null) {
            String leftSide = assignParts[0];
            String rightSide = assignParts[1].trim();

            if (rightSide.endsWith(";")) {
                rightSide = rightSide.substring(0, rightSide.length() - 1);
            }

            if (isLiteral(rightSide) || containsIgnoredTerm(rightSide)) {
                return new TempVarsAndCodeLine(javaLine, tempDecls, tempNames, stringTempNames, false);
            }

            // --- HÄR ANVÄNDER VI VÅRT NYA TYPE-HACK ---
            String declaredType = extractTypeFromDeclaration(leftSide);
            String typeToUse = declaredType != null ? declaredType : "var";

            String tempName = getNextTemp(lineNumber);
            // Skriver nu t.ex. "float temp_X = ..." istället för "var temp_X = ..."
            tempDecls.add(indentation + typeToUse + " " + tempName + " = " + rightSide + ";");
            tempNames.add(tempName);

            if (rightSide.contains("\"")) {
                stringTempNames.add(tempName);
            }

            return new TempVarsAndCodeLine(indentation + leftSide.trim() + " " + tempName + ";", tempDecls, tempNames, stringTempNames, false);
        }

        Pattern funcPattern = Pattern.compile("([a-zA-Z0-9_]+)\\((.*)\\);?");
        Matcher matcher = funcPattern.matcher(workingLine);

        if (matcher.find()) {
            String methodName = matcher.group(1);
            String arguments = matcher.group(2);

            List<String> args = splitArguments(arguments);
            List<String> newArgs = new ArrayList<>();

            boolean isMethodDeclaration = false;
            for (String arg : args) {
                if (arg.matches("^[a-zA-Z_][a-zA-Z0-9_\\[\\]<>,]*\\s+[a-zA-Z_][a-zA-Z0-9_]*$")) {
                    isMethodDeclaration = true;
                    break;
                }
            }

            if (isMethodDeclaration || workingLine.endsWith("{")) {
                return new TempVarsAndCodeLine(javaLine, tempDecls, tempNames, stringTempNames, false);
            }

            for (String arg : args) {
                if (!arg.isEmpty()) {
                    if (isLiteral(arg) || containsIgnoredTerm(arg)) {
                        newArgs.add(arg);
                    } else {
                        String tempName = getNextTemp(lineNumber);
                        tempDecls.add(indentation + "var " + tempName + " = " + arg + ";");
                        tempNames.add(tempName);

                        if (arg.contains("\"")) {
                            stringTempNames.add(tempName);
                        }

                        newArgs.add(tempName);
                    }
                }
            }

            String newFuncCall = indentation + methodName + "(" + String.join(", ", newArgs) + ");";
            return new TempVarsAndCodeLine(newFuncCall, tempDecls, tempNames, stringTempNames, false);
        }

        return new TempVarsAndCodeLine(javaLine, tempDecls, tempNames, stringTempNames, false);
    }

    private String getNextTemp(int lineNumber) {
        return "temp_" + lineNumber + "_" + (tempCounter++);
    }

    // --- Testkörning ---
    public static void main(String[] args) {
        ExpressionExtractor extractor = new ExpressionExtractor();

        System.out.println("--- Test 1: Array-initiering ---");
        TempVarsAndCodeLine res1 = extractor.extractExpressions("int[] position = {10,12,15,20,25,30,32,30,25,20,15,12,10,7,5,4,3,4,5,7,9,10,10};", 30);
        System.out.println("Körbar kod:\n" + res1.toString());

        System.out.println("\n--- Test 2: Extraction av typ ---");
        TempVarsAndCodeLine res2 = extractor.extractExpressions("float hastighet = speedx * 2;", 31);
        System.out.println("Körbar kod:\n" + res2.toString());
    }
}
