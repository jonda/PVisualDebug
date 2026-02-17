/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisualTool;

import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import processing.app.Base;
import processing.app.ui.Editor;
import processing.app.ui.EditorToolbar;

/**
 *
 * @author dahjon
 */
public class PVisualConfig extends JFrame {

    JPanel buttonPanel = new JPanel();
    JButton addPVisionFunctionsButton = new JButton("Lägg till funktioner i koden som krävs för PVision");
    JButton removePVisionFunctionsButton = new JButton("Ta bort PVision-relaterade funktioner från koden ");
    Base base;

    public PVisualConfig(Base base) {
        System.out.println("->PVisualConfig");
        this.base = base;
        setTitle("PVisual Config Window");
        add(buttonPanel);
        buttonPanel.add(addPVisionFunctionsButton);
        buttonPanel.add(removePVisionFunctionsButton);
        addPVisionFunctionsButton.addActionListener(this::addFunctionsActionPerformed);
        removePVisionFunctionsButton.addActionListener(this::removeFunctionsActionPerformed);
        pack();
        Editor editor = base.getActiveEditor();
        Point location = editor.getLocation();
        setLocation(Math.max(0,location.x-100), Math.max(0,location.y-getHeight()));
        setVisible(true);
    }

    void addFunctionsActionPerformed(ActionEvent ae) {
        Editor editor = base.getActiveEditor();
        
        String code = editor.getText();
        String newCode = InsertUtils.insertPVisualFunctions(code);
        editor.setText(newCode);
//        EditorToolbar et = editor.getToolbar();
//        System.out.println("et.handleRun()");
//        et.handleRun(0);
//        editor.setText(code);

    }

    void removeFunctionsActionPerformed(ActionEvent ae) {
        System.out.println("---------------------removeFunctionsActionPerformed----------------");
        Editor editor = base.getActiveEditor();
        String code = editor.getText();
        String newCode = InsertUtils.removePVisualFunctions(code);
        editor.setText(newCode);        
    }

}
