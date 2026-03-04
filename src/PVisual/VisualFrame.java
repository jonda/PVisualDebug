/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import processing.core.PApplet;

/**
 *
 * @author dahjon
 */
public class VisualFrame extends JDialog {

    boolean autoMode = false;

    JLabel imageLabel = new JLabel("JVisual");
    JTextArea debugArea = new JTextArea("i:?");
//    JEditorPane origCodeArea = new JEditorPane();
    JTextArea origCodeArea = new JTextArea();
    JTextArea code1Area = new JTextArea();
    JTextArea code2Area = new JTextArea();
    PVRowList rowList = new PVRowList();

    //JButton nextButton = new JButton("Nästa");
//        JEditTextArea debugLabel = new JEditTextArea(new PdeTextAreaDefaults(),
//                             new PdeInputHandler());
    int width = 400;
    int height = 700;
    public static final int DO_NOT_SHOW_I = Integer.MIN_VALUE;
    static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    int lastIndex = 0;
    String lastForBlock = "";
    int padding = 10;
    final TitledBorder origCodeBorder = new TitledBorder("Loopens kod:");
    final TitledBorder indexVariabelBorder = new TitledBorder("Indexvariabel");

    public String getLastForBlock() {
        return lastForBlock;
    }

    public VisualFrame(PApplet par, int delayValue) {
        //origCodeArea.setContentType("text/html");
        //setModal(!autoMode);

        if (delayValue >= 0) {
            autoMode = true;
        }
        setTitle("JVisual");
        add(imageLabel, BorderLayout.SOUTH);
        add(debugArea, BorderLayout.NORTH);

        JPanel codePanel = new JPanel();
        codePanel.setLayout(new BoxLayout(codePanel, BoxLayout.Y_AXIS));
        debugArea.setBorder(new CompoundBorder(new EmptyBorder(padding, padding, padding, padding), indexVariabelBorder));
        origCodeArea.setBorder(new CompoundBorder(new EmptyBorder(padding, padding, padding, padding), origCodeBorder));

//        code1Area.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED),new TitledBorder("Först byter vi ut varabeln med dess värde")));
        code1Area.setBorder(new CompoundBorder(new EmptyBorder(padding, padding, padding, padding), new TitledBorder("Först byter vi ut varabeln med dess innehåll")));
        code2Area.setBorder(new CompoundBorder(new EmptyBorder(padding, padding, padding, padding), new TitledBorder("Och sedan räknar vi ut uttrycket")));
        codePanel.add(origCodeArea);
        codePanel.add(code1Area);
        codePanel.add(code2Area);
        add(codePanel, BorderLayout.CENTER);
        origCodeArea.setEditable(false);
        code1Area.setEditable(false);
        code2Area.setEditable(false);
        debugArea.setEditable(false);
        final Font font = new Font("Monospaced", Font.PLAIN, 20);
        origCodeArea.setFont(font);
        code1Area.setFont(font);
        code2Area.setFont(font);
        debugArea.setFont(font);
        setSize(width, height);
        setLocation(100, 100);
        //debugLabel.setFont(new Font("Lucida", Font.PLAIN, 24));

    }

//    void nextButtonPerformed(ActionEvent ae) {
//        setVisible(false);
//        Timer tim = new Timer(0, this::showAgainPerformed);
//
//    }
//
//    void showAgainPerformed(ActionEvent ae) {
//        setVisible(true);
//
//    }
    void waitForNextButton() {
//        JOptionPane optionPane = new JOptionPane("Tryck på knappen för att gå vidare", JOptionPane.INFORMATION_MESSAGE);
//        
//        JDialog dialog = optionPane.createDialog("Title");
//        dialog.setVisible(true);
        JDialog d = new JDialog(this, true);
        JButton knapp = new JButton("Gå vidare");
        knapp.addActionListener((ae) -> {
            System.out.println("gömmer nu");
            d.setVisible(false);
            d.dispose();
        });
        knapp.setFont(new Font("Lucida", Font.PLAIN, 50));
        d.add(knapp);
        d.pack();
        Point p = getLocation();
        Dimension s = d.getSize();
        d.setLocation(p.x, p.y - s.height); // Set custom location
        d.setVisible(true);

    }

    public int getLastIndex() {
        return lastIndex;
    }

    public static void main(String[] args) {
        // --- Testfall ---

//        // Krav 1: Ska fungera på både if och while
//        System.out.println(extractVariable("if(a < 5)"));                  // Output: a
//        System.out.println(extractVariable("while ( ! isRunning )"));      // Output: isRunning
//
//        // Krav 2: Variabeln på höger sida
//        System.out.println(extractVariable("if(5 < b)"));                  // Output: b
//
//        // Komplexa fall med metoder och strängar
//        System.out.println(extractVariable("while(\"hej\".equals(s))"));   // Output: s
//        System.out.println(extractVariable("if(true == minVariabel)"));    // Output: minVariabel
//        System.out.println(extractVariable("if (10.5 >= counter)"));       // Output: counter
    }

//    /**
//     * Metod för att extrahera den första variabeln i ett if- eller
//     * while-villkor.
//     */
//    public static String extractVariable(String codeLine) {
//        // 1. Regex för att hitta både 'while' och 'if'
//        // (?:while|if) matchar antingen "while" eller "if" utan att spara själva ordet i en egen grupp.
//        Pattern statementPattern = Pattern.compile("(?:while|if)\\s*\\((.*)\\)");
//        Matcher statementMatcher = statementPattern.matcher(codeLine);
//
//        if (statementMatcher.find()) {
//            // Hämta hela villkoret inuti parenteserna
//            String condition = statementMatcher.group(1).trim();
//
//            // 2. Rensa bort all text inuti citattecken ("...") 
//            // Detta förhindrar att vi råkar tro att ett ord inuti en sträng är en variabel.
//            condition = condition.replaceAll("\".*?\"", "");
//
//            // 3. Regex för att hitta giltiga Java-namn (variabler/metoder)
//            // [a-zA-Z_$] = Måste börja med bokstav, _ eller $
//            // [a-zA-Z0-9_$]* = Får följas av noll eller fler bokstäver, siffror, _ eller $
//            Pattern idPattern = Pattern.compile("[a-zA-Z_$][a-zA-Z0-9_$]*");
//            Matcher idMatcher = idPattern.matcher(condition);
//
//            while (idMatcher.find()) {
//                String match = idMatcher.group();
//
//                // 4. Filtrera bort boolean-värden och null
//                if (match.equals("true") || match.equals("false") || match.equals("null")) {
//                    continue;
//                }
//
//                // 5. Kolla om ordet följs av en vänsterparentes '('
//                // Om det gör det, är det ett metodanrop (t.ex. 'equals'), inte en variabel.
//                int end = idMatcher.end();
//                boolean isMethodCall = false;
//                for (int i = end; i < condition.length(); i++) {
//                    char c = condition.charAt(i);
//                    if (c == '(') {
//                        isMethodCall = true;
//                        break;
//                    } else if (!Character.isWhitespace(c)) {
//                        // Vi hittade ett annat tecken (t.ex. '.' eller '==') innan en eventuell parentes
//                        break;
//                    }
//                }
//
//                // Om det var en metod, hoppa till nästa matchning i while-loopen
//                if (isMethodCall) {
//                    continue;
//                }
//
//                // Har vi passerat alla filter ovan? Då har vi hittat vår variabel!
//                return match;
//            }
//        }
//
//        return "Ingen variabel hittades";
//    }

//    public static String replaceIndexVariable(String code, String indexVariable ,int i){
//        System.out.println("replaceIndexVariable code: '"+code+"', indexVariable: '"+indexVariable+"', i:"+i);
//        String retVal = code.replace(indexVariable, ""+i);
//        System.out.println("retVal = " + retVal);
//        return retVal;
//    }
    //Obs denna funktion kör i en annan tråd
    public void show(BufferedImage bi, int rowNr, String variableString, String origCode, String code1, String code2) {
        System.out.println("-> show origCode = " + origCode);
        setVisible(true);
        ImageIcon ic = new ImageIcon(bi);

        //lastIndex = i;
        if (origCode == null) {
            origCode = "i";
        }
        //String indexVariable = "";
        //String debug = "";
        if (origCode.contains("\n")) {
            String[] arr = origCode.split("\n");
            rowList.set(new PVRow(rowNr, arr[0], code1, code2));
            rowList.set(new PVRow(rowNr + 1, arr[1], arr[1], arr[1]));

        } else {
            rowList.set(new PVRow(rowNr, origCode, code1, code2));
        }

        SwingUtilities.invokeLater(new ShowCode(variableString, rowList.getOrigCode(), rowList.getCode1(), rowList.getCode2(), ic));
        //SwingUtilities.invokeLater(new ShowCode(debug, origCode, code1, code2, ic));

//        if (origCode.contains("(")) {
//            ;
//            if (type == BlockType.FOR) {
//                lastForBlock = origCode;
//                indexVariable = findIndexVariable(origCode);
//                code1 = replaceIndexVariable(origCode, i + "");
//            } else {
//                indexVariable = extractVariable(origCode);
//                code1 = replaceSafe(origCode, indexVariable, i + "");
//            }
//            indexVariabelBorder.setTitle(type.getVariabelBeteckning());
//            origCodeBorder.setTitle(type.getFullName() + "ens kod");
//            System.out.println("indexVariable: " + indexVariable + "=" + i);
//            System.out.println("code1 = " + code1);
//            code2 = CodeEvaluator.processCode(origCode, indexVariable, i);
//            System.out.println("code2 = " + code2);
//            debug = indexVariable + ":" + i;
        ////                    + "\n"
////                    + code1
////                    + "\n\n\n"
////                    + code2;
//
//        } else {
//            System.out.println("i = " + i + ", DO_NOT_SHOW_I = " + DO_NOT_SHOW_I);
//            if (i != DO_NOT_SHOW_I) {
//                debugArea.setText(origCode + ": " + i);
//            } else {
//                debugArea.setText("");
//            }
//        }
//
//            debugArea.setText(debug);
//            code1Area.setText(code1);
//            code2Area.setText(code2);

//        f.setLocation(pVisual.frameX, pVisual.frameY);
//        pVisual.frameX += width;
//        if (pVisual.frameX + width > PVisual.screenWidth) {
//            pVisual.frameX = 0;
//            pVisual.frameY += height;
//        }
        if (!autoMode) {
            waitForNextButton();
        }
    }

    private class ShowCode implements Runnable {

        String debug;
        String origCode;
        String code1;
        String code2;
        ImageIcon ic;

        public ShowCode(String debug, String origCode, String code1, String code2, ImageIcon ic) {
            this.debug = debug;
            this.origCode = origCode;
            this.code1 = code1;
            this.code2 = code2;
            this.ic = ic;

        }

        @Override
        public void run() {
            debugArea.setText(debug);
            code1Area.setText(code1);
            code2Area.setText(code2);
            imageLabel.setIcon(ic);
            int endBraceindex = origCode.lastIndexOf('}');
            System.out.println("endBraceindex = " + endBraceindex);
            int startOfEndLine = origCode.lastIndexOf('\n', endBraceindex);
            int endOfEndLine = origCode.indexOf('\n', endBraceindex);
            if (endOfEndLine == -1) {
                endOfEndLine = origCode.length() - 1;
            }
            System.out.println("origCode.length() = " + origCode.length());

            //origCode = "<pre>"+ origCode.substring(0, startOfEndLine) + "<b>"+origCode.substring(startOfEndLine, endOfEndLine+1)+"</b>"+origCode.substring(endOfEndLine+1)+"</pre>";
            System.out.println("origCode = " + origCode);
            origCodeArea.setText(origCode);
//            origCodeArea.setSelectionColor(Color.red);
//            origCodeArea.setSelectionStart(startOfEndLine);
            System.out.println("startOfEndLine = " + startOfEndLine);
//            origCodeArea.setSelectionEnd(endOfEndLine);
            System.out.println("endOfEndLine = " + endOfEndLine);
            System.out.println("origCode.length() = " + origCode.length());
            pack();
        }
    }

//-----------------------------------
//    public static String replaceIndexVariable(String code, String replacement) {
//        // 1. Hitta variabelnamnet (t.ex. "i")
//        Pattern pattern = Pattern.compile("for\\s*\\(\\s*int\\s+(\\w+)");
//        Matcher matcher = pattern.matcher(code);
//
//        String varName = "";
//        if (matcher.find()) {
//            varName = matcher.group(1);
//        } else {
//            return code; // Ingen loop hittad
//        }
//
//        // 2. Hitta positionerna för for-loopens parenteser
//        int forIndex = code.indexOf("for");
//        int openParen = code.indexOf("(", forIndex);
//        int closeParen = code.indexOf(")", openParen);
//
//        // Om vi inte hittar parenteserna, avbryt
//        if (openParen == -1 || closeParen == -1) {
//            return code;
//        }
//
//        // 3. Extrahera innehållet i for-loopen: "int i=0; i < 20; i++"
//        String forContent = code.substring(openParen + 1, closeParen);
//
//        // Dela upp innehållet vid semikolon
//        String[] forParts = forContent.split(";");
//
//        StringBuilder sb = new StringBuilder();
//
//        // --- BYGG IHOP KODEN IGEN ---
//        // A. Lägg till allt INNAN for-loopen (t.ex. "fill(255...  for ")
//        sb.append(code.substring(0, openParen + 1));
//
//        // B. Hantera for-loopens tre delar
//        if (forParts.length == 3) {
//            // Del 1: "int i=0" -> Behålls original (byts INTE ut)
//            sb.append(forParts[0]).append(";");
//
//            // Del 2: " i < 20" -> Här byter vi ut variabeln!
//            // Vi använder vår hjälpmetod för att byta ut säkert
//            sb.append(replaceSafe(forParts[1], varName, replacement)).append(";");
//
//            // Del 3: " i++" -> Behålls original (byts INTE ut)
//            sb.append(forParts[2]);
//        } else {
//            // Om loopen ser konstig ut, lägg bara tillbaka den som den var
//            sb.append(forContent);
//        }
//
//        // C. Lägg till parentesen som avslutar loopen
//        sb.append(")");
//
//        // D. Hantera resten av koden (loop-kroppen)
//        // Här byter vi ut alla förekomster av 'i', men skyddar textsträngar.
//        String body = code.substring(closeParen + 1);
//        sb.append(replaceSafe(body, varName, replacement));
//
//        return sb.toString();
//    }

    // Hjälpmetod: Byter ut variabeln men undviker textsträngar ("...")
//    private static String replaceSafe(String text, String targetVar, String replacement) {
//        String[] parts = text.split("\"", -1);
//        StringBuilder sb = new StringBuilder();
//
//        for (int j = 0; j < parts.length; j++) {
//            if (j % 2 == 0) {
//                // Förklaring av Regex:
//                // (?<!\+\+|--)\b          <- Får inte föregås av ++ eller --
//                // \btargetVar\b           <- Själva variabelnamnet
//                // (?!(\s*(\+\+|--|=)))    <- Får inte följas av ++, -- eller = (med valfritt mellanrum)
//                String regex = "(?<!\\+\\+|--)\\b" + targetVar + "\\b(?!(\\s*(\\+\\+|--|=)))";
//
//                parts[j] = parts[j].replaceAll(regex, replacement);
//            }
//            sb.append(parts[j]);
//            if (j < parts.length - 1) {
//                sb.append("\"");
//            }
//        }
//        return sb.toString();
//    }
//
}
