/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisualTool;

/**
 *
 * @author dahjon
 */
import processing.app.Base;
import processing.app.tools.Tool;
import processing.app.ui.Editor;
public class PVisualTool  implements Tool {
  Base base;

  // In Processing 3, the "Base" object is passed instead of an "Editor"
  public void init(Base base) {
    // Store a reference to the Processing application itself
    this.base = base;
  }

  public void run() {
    // Run this Tool on the currently active Editor window
    //editor.setText("Deleted your code. What now?");
    new PVisualConfig(base);
  }

  public String getMenuTitle() {
    return "PVisual tool";
  }
  
  


}

