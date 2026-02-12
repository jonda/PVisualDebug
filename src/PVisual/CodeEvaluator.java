/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisual;

/**
 *
 * @author dahjon
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEvaluator {

    public static void main(String[] args) {
        String rawCode = "fill(255, 0, 0);\n" +
                         "  for (int i=0; i < 20; i++) {\n" +
                         "    delay(500);\n" +
                         "    circle(20*i, 20*i, 20+10*i);\n" +
                         "    if (i == 5) { text(\"Fem!\", 0, 0); }\n" +
                         "  }";

        int repValue = 5; // Vi testar med 5. (5 < 20 ska bli "true")

        String result = processCode(rawCode, repValue);
        System.out.println("--- RESULTAT ---");
        System.out.println(result);
    }

    public static String processCode(String code, int val) {
        // 1. Hitta variabelnamnet
        Pattern pattern = Pattern.compile("for\\s*\\(\\s*int\\s+(\\w+)");
        Matcher matcher = pattern.matcher(code);
        
        String varName = "";
        if (matcher.find()) {
            varName = matcher.group(1);
        } else {
            return code; 
        }

        // 2. Identifiera for-loopen
        int forIndex = code.indexOf("for");
        int openParen = code.indexOf("(", forIndex);
        int closeParen = code.indexOf(")", openParen);
        
        if (openParen == -1 || closeParen == -1) return code;

        String forContent = code.substring(openParen + 1, closeParen);
        String[] forParts = forContent.split(";");
        
        StringBuilder sb = new StringBuilder();

        // --- BYGG LOOP-HUVUDET ---
        sb.append(code.substring(0, openParen + 1));
        if (forParts.length == 3) {
            sb.append(forParts[0]).append(";"); // int i=0
            
            // --- HÄR ÄR NYHETEN: Evaluera villkoret ---
            String conditionPart = forParts[1];
            // Byt först ut i mot 5
            String condWithVal = conditionPart.replaceAll("\\b" + varName + "\\b", String.valueOf(val));
            // Försök räkna ut om det är true/false
            String evaluatedCond = evalBoolean(condWithVal);
            
            sb.append(" " + evaluatedCond).append(";"); // Lägg till "true" eller "false"
            
            sb.append(forParts[2]); // i++
        } else {
            sb.append(forContent);
        }
        sb.append(")");

        // --- BEARBETNING AV LOOP-KROPPEN ---
        String body = code.substring(closeParen + 1);
        String processedBody = evaluateAndReplace(body, varName, val);
        sb.append(processedBody);

        return sb.toString();
    }

    // Ny metod som hanterar jämförelser (<, >, <=, >=, ==, !=)
    private static String evalBoolean(String expression) {
        expression = expression.trim();
        
        // Hitta vilken operator som används
        String operator = "";
        if (expression.contains("<=")) operator = "<=";
        else if (expression.contains(">=")) operator = ">=";
        else if (expression.contains("==")) operator = "==";
        else if (expression.contains("!=")) operator = "!=";
        else if (expression.contains("<")) operator = "<";
        else if (expression.contains(">")) operator = ">";
        
        // Om ingen jämförelse finns, returnera bara strängen (kanske var det bara en boolesk variabel)
        if (operator.isEmpty()) return expression;

        try {
            // Dela upp strängen: "5 < 20" blir "5" och "20"
            String[] operands = expression.split(Pattern.quote(operator));
            if (operands.length != 2) return expression;

            // Räkna ut vänster och höger sida med vår matte-parser
            double left = evalMath(operands[0]);
            double right = evalMath(operands[1]);
            
            boolean result = false;
            switch (operator) {
                case "<": result = left < right; break;
                case ">": result = left > right; break;
                case "<=": result = left <= right; break;
                case ">=": result = left >= right; break;
                case "==": result = Math.abs(left - right) < 0.000001; break; // Flyttalsjämförelse
                case "!=": result = Math.abs(left - right) > 0.000001; break;
            }
            return String.valueOf(result); // Returnerar "true" eller "false"
            
        } catch (Exception e) {
            return expression; // Om något gick fel, behåll originalet
        }
    }

    private static String evaluateAndReplace(String text, String varName, int value) {
        // Samma logik som förut för kroppen...
        String[] parts = text.split("\"", -1);
        StringBuilder sb = new StringBuilder();
        
        for (int j = 0; j < parts.length; j++) {
            if (j % 2 == 0) {
                String segment = parts[j];
                // Uppdaterat regex för att även fånga jämförelser inuti kroppen (t.ex. if(i==5))
                // Lägger till <>=! i regexet
                String mathRegex = "([0-9\\s\\+\\-\\*\\/\\.\\<\\>\\=\\!]*\\b" + varName + "\\b[0-9\\s\\+\\-\\*\\/\\.\\<\\>\\=\\!]*)";
                
                Matcher m = Pattern.compile(mathRegex).matcher(segment);
                StringBuffer buffer = new StringBuffer();

                while (m.find()) {
                    String expression = m.group(1);
                    String expressionWithVal = expression.replaceAll("\\b" + varName + "\\b", String.valueOf(value));
                    
                    // Här kollar vi om det är en boolean eller vanlig matte
                    String result;
                    if (expressionWithVal.matches(".*[<>=!].*")) {
                        result = evalBoolean(expressionWithVal);
                    } else {
                        try {
                            double d = evalMath(expressionWithVal);
                            result = (d == (long) d) ? String.format("%d", (long) d) : String.valueOf(d);
                        } catch (Exception e) {
                            result = expressionWithVal;
                        }
                    }
                    m.appendReplacement(buffer, result);
                }
                m.appendTail(buffer);
                sb.append(buffer.toString());
            } else {
                sb.append(parts[j]);
                if (j < parts.length - 1) sb.append("\"");
            }
        }
        return sb.toString();
    }

    // Samma matte-parser som tidigare
    public static double evalMath(final String str) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }
            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();
                double x;
                int startPos = this.pos;
                if (eat('(')) { x = parseExpression(); eat(')'); }
                else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else { throw new RuntimeException("Unknown char"); }
                return x;
            }
        }.parse();
    }
}