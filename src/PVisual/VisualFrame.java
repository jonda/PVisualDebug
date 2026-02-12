/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisual;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 *
 * @author dahjon
 */
public class VisualFrame extends JFrame {

    JLabel imageLabel = new JLabel("JVisual");
    JTextArea debugLabel = new JTextArea("i:?");
    int width = 400;
    int height = 400;
    public static final int DO_NOT_SHOW_I = Integer.MIN_VALUE;
    static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();

    public VisualFrame() {
        setTitle("JVisual");
        add(imageLabel);
        add(debugLabel, BorderLayout.NORTH);
        debugLabel.setEditable(false);
        setSize(width, height);
        debugLabel.setFont(new Font("Lucida", Font.PLAIN, 24));

    }

    static String findIndexVariable(String code) {
        //String code = "for (int counter = 0; counter < 20; counter++)";

        // Regex-mönstret
        // OBS: I Java-strängar måste vi dubbel-escapa backslash (\\s istället för \s)
        String regex = "for\\s*\\(\\s*\\w+\\s+(\\w+)\\s*=";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);

        if (matcher.find()) {
            // group(1) returnerar det som fanns inuti första parentesen i vårt regex
            String variableName = matcher.group(1);
            System.out.println("Hittad indexvariabel: '" + variableName+"'");
            return variableName.trim();
        } else {
            System.out.println("Ingen matchning hittades.");
            return "";
        }
    }

//    public static String replaceIndexVariable(String code, String indexVariable ,int i){
//        System.out.println("replaceIndexVariable code: '"+code+"', indexVariable: '"+indexVariable+"', i:"+i);
//        String retVal = code.replace(indexVariable, ""+i);
//        System.out.println("retVal = " + retVal);
//        return retVal;
//    }
    public void show(BufferedImage bi, String origCode, int i) {
        ImageIcon ic = new ImageIcon(bi);
        imageLabel.setIcon(ic);
        if (origCode == null) {
            origCode = "i";
        }
        if (origCode.contains("(")) {
            String indexVariable = findIndexVariable(origCode);
            String code1 = replaceIndexVariable(origCode,  i+"");
            String code2 = CodeEvaluator.processCode(origCode, i);
            code1 = indexVariable + ":"+i+"\n"
                    + "Först byter vi ut varabeln med dess värde\n"+
                    code1+
                    "\n\nOch sedan räknar vi ut uttrycket\n"+
                    code2;
            debugLabel.setText(code1);
        } else {
            if (i != DO_NOT_SHOW_I) {
                debugLabel.setText(origCode+": " + i);
            } else {
                debugLabel.setText("");
            }
        }

//        f.setLocation(pVisual.frameX, pVisual.frameY);
//        pVisual.frameX += width;
//        if (pVisual.frameX + width > PVisual.screenWidth) {
//            pVisual.frameX = 0;
//            pVisual.frameY += height;
//        }
    }




//-----------------------------------
    public static String replaceIndexVariable(String code, String replacement) {
        // 1. Hitta variabelnamnet (t.ex. "i")
        Pattern pattern = Pattern.compile("for\\s*\\(\\s*int\\s+(\\w+)");
        Matcher matcher = pattern.matcher(code);
        
        String varName = "";
        if (matcher.find()) {
            varName = matcher.group(1);
        } else {
            return code; // Ingen loop hittad
        }

        // 2. Hitta positionerna för for-loopens parenteser
        int forIndex = code.indexOf("for");
        int openParen = code.indexOf("(", forIndex);
        int closeParen = code.indexOf(")", openParen);
        
        // Om vi inte hittar parenteserna, avbryt
        if (openParen == -1 || closeParen == -1) return code;

        // 3. Extrahera innehållet i for-loopen: "int i=0; i < 20; i++"
        String forContent = code.substring(openParen + 1, closeParen);
        
        // Dela upp innehållet vid semikolon
        String[] forParts = forContent.split(";");
        
        StringBuilder sb = new StringBuilder();

        // --- BYGG IHOP KODEN IGEN ---

        // A. Lägg till allt INNAN for-loopen (t.ex. "fill(255...  for ")
        sb.append(code.substring(0, openParen + 1));

        // B. Hantera for-loopens tre delar
        if (forParts.length == 3) {
            // Del 1: "int i=0" -> Behålls original (byts INTE ut)
            sb.append(forParts[0]).append(";");

            // Del 2: " i < 20" -> Här byter vi ut variabeln!
            // Vi använder vår hjälpmetod för att byta ut säkert
            sb.append(replaceSafe(forParts[1], varName, replacement)).append(";");

            // Del 3: " i++" -> Behålls original (byts INTE ut)
            sb.append(forParts[2]);
        } else {
            // Om loopen ser konstig ut, lägg bara tillbaka den som den var
            sb.append(forContent);
        }

        // C. Lägg till parentesen som avslutar loopen
        sb.append(")");

        // D. Hantera resten av koden (loop-kroppen)
        // Här byter vi ut alla förekomster av 'i', men skyddar textsträngar.
        String body = code.substring(closeParen + 1);
        sb.append(replaceSafe(body, varName, replacement));

        return sb.toString();
    }

    // Hjälpmetod: Byter ut variabeln men undviker textsträngar ("...")
    private static String replaceSafe(String text, String targetVar, String replacement) {
        String[] parts = text.split("\"", -1);
        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < parts.length; j++) {
            // Jämna index är kod, udda index är textsträngar
            if (j % 2 == 0) {
                // Använd Word Boundary (\b) för att bara byta "i", inte "int"
                String regex = "\\b" + targetVar + "\\b";
                parts[j] = parts[j].replaceAll(regex, replacement);
            }
            sb.append(parts[j]);
            if (j < parts.length - 1) {
                sb.append("\"");
            }
        }
        return sb.toString();
    }
}