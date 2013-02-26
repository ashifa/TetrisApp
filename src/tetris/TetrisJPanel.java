/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author 305020571
 */
public class TetrisJPanel extends JPanel implements KeyListener {

    private CopyOnWriteArrayList<MyPoint> bottomPts = new CopyOnWriteArrayList<MyPoint>();
    private Drops fallingShape = new Drops();
    private Drops NextFalling = new Drops();
    private final static int TR_WIDTH = 20;
    private ScoreWindow scoreWin = new ScoreWindow();
    private ExecutorService es = Executors.newSingleThreadExecutor();
    private boolean isStart = true;
    private Image bkImage = new ImageIcon("game_bg.png").getImage();

    public TetrisJPanel() {
        this.setPreferredSize(new Dimension(480, 854));
        //       this.setLayout(null);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.add(scoreWin);
        this.scoreWin.setLocation(400, 100);


        for (int i =0; i<9; i++){
            this.bottomPts.add(new MyPoint(i,17));
        }

        es.submit(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        int sleepTime = 500 - scoreWin.getLevel() * 20;
                        Thread.sleep(sleepTime > 100 ? sleepTime : 100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TetrisJPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (!isStart) {
                        continue;
                    }
                    fallDown();
                    repaint();
                }
            }
        });

    }

    private void drawBlocks(final Graphics g) {

        for (Point pt : fallingShape.getPts()) {
            this.drawSinggleBlock(g, pt, fallingShape.color);
        }
        for (MyPoint pt : this.bottomPts) {
            this.drawSinggleBlock(g, pt, pt.color);
        }
        g.translate(-50, -90);
        for (Point pt : this.NextFalling.getPts()) {
            this.drawSinggleBlock(g, pt, NextFalling.color);
        }
        g.translate(50, 90);


    }

    private void drawSinggleBlock(final Graphics g, Point pt, Color color) {
        g.setColor(color.darker());


        g.fill3DRect(pt.x * TR_WIDTH, pt.y * TR_WIDTH, TR_WIDTH, TR_WIDTH, true);


        g.setColor(color);
        int width = 3;
        g.fillRoundRect(pt.x * TR_WIDTH + width, pt.y * TR_WIDTH + width, TR_WIDTH - width * 2, TR_WIDTH - width * 2, 4, 4);

        g.setColor(Color.WHITE);
//         g.fillRoundRect(pt.x * TR_WIDTH, pt.y * TR_WIDTH, TR_WIDTH, TR_WIDTH,30,30); 
//       g.drawOval(pt.x * TR_WIDTH, pt.y * TR_WIDTH, TR_WIDTH,TR_WIDTH );
        g.fillOval(pt.x * TR_WIDTH, pt.y * TR_WIDTH, 4, 4);

    }

    private void generateDrops() {
        this.fallingShape = this.NextFalling;
        this.NextFalling = new Drops();
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        g.drawImage(bkImage, 0, 0, this.getWidth(), this.getHeight(), this);


        g.translate(50, 120);
        this.drawBlocks(g);

        //draw net lines
        g.setColor(Color.DARK_GRAY);
        for (int x = 0; x < 11; x++) {
            g.drawLine(x * TR_WIDTH, 0, x * TR_WIDTH, TR_WIDTH * 18);
        }
        for (int y = 0; y < 19; y++) {
            g.drawLine(0, y * TR_WIDTH, TR_WIDTH * 10, y * TR_WIDTH);
        }
        g.translate(-50, -120);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN:
                this.fallDown();
                break;
            case KeyEvent.VK_LEFT:
                if (this.isInRange(-1) && !this.isCollision(-1, 0, false)) {
                    this.fallingShape.translate(-1, 0);
                    AudioPlayer.MOVE.play();
                }
                break;
            case KeyEvent.VK_UP:
                if (!this.isCollision(0, 0, true)) {
                    this.fallingShape.rotate();
                    AudioPlayer.ROTATE.play();
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (this.isInRange(1) && !this.isCollision(1, 0, false)) {
                    this.fallingShape.translate(1, 0);
                    AudioPlayer.MOVE.play();
                }
                break;
            case KeyEvent.VK_ENTER:
                System.out.println("enter");
                this.isStart = !isStart;
                break;
            default:
                ;
        }
        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private boolean isInRange(int i) {
        for (Point pt : this.fallingShape.getPts()) {
            if (pt.x + i < 0 || pt.x + i > 9) {
                return false;
            }
        }

        return true;
    }

    private boolean isCollision(int x, int y, boolean isRotate) {

        Drops clone = (Drops) this.fallingShape.clone();
        clone.translate(x, y);
        if (isRotate) {
            clone.rotate();
        }
        //hit the bottom line
        for (Point pt : clone.getPts()) {
            if (pt.y == 18) {
                return true;
            }
        }
        //collision with bottom points
        for (Point pt : clone.getPts()) {
            for (Point bottomPt : this.bottomPts) {
                if (pt.equals(bottomPt)) {
                    //                 System.out.println(pt + " bottomPt " + bottomPt);
                    return true;
                }
            }
        }
        //collision with side edges
        for (Point pt : clone.getPts()) {
            if (pt.x < 0 || pt.x > 9) {
                return true;
            }
        }
        return false;
    }

    private synchronized void fallDown() {
        if (!this.isCollision(0, 1, false)) {
            this.fallingShape.translate(0, 1);
        } else {
            this.mergeToBottom();
        }
    }

    private void mergeToBottom() {
        //       this.bottomPts.addAll(Arrays.asList(this.fallingShape.getPts()));
        for (MyPoint pt : this.fallingShape.getPts()) {
            if (pt.y == 1) {
                this.bottomPts.clear();
                this.isStart = false;
                return;
            }
            pt.color = this.fallingShape.color;
            this.bottomPts.add(pt);
        }
        AudioPlayer.FALLDOWN.play();
        //check if get scores
        checkScore();
        this.generateDrops();

    }

    private void checkScore() {
        int[] counter = new int[18];

        for (Point pt : this.bottomPts) {
            counter[pt.y]++;
        }
        int removedLines = 0;
        for (int y = 0; y < 18; y++) {
            if (counter[y] == 10) {
                removedLines++;
                // remove this line
                for (int x = 0; x < 10; x++) {
                    this.bottomPts.remove(new MyPoint(x, y));

                }

                //move one line down
                for (Point pt : this.bottomPts) {
                    if (pt.y < y) {
                        pt.translate(0, 1);
                    }
                }
            }
        }

        if (removedLines == 1) {
            AudioPlayer.CLEAR.play();
            //get Score
            this.scoreWin.setScore(this.scoreWin.getScore() + 100);
        } else if (removedLines == 2) {
            AudioPlayer.DOUBLEKILL.play();
            //get Score
            this.scoreWin.setScore(this.scoreWin.getScore() + 100 * 3);
        } else if (removedLines == 3) {
            AudioPlayer.TRIPLEKILL.play();
            //get Score
            this.scoreWin.setScore(this.scoreWin.getScore() + 100 * 5);
        } else if (removedLines == 4) {
            AudioPlayer.GODLIKE.play();
            //get Score
            this.scoreWin.setScore(this.scoreWin.getScore() + 100 * 8);
        }else{//0
        }
        //set level
        this.scoreWin.setLevel(this.scoreWin.getScore() / 400);
    }
}
