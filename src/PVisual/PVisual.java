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
    int frameX = 0;
    int frameY = 0;
    static int screenWidth     = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
      
        // width will store the width of the screen
        
    public PVisual(PApplet parent) {
        this.parent = parent;
        //parent.registerMethod("dispose", this);
    }

    public  void show() {
        int width = 400;
        int height = 400;
        PImage sketchImage = parent.get();
        BufferedImage bi = (BufferedImage) sketchImage.getNative();
        JFrame f = new JFrame();
        f.setTitle("JVisual");
        JLabel jl = new JLabel("JVisual");
        ImageIcon ic = new ImageIcon(bi);
        jl.setIcon(ic);
        f.add(jl);
        f.setLocation(frameX, frameY);
          frameX +=width;
          if(frameX+width>screenWidth){
              frameX = 0;
              frameY += height;
          }
        f.setSize(width,height);
        
        f.setVisible(true);


    }
    
    public static void hello(){
        System.out.println("Hello PVisual");
    }

}
