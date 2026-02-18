package PVisual;

import java.awt.image.BufferedImage;

import processing.core.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author dahjon
 */
public class PVisual {

    PApplet parent;
    int delayValue;
    VisualFrame vf;

    // width will store the width of the screen
    public PVisual(PApplet parent, int delayValue) {
        this.delayValue = delayValue;
        this.parent = parent;
         vf = new VisualFrame(parent, delayValue);
        
        //parent.registerMethod("dispose", this);
    }

    public static void hello() {
        System.out.println("Hello PVisual");

    }

    public void show() {
        show(VisualFrame.DO_NOT_SHOW_I);

    }

    public void show(int i) {
        String code = getCode();
        show(code, i);

    }

    public void show(int i, BlockType type) {
        String code = getCode();
        show(code, i, type);

    }

    public void showAfterFor() {
        String code = getCode();
        show(code, vf.getLastIndex() + 1);
        System.out.println("Väntar nu: " + delayValue);
    }

    private String getCode() {
        String sketchName = parent.sketchFile(parent.sketchPath()).getName();

        System.out.println("Sketchens namn är : " + sketchName);

        // Nu kan du använda namnet för att titta på koden:
        String fileName = sketchName + ".pde";
        String[] codeLines = parent.loadStrings(fileName);
        String code = "";
        if (codeLines != null) {
            //parent.println("--- KODEN FRÅN " + fileName + " ---");
            for (String line : codeLines) {
                //parent.println(line);
                code += line + "\n";
            }
        }
        return code;
    }

    public void show(String code, int i) {
        show(code, i, BlockType.FOR);
    }

    public void show(String code, int i, BlockType type) {
        PImage sketchImage = parent.get();
        BufferedImage bi = (BufferedImage) sketchImage.getNative();
        vf.show(bi, code, i, type);
        //vf.setVisible(true);
        if (delayValue!=-1) {
            parent.delay(delayValue);
        }

    }

}
