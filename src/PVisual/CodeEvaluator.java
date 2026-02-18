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

public class CodeEvaluator {
public static void main(String[] args) {
        String rawCode = 
            "// Test av For-loop\n" +
            "for (int i=0; i < 20; i++) {\n" +
            "  circle(20*i, 100, 10);\n" +
            "}\n" +
            "\n" +
            "// Test av While-loop\n" +
            "int i = 0;\n" +
            "while (i < 10) {\n" +
            "  if (i == 5) println(\"Mitt i prick\");\n" +
            "  i++;\n" +
            "}";

        int val = 5; // Vi sätter i = 5

        System.out.println("--- INPUT ---");
        System.out.println(rawCode);
        
        System.out.println("\n--- RESULTAT ---");
        System.out.println(processCode(rawCode, "i", val));
    }

    public static String processCode(String code, String varName, int val) {
        // STEG 1: Skydda textsträngar (så vi inte ändrar text inuti "...")
        // Vi byter ut alla "text" mot __STR0__, __STR1__ etc.
        List<String> stringLiterals = new ArrayList<>();
        Matcher stringMatcher = Pattern.compile("\"([^\"]*)\"").matcher(code);
        StringBuffer sbRaw = new StringBuffer();
        while (stringMatcher.find()) {
            stringLiterals.add(stringMatcher.group(0));
            stringMatcher.appendReplacement(sbRaw, "__STR" + (stringLiterals.size() - 1) + "__");
        }
        stringMatcher.appendTail(sbRaw);
        String safeCode = sbRaw.toString();

        // STEG 2: Maskera For-loopens initiering och uppdatering
        // Vi letar efter: for ( int i=... ;  OCH  ; i++ )
        // Vi vill behålla dessa exakt som de är.
        String parsedCode = maskForLoops(safeCode, varName);

        // STEG 3: Byt ut variabeln mot värdet
        // Nu är for-loopens känsliga delar borta (ersatta med platshållare), 
        // så vi kan byta ut ALLA 'i' som är kvar.
        parsedCode = parsedCode.replaceAll("\\b" + varName + "\\b", String.valueOf(val));

        // STEG 4: Evaluera uttryck (Matte och Boolean)
        // Nu står det t.ex. "while (5 < 10)" eller "20 * 5". Vi räknar ut dem.
        parsedCode = evaluateExpressions(parsedCode);

        // STEG 5: Återställ For-loopar och Textsträngar
        parsedCode = unmaskForLoops(parsedCode);
        
        // Återställ strängar
        for (int k = 0; k < stringLiterals.size(); k++) {
            parsedCode = parsedCode.replace("__STR" + k + "__", stringLiterals.get(k));
        }

        return parsedCode;
    }

    // --- MASKERINGS-LOGIK ---
    // Vi använder statiska listor för enkelhetens skull i detta exempel för att lagra de maskerade delarna
    static List<String> forInits = new ArrayList<>();
    static List<String> forUpdates = new ArrayList<>();

    private static String maskForLoops(String code, String varName) {
        forInits.clear();
        forUpdates.clear();
        
        // Regex för att hitta hela for-parentesen: for ( A ; B ; C )
        // Vi måste fånga grupp 1 (A) och grupp 3 (C) om de innehåller vår variabel
        Pattern p = Pattern.compile("for\\s*\\(([^;]*);([^;]*);([^)]*)\\)");
        Matcher m = p.matcher(code);
        StringBuffer sb = new StringBuffer();
        
        while (m.find()) {
            String init = m.group(1); // t.ex. "int i=0"
            String cond = m.group(2); // t.ex. " i < 20"
            String update = m.group(3); // t.ex. " i++"

            // Om denna loop handlar om vår variabel, maskera start och slut
            if (init.trim().startsWith("int " + varName) || update.contains(varName)) {
                forInits.add(init);
                forUpdates.add(update);
                
                // Ersätt med: for ( __FORINIT_0__ ; cond ; __FORUPD_0__ )
                String replacement = "for (__FORINIT_" + (forInits.size()-1) + "__;" + cond + ";__FORUPD_" + (forUpdates.size()-1) + "__)";
                m.appendReplacement(sb, replacement);
            } else {
                // Om det är en loop för en annan variabel (t.ex. k), gör inget
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
        
        // Regex som letar efter:
        // 1. Boolean jämförelser:  5 < 10,  5 == 5, 10 >= 5
        // 2. Enkel matte: 20 * 5, 5 + 10
        // Vi matchar siffror följt av en operator följt av siffror
        
        // Mönster: (siffror) (mellanslag) (operator) (mellanslag) (siffror)
        // Operatorer: <, >, <=, >=, ==, !=, +, -, *, /
        String regex = "(\\d+)\\s*([<>=!]+|[\\+\\-\\*\\/])\\s*(\\d+)";
        
        // Vi kör detta i en loop för att hantera kedjor eller nästlade uttryck enkelt
        // (En full parser är bättre för komplex matte, men detta löser while(5 < 10))
        
        // Först en grov sökning. Vi måste köra while-loopen flera gånger om vi har t.ex. 5 + 5 + 5
        // Men för villkor (5 < 10) räcker en passering oftast.
        
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(code);
        
        while (m.find()) {
            double left = Double.parseDouble(m.group(1));
            String op = m.group(2);
            double right = Double.parseDouble(m.group(3));
            
            String result = "";
            boolean isBool = false;
            
            switch(op) {
                // Boolean
                case "<": result = String.valueOf(left < right); isBool = true; break;
                case ">": result = String.valueOf(left > right); isBool = true; break;
                case "<=": result = String.valueOf(left <= right); isBool = true; break;
                case ">=": result = String.valueOf(left >= right); isBool = true; break;
                case "==": result = String.valueOf(Math.abs(left - right) < 0.001); isBool = true; break;
                case "!=": result = String.valueOf(Math.abs(left - right) > 0.001); isBool = true; break;
                
                // Matte
                case "*": result = fmt(left * right); break;
                case "/": result = fmt(left / right); break;
                case "+": result = fmt(left + right); break;
                case "-": result = fmt(left - right); break;
            }
            
            m.appendReplacement(sb, result);
        }
        m.appendTail(sb);
        
        // Om vi gjorde ersättningar, kör en gång till för att fånga eventuella nya mönster
        // (t.ex. om vi hade "2 * 5 < 20", blev det "10 < 20", nu måste vi lösa det också)
        if (!sb.toString().equals(code)) {
            return evaluateExpressions(sb.toString());
        }
        
        return sb.toString();
    }

    private static String fmt(double d) {
        if (d == (long) d) return String.format("%d", (long) d);
        return String.valueOf(d);
    }
}