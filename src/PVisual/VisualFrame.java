/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisual;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author dahjon
 */
public class VisualFrame extends JFrame {

    JLabel imageLabel = new JLabel("JVisual");
    JLabel debugLabel = new JLabel("i:?");
    int width = 400;
    int height = 400;
    public static final int DO_NOT_SHOW_I = Integer.MIN_VALUE;
    static int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();

    public VisualFrame() {
        setTitle("JVisual");
        add(imageLabel);
        add(debugLabel, BorderLayout.NORTH);
        setSize(width, height);
        debugLabel.setFont(new Font("Lucida", Font.PLAIN, 24));

    }

    public void show(BufferedImage bi, int i) {
        ImageIcon ic = new ImageIcon(bi);
        imageLabel.setIcon(ic);
        if (i != DO_NOT_SHOW_I) {
            debugLabel.setText("i: " + i);
        } else {
            debugLabel.setText("");
        }

//        f.setLocation(pVisual.frameX, pVisual.frameY);
//        pVisual.frameX += width;
//        if (pVisual.frameX + width > PVisual.screenWidth) {
//            pVisual.frameX = 0;
//            pVisual.frameY += height;
//        }
    }

}
