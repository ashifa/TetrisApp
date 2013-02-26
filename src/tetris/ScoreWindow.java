/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;

/**
 *
 * @author 305020571
 */
public class ScoreWindow extends JLabel {

    private int score;
    private int level;

    public ScoreWindow() {

        this.setPreferredSize(new Dimension(200, 100));
        this.setFont(new Font(Font.SANS_SERIF, Font.BOLD|Font.ITALIC, 25));
        this.setForeground(Color.red);
        //this.setText("<html><body style=font-family:arial;color:red;font-size:20px> Score: " + score + "<hr /> Level: " + level + "</body></html>");
        this.setText("<html>Score: " + score + "<br/> Level: " + level + "</html>");
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;

        this.setText("<html>Score: " + score + "<br/> Level: " + level + "</html>");
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        this.setText("<html>Score: " + score + "<br/> Level: " + level + "</html>");

    }
}
