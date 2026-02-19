/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisual;

/**
 *
 * @author dahjon
 */
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//Skapad av Gemini
public class CodeEvaluator {


    public static void main(String[] args) {
        String rawCode = 
            "// --- WHILE-LOOP TEST ---\n" +
            "int i = 0;             // Ska INTE bytas ut (pga =)\n" +
            "while (i < 10) {       // Ska bytas och evalueras (i < 10)\n" +
            "  if (i == 5) {        // Ska bytas och evalueras (i == 5)\n" +
            "     i += 2;           // Ska INTE bytas ut (pga +=)\n" +
            "  }\n" +
            "  circle(20*i, 10, 5); // Ska bytas och evalueras\n" +
            "  i++;                 // Ska INTE bytas ut (pga ++)\n" +
            "  --i;                 // Ska INTE bytas ut (pga --)\n" +
            "  i = i + 1;                 // Ska INTE bytas ut (pga =)\n" +
            "}\n";

        int val = 5;

        System.out.println("--- INPUT ---");
        System.out.println(rawCode);
        
        System.out.println("\n--- RESULTAT ---");
        System.out.println(processCode(rawCode, "i", val));
    }

    public static String processCode(String code, String varName, int val) {
        // 1. Skydda textsträngar
        List<String> stringLiterals = new ArrayList<>();
        Matcher stringMatcher = Pattern.compile("\"([^\"]*)\"").matcher(code);
        StringBuffer sbRaw = new StringBuffer();
        while (stringMatcher.find()) {
            stringLiterals.add(stringMatcher.group(0));
            stringMatcher.appendReplacement(sbRaw, "__STR" + (stringLiterals.size() - 1) + "__");
        }
        stringMatcher.appendTail(sbRaw);
        String safeCode = sbRaw.toString();

        // 2. Maskera For-loopens initiering och uppdatering
        String parsedCode = maskForLoops(safeCode, varName);

        // 3. BYT UT VARIABELN (MED SMARTA REGLER)
        // Regex förklarat:
        // (?<!\\+\\+\\s*|--\\s*)           -> Inget ++ eller -- före variabeln
        // \\b VAR \\b                      -> Variabelnamnet som ett eget ord
        // (?!\\s*\\+\\+|\\s*--             -> Inget ++ eller -- efter variabeln
        // |\\s*[\\+\\-\\*\\/%]?=(?!=))     -> Inget =, +=, -=, *=, /= (men == är okej!)
        String safeReplaceRegex = "(?<!\\+\\+\\s*|--\\s*)\\b" + varName + "\\b(?!\\s*\\+\\+|\\s*--|\\s*[\\+\\-\\*\\/%]?=(?!=))";
        parsedCode = parsedCode.replaceAll(safeReplaceRegex, String.valueOf(val));

        // 4. Evaluera uttryck (Matte och Boolean)
        parsedCode = evaluateExpressions(parsedCode);

        // 5. Återställ For-loopar
        parsedCode = unmaskForLoops(parsedCode);
        
        // 6. Återställ textsträngar
        for (int k = 0; k < stringLiterals.size(); k++) {
            parsedCode = parsedCode.replace("__STR" + k + "__", stringLiterals.get(k));
        }

        return parsedCode;
    }

    // --- MASKERINGS-LOGIK ---
    static List<String> forInits = new ArrayList<>();
    static List<String> forUpdates = new ArrayList<>();

    private static String maskForLoops(String code, String varName) {
        forInits.clear();
        forUpdates.clear();
        
        Pattern p = Pattern.compile("for\\s*\\(([^;]*);([^;]*);([^)]*)\\)");
        Matcher m = p.matcher(code);
        StringBuffer sb = new StringBuffer();
        
        while (m.find()) {
            String init = m.group(1);
            String cond = m.group(2);
            String update = m.group(3);

            if (init.trim().startsWith("int " + varName) || update.contains(varName)) {
                forInits.add(init);
                forUpdates.add(update);
                String replacement = "for (__FORINIT_" + (forInits.size()-1) + "__;" + cond + ";__FORUPD_" + (forUpdates.size()-1) + "__)";
                m.appendReplacement(sb, replacement);
            } else {
                m.appendReplacement(sb, m.group(0));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String unmaskForLoops(String code) {
        for (int k = 0; k < forInits.size(); k++) {
            code = code.replace("__FORINIT_" + k + "__", forInits.get(k));
            code = code.replace("__FORUPD_" + k + "__", forUpdates.get(k));
        }
        return code;
    }

    // --- EVALUERINGS-LOGIK ---
    private static String evaluateExpressions(String code) {
        StringBuffer sb = new StringBuffer();
        
        // Matchar ex: "5 < 10" eller "20 * 5"
        String regex = "(-?\\d+(?:\\.\\d+)?)\\s*([<>=!]+|[\\+\\-\\*\\/])\\s*(-?\\d+(?:\\.\\d+)?)";
        
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(code);
        boolean replacedAnything = false;
        
        while (m.find()) {
            replacedAnything = true;
            double left = Double.parseDouble(m.group(1));
            String op = m.group(2);
            double right = Double.parseDouble(m.group(3));
            
            String result = m.group(0); // Fallback
            
            try {
                switch(op) {
                    // Boolean
                    case "<": result = String.valueOf(left < right); break;
                    case ">": result = String.valueOf(left > right); break;
                    case "<=": result = String.valueOf(left <= right); break;
                    case ">=": result = String.valueOf(left >= right); break;
                    case "==": result = String.valueOf(Math.abs(left - right) < 0.0001); break;
                    case "!=": result = String.valueOf(Math.abs(left - right) > 0.0001); break;
                    // Matte
                    case "*": result = fmt(left * right); break;
                    case "/": result = fmt(left / right); break;
                    case "+": result = fmt(left + right); break;
                    case "-": result = fmt(left - right); break;
                }
            } catch (Exception e) {
                // Ignore
            }
            m.appendReplacement(sb, result);
        }
        m.appendTail(sb);
        
        if (replacedAnything && !sb.toString().equals(code)) {
            return evaluateExpressions(sb.toString()); // Kör igen för nästlade uttryck
        }
        
        return sb.toString();
    }

    private static String fmt(double d) {
        if (d == (long) d) return String.format("%d", (long) d);
        return String.valueOf(d);
    }
}