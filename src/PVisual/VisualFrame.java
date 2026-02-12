/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisual;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import processing.core.PImage;

/**
 *
 * @author dahjon
 */
public class VisualFrame extends JFrame{
        JLabel jl = new JLabel("JVisual");
        int width = 400;
        int height = 400;

    public VisualFrame() {
            setTitle("JVisual");
        add(jl);
        setSize(width, height);
        setVisible(true);
}

    
    
    public void show(BufferedImage bi) {
        ImageIcon ic = new ImageIcon(bi);
        jlsetIcon(ic);
//        f.setLocation(pVisual.frameX, pVisual.frameY);
//        pVisual.frameX += width;
//        if (pVisual.frameX + width > PVisual.screenWidth) {
//            pVisual.frameX = 0;
//            pVisual.frameY += height;
//        }
        PImage sketchImage = pVisual.parent.get();
        BufferedImage bi = (BufferedImage) sketchImage.getNative();
    }
    
}
