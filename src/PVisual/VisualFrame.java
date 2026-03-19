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

import javax.swing.BoxLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import processing.core.PApplet;

/**
 *
 * @author dahjon
 */
public class VisualFrame extends JDialog {

    boolean autoMode = false;

    JLabel imageLabel = new JLabel();
    JTextArea debugArea = new JTextArea("i:?");
//    JEditorPane origCodeArea = new JEditorPane();
    JTextPane origCodeArea = new JTextPane();
    JScrollPane origCodeScroll = new JScrollPane(origCodeArea);
    JTextPane code1Area = new JTextPane();
    JScrollPane code1Scroll = new JScrollPane(code1Area);
    JTextPane code2Area = new JTextPane();
    JScrollPane code2Scroll = new JScrollPane(code2Area);
    PVRowList rowList = new PVRowList();

    public PVRowList getRowList() {
        return rowList;
    }

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
    final TitledBorder origCodeBorder = new TitledBorder("Orginalkod:");
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
        //add(imageLabel, BorderLayout.SOUTH);
        add(debugArea, BorderLayout.NORTH);

        JPanel codePanel = new JPanel();
        codePanel.setLayout(new BoxLayout(codePanel, BoxLayout.X_AXIS));
        debugArea.setBorder(new CompoundBorder(new EmptyBorder(padding, padding, padding, padding), indexVariabelBorder));
        origCodeScroll.setBorder(new CompoundBorder(new EmptyBorder(padding, padding, padding, padding), origCodeBorder));

//        code1Area.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED),new TitledBorder("Först byter vi ut varabeln med dess värde")));
        code1Scroll.setBorder(new CompoundBorder(new EmptyBorder(padding, padding, padding, padding), new TitledBorder("Först byter vi ut varabeln med dess innehåll")));
        code2Scroll.setBorder(new CompoundBorder(new EmptyBorder(padding, padding, padding, padding), new TitledBorder("Och sedan räknar vi ut uttrycket")));
        //add(origCodeScroll, BorderLayout.WEST);
        codePanel.add(origCodeScroll);
        codePanel.add(code1Scroll);
        codePanel.add(code2Scroll);
        codePanel.add(imageLabel);
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

    //Obs denna funktion kör i en annan tråd
    public void show(BufferedImage bi, int rowNr, String variableString, String code1, String code2) {
        System.out.println("-> show rowNr = " + rowNr + ", code1 = " + code1);
        setVisible(true);
        ImageIcon ic = new ImageIcon(bi);

        //lastIndex = i;
        //String indexVariable = "";
        //String debug = "";
        rowList.setDebugInfo(rowNr, code1, code2);

        SwingUtilities.invokeLater(new ShowCode(rowNr, variableString, rowList, ic));

        if (!autoMode) {
            waitForNextButton();
        }
    }

    private class ShowCode implements Runnable {

        String debug;
        PVRowList rowList;

        ImageIcon ic;
        int currRowNr;

        public ShowCode(int currRowNr, String debug, PVRowList rowList, ImageIcon ic) {
            this.currRowNr = currRowNr;
            this.debug = debug;
            this.rowList = rowList;

            this.ic = ic;

        }

        @Override
        public void run() {
            debugArea.setText(debug);

            origCodeArea.setText("");
            code1Area.setText("");
            code2Area.setText("");
            final Document origDoc = origCodeArea.getDocument();
            final Document code1Doc = code1Area.getDocument();
            final Document code2Doc = code2Area.getDocument();
            boolean extraLinesInserted = false;
            for (int i = 0; i < rowList.size(); i++) {
                PVRow row = rowList.get(i);
                StringBuilder sb = new StringBuilder();
                sb.append(row.getRowNr());
                sb.append(' ');
                sb.append(row.getOrigCode());
                sb.append('\n');
                try {
                    SimpleAttributeSet colorCode = new SimpleAttributeSet();
                    StyleConstants.setFontFamily(colorCode, "Courier New Italic");
                    StyleConstants.setFontSize(colorCode, 12);

                    if (row.getRowNr() == currRowNr) {
                        StyleConstants.setForeground(colorCode, Color.RED);
                    } else {
                        StyleConstants.setForeground(colorCode, Color.BLUE);

                    }
                    origDoc.insertString(origDoc.getLength(), sb.toString(), colorCode);
                    
                    if(row.getRowNr() == currRowNr +1 && !row.getCode1().isBlank()){
                        
                        extraLinesInserted = true;
                    }

                    if (!extraLinesInserted && row.getRowNr() > currRowNr && row.getCode1().isBlank()) {
                        if (!row.getOrigCode().isBlank() && !row.getOrigCode().trim().startsWith("//")) {
                            row.setCode1andCode2toOrigCode();
                            extraLinesInserted = true;
                        }
                    }
                    String code1 = row.getCode1();
                    String code2 = row.getCode2();
                    if (code1 != null && !code1.isBlank()) {
                        code1Doc.insertString(code1Doc.getLength(), row.getRowNr() + " " + code1 + "\n", colorCode);
                        code2Doc.insertString(code2Doc.getLength(), row.getRowNr() + " " + code2 + "\n", colorCode);
                    }
                } catch (BadLocationException ex) {
                    JOptionPane.showMessageDialog(VisualFrame.this, "Problem att lägga till i textarea: " + ex.getMessage());
                }
            }

            imageLabel.setIcon(ic);

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
