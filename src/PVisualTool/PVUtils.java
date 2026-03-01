/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisualTool;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author dahjon
 */
public class PVUtils {
     public static String findIndexVariable(String code) {
        //String code = "for (int counter = 0; counter < 20; counter++)";

        // Regex-mönstret
        // OBS: I Java-strängar måste vi dubbel-escapa backslash (\\s istället för \s)
        String regex = "for\\s*\\(\\s*\\w+\\s+(\\w+)\\s*=";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);

        if (matcher.find()) {
            // group(1) returnerar det som fanns inuti första parentesen i vårt regex
            String variableName = matcher.group(1);
            System.out.println("Hittad indexvariabel: '" + variableName + "' i koden: "+code);
            return variableName.trim();
        } else {
            System.out.println("Ingen matchning hittades.");
            if(code.trim().startsWith("for")){
                System.out.println("Ingen matchn ing hittades trorts att raden startade med for???!!!");
            }
            return "";
        }
    }

    public static ArrayList<CodeRowVar> getFunctionParameters(String codeLine) {
        // Regular expression to find function definitions
        // It looks for a pattern like: public static void myFunction(int param1, String param2) {
        Pattern pattern = Pattern.compile(
            "(?:public|private|protected|static|\\s)*" + // Modifiers
            "\\w+\\s+" +                                  // Return type
            "(\\w+)\\s*" +                                // Function name
            "\\(([^)]*)\\)" +                             // Parameters
            "\\s*\\{?"
        );

        Matcher matcher = pattern.matcher(codeLine);

        if (matcher.find()) {
            ArrayList<CodeRowVar> params = new ArrayList<>();
            String paramsString = matcher.group(2);
            if (paramsString != null && !paramsString.trim().isEmpty()) {
                String[] paramPairs = paramsString.split(",");
                for (String pair : paramPairs) {
                    pair = pair.trim();
                    String[] typeAndName = pair.split("\\s+");
                    if (typeAndName.length == 2) {
                        params.add(new CodeRowVar(typeAndName[0], typeAndName[1], "null"));
                    }
                }
            }
            return params;
        }
        return null;
    }
    public static void main(String[] args) {
        //String code = "void setup(){";
        //String code = "int i = 0;";
        //System.out.println("getFunctionParameters(code) = " + getFunctionParameters(code));
        String c = "fo(int i=0;i<10;i++){\n";
        System.out.println("findIndexVariable(c) = " + findIndexVariable(c));
    }
}
