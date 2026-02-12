package PVisual;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

    VisualFrame vf = new VisualFrame();
      
        // width will store the width of the screen
        
    public PVisual(PApplet parent) {
        this.parent = parent;
        //parent.registerMethod("dispose", this);
    }

    
    public static void hello(){
        System.out.println("Hello PVisual");
    }
    
    public void show(){
        show(VisualFrame.DO_NOT_SHOW_I);
        
    }
    public void show(int i){
        PImage sketchImage = parent.get();
        BufferedImage bi = (BufferedImage) sketchImage.getNative();
        vf.show(bi,i);
        vf.setVisible(true);
    }

}
