/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import javax.swing.JFrame;

/**
 *
 * @author 305020571
 */
public class TetrisApp {

    public static void main(String[] args) {
        JFrame myFrame = new JFrame();
        TetrisJPanel myPanel = new TetrisJPanel();

     
        myFrame .add(myPanel);
  
        
        myFrame.setVisible(true);
        myFrame.pack();
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
