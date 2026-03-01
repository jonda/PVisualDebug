/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisualTool;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import processing.app.Base;
import processing.app.ui.Editor;
import processing.app.ui.EditorToolbar;

/**
 *
 * @author dahjon
 */
public class PVisualConfig extends JFrame {

    JPanel buttonPanel = new JPanel();
    JButton addPVisionFunctionsButton = new JButton("Kör");//("Lägg till funktioner i koden som krävs för PVision");
    JButton removePVisionFunctionsButton = new JButton("Stäng");//("Ta bort PVision-relaterade funktioner från koden ");
    JCheckBox clickCheckBox = new JCheckBox("Klicka för varje steg");
    JTextField delayField = new JTextField(15);
    JTextArea codeArea = new JTextArea(20, 40);
    final JScrollPane codeScroll = new JScrollPane(codeArea);

    Base base;
    JCheckBox advancedModeBox = new JCheckBox("Advanced mode");
    //private boolean advancedMode = true;

    public PVisualConfig(Base base) {
        System.out.println("->PVisualConfig");
        this.base = base;
        setTitle("PVisual Config Window");
        add(buttonPanel, BorderLayout.NORTH);
        buttonPanel.add(addPVisionFunctionsButton);
        buttonPanel.add(removePVisionFunctionsButton);
        buttonPanel.add(clickCheckBox);
        JPanel delayPanel = new JPanel();
        buttonPanel.add(delayPanel);
        buttonPanel.add(advancedModeBox);
        //delayPanel.add(new JLabel("Ange elay i millisektunder: "));
        delayField.setText("500");
        delayPanel.add(delayField);
        delayPanel.setBorder(new TitledBorder("Fördröjning mellan varje steg"));
        clickCheckBox.addActionListener(this::checkBoxActionPerformed);
        addPVisionFunctionsButton.addActionListener(this::addFunctionsActionPerformed);
        removePVisionFunctionsButton.addActionListener(this::removeFunctionsActionPerformed);
        advancedModeBox.addActionListener(this::advancedModeBoxActionPerformed);
//        if (isAdvancedMode()) {
//        }
        codeArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

        pack();
        Editor editor = base.getActiveEditor();
        Point location = editor.getLocation();
        setLocation(Math.max(0, location.x - 100), Math.max(0, location.y - getHeight()));
        setVisible(true);
    }

    void checkBoxActionPerformed(ActionEvent ae) {
        delayField.setEnabled(!clickCheckBox.isSelected());
    }

    void advancedModeBoxActionPerformed(ActionEvent ae) {
        if (isAdvancedMode()) {
            add(codeScroll, BorderLayout.CENTER);
            pack();
        } else {
            remove(codeScroll);
            pack();
        }
    }

    void addFunctionsActionPerformed(ActionEvent ae) {
        Editor editor = base.getActiveEditor();
        
        String code = editor.getText();
        int delayValue = -1;
        if (!clickCheckBox.isSelected()) {
            delayValue = Integer.parseInt(delayField.getText());
        }
        String newCode = InsertUtils.insertPVisualFunctions(code, delayValue);
        editor.setText(newCode);
        EditorToolbar et = editor.getToolbar();
        System.out.println("et.handleRun()");
        et.handleRun(0);
        editor.setText(code);
        codeArea.setText(newCode);
        //pack();

    }

    void removeFunctionsActionPerformed(ActionEvent ae) {
        System.out.println("---------------------removeFunctionsActionPerformed----------------");
//        Editor editor = base.getActiveEditor();
//        String code = editor.getText();
//        String newCode = InsertUtils.removePVisualFunctions(code);
//        editor.setText(newCode);
        setVisible(false);
        dispose();
    }

    public boolean isAdvancedMode() {
        //return advancedMode;
        return advancedModeBox.isSelected();
    }

}
