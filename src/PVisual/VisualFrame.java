/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisual;

import java.awt.BorderLayout;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
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
    JTextArea origCodeArea = new JTextArea();
    JTextArea code1Area = new JTextArea();
    JTextArea code2Area = new JTextArea();

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

    public String getLastForBlock() {
        return lastForBlock;
    }

    public VisualFrame(PApplet par, int delayValue) {
        //setModal(!autoMode);

        if (delayValue >= 0) {
            autoMode = true;
        }
        setTitle("JVisual");
        add(imageLabel, BorderLayout.SOUTH);
        add(debugArea, BorderLayout.NORTH);

        JPanel codePanel = new JPanel();
        codePanel.setLayout(new BoxLayout(codePanel, BoxLayout.Y_AXIS));
        debugArea.setBorder(new CompoundBorder(new EmptyBorder(padding,padding,padding,padding),new TitledBorder("Indexvariabel")));
        origCodeArea.setBorder(new CompoundBorder(new EmptyBorder(padding,padding,padding,padding),new TitledBorder("Loopens kod:")));

//        code1Area.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED),new TitledBorder("Först byter vi ut varabeln med dess värde")));
        code1Area.setBorder(new CompoundBorder(new EmptyBorder(padding,padding,padding,padding),new TitledBorder("Först byter vi ut varabeln med dess värde")));
        code2Area.setBorder(new CompoundBorder(new EmptyBorder(padding,padding,padding,padding),new TitledBorder("Och sedan räknar vi ut uttrycket")));
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
        // Testfall
        System.out.println(extractVariable("while(a<5)"));           // Output: a
        System.out.println(extractVariable("while(s.equals(\"hej\"))")); // Output: s
        System.out.println(extractVariable("while ( ! isRunning )"));    // Output: isRunning
        System.out.println(extractVariable("while(counter >= 10)"));     // Output: counter
    }

    /**
     * Metod för att extrahera den första variabeln i ett while-villkor.
     * @param codeLine 
     * @return 
     */
    public static String extractVariable(String codeLine) {
        // 1. Regex för att hitta allt inuti while-parenteserna
        // while\s* = matchar "while" följt av valfritt antal mellanslag
        // \((.*)\) = fångar allt inuti de yttersta parenteserna
        Pattern pattern = Pattern.compile("while\\s*\\((.*)\\)");
        Matcher matcher = pattern.matcher(codeLine);

        if (matcher.find()) {
            // Hämta innehållet, t.ex. "a<5" eller " s.equals(...) "
            String condition = matcher.group(1).trim();

            // 2. Ta bort eventuellt utropstecken i början (för !variabel)
            if (condition.startsWith("!")) {
                condition = condition.substring(1).trim();
            }

            // 3. Dela upp strängen vid första tecknet som INTE är en del av ett namn.
            // Vi splittrar vid mellanslag, punkt, eller operatorer (<, >, =, !)
            String[] parts = condition.split("[\\s.<>=!]+");

            // Returnera den första delen, vilket bör vara variabelnamnet
            if (parts.length > 0) {
                return parts[0];
            }
        }

        return "Ingen variabel hittades";
    }

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
            //System.out.println("Hittad indexvariabel: '" + variableName + "'");
            return variableName.trim();
        } else {
            //System.out.println("Ingen matchning hittades.");
            return "";
        }
    }

//    public static String replaceIndexVariable(String code, String indexVariable ,int i){
//        System.out.println("replaceIndexVariable code: '"+code+"', indexVariable: '"+indexVariable+"', i:"+i);
//        String retVal = code.replace(indexVariable, ""+i);
//        System.out.println("retVal = " + retVal);
//        return retVal;
//    }
    
    //Obs denna funktion kör i en annan tråd
    public void show(BufferedImage bi, String origCode, int i, BlockType type) {
        System.out.println("-> show origCode = " + origCode);
        setVisible(true);
        ImageIcon ic = new ImageIcon(bi);
        
        lastIndex = i;
        if (origCode == null) {
            origCode = "i";
        }
        String indexVariable = "";
        String code1 = "";
        String code2 = "";
        String debug = "";
        if (origCode.contains("(")) {
            ;
            if (type == BlockType.FOR) {
                lastForBlock = origCode;
                indexVariable = findIndexVariable(origCode);
                code1 = replaceIndexVariable(origCode, i + "");
            } else {
                indexVariable = extractVariable(origCode);
                code1 = replaceSafe(origCode, indexVariable, i + "");
            }

            System.out.println("indexVariable: " + indexVariable + "=" + i);
            System.out.println("code1 = " + code1);
            code2 = CodeEvaluator.processCode(origCode, indexVariable, i);
            System.out.println("code2 = " + code2);
            debug = indexVariable + ":" + i;
//                    + "\n"
//                    + code1
//                    + "\n\n\n"
//                    + code2;
        } else {
            System.out.println("i = " + i + ", DO_NOT_SHOW_I = " + DO_NOT_SHOW_I);
            if (i != DO_NOT_SHOW_I) {
                debugArea.setText(origCode + ": " + i);
            } else {
                debugArea.setText("");
            }
        }
        
        SwingUtilities.invokeLater(new ShowCode(debug, origCode,code1, code2,ic));
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
            origCodeArea.setText(origCode);
            code1Area.setText(code1);
            code2Area.setText(code2);  
            imageLabel.setIcon(ic); 
            pack();
        }
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
        if (openParen == -1 || closeParen == -1) {
            return code;
        }

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
