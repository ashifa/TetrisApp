/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author 305020571
 */
public class Drops implements Cloneable {

    private MyPoint[][] pts;
    //XXXX
    private final static MyPoint[][] a = {{new MyPoint(0, 0), new MyPoint(1, 0), new MyPoint(2, 0), new MyPoint(3, 0)},
        {new MyPoint(1, 1), new MyPoint(1, 0), new MyPoint(1, -1), new MyPoint(1, -2)}
    };
    //XX
    //XX
    final private static MyPoint[][] b = {{new MyPoint(0, 0), new MyPoint(1, 0), new MyPoint(0, 1), new MyPoint(1, 1)}};
    // X
    //XXX
    private final static MyPoint[][] c = {{new MyPoint(1, 0), new MyPoint(0, 1), new MyPoint(1, 1), new MyPoint(2, 1)},
        {new MyPoint(0, 0), new MyPoint(0, 1), new MyPoint(1, 1), new MyPoint(0, 2)},
        {new MyPoint(0, 0), new MyPoint(1, 0), new MyPoint(2, 0), new MyPoint(1, 1)},
        {new MyPoint(1, 0), new MyPoint(0, 1), new MyPoint(1, 1), new MyPoint(1, 2)},};
    //XX
    // XX
    private final static MyPoint[][] d = {{new MyPoint(0, 0), new MyPoint(0, 1), new MyPoint(1, 1), new MyPoint(1, 2)},
        {new MyPoint(1, 0), new MyPoint(2, 0), new MyPoint(0, 1), new MyPoint(1, 1)},};
    // XX
    //XX
    private final static MyPoint[][] e = {{new MyPoint(1, 0), new MyPoint(0, 1), new MyPoint(1, 1), new MyPoint(0, 2)},
        {new MyPoint(1, 0), new MyPoint(0, 0), new MyPoint(2, 1), new MyPoint(1, 1)},};
    //XXX
    //  X
    private final static MyPoint[][] f = {{new MyPoint(0, 0), new MyPoint(1, 0), new MyPoint(2, 0), new MyPoint(2, 1)},
        {new MyPoint(1, 0), new MyPoint(1, 1), new MyPoint(0, 2), new MyPoint(1, 2)},
        {new MyPoint(0, 0), new MyPoint(0, 1), new MyPoint(1, 1), new MyPoint(2, 1)},
        {new MyPoint(0, 0), new MyPoint(1, 0), new MyPoint(0, 1), new MyPoint(0, 2)},};
    //XXX
    //X  
    private final static MyPoint[][] g = {{new MyPoint(0, 0), new MyPoint(1, 0), new MyPoint(2, 0), new MyPoint(0, 1)},
        {new MyPoint(0, 0), new MyPoint(1, 0), new MyPoint(1, 1), new MyPoint(1, 2)},
        {new MyPoint(2, 0), new MyPoint(0, 1), new MyPoint(1, 1), new MyPoint(2, 1)},
        {new MyPoint(0, 0), new MyPoint(0, 1), new MyPoint(0, 2), new MyPoint(1, 2)},};
    static final private Random seed = new Random();
    private int x = 3;
    private int y;
    private int angle;
    Color color;
    private static Color[] candidateColor = {Color.CYAN, Color.LIGHT_GRAY, Color.PINK, Color.GREEN, Color.RED, Color.ORANGE, Color.MAGENTA, Color.BLUE, Color.YELLOW};
    private static MyPoint[][][] candidatePts = {a,b,c,d,e,f,g};
    public Drops() {

        //assign shape
        this.pts = candidatePts[seed.nextInt(7)];
        //assign angle
        this.angle = seed.nextInt(4) % this.pts.length;
        //assign color
        color = candidateColor[seed.nextInt(9)];
        //color = new Color(seed.nextInt(256),seed.nextInt(256),seed.nextInt(256));


    }

    public void translate(int x, int y) {
        this.x = this.x + x;
        this.y = this.y + y;

    }

    public void rotate() {
        this.angle += 1;
        this.angle %= this.pts.length;

    }

    public MyPoint[] getPts() {
        MyPoint[] tmp = {(MyPoint) pts[angle][0].clone(), (MyPoint) pts[angle][1].clone(), (MyPoint) pts[angle][2].clone(), (MyPoint) pts[angle][3].clone()};

        for (MyPoint pt : tmp) {
            pt.translate(x, y);
        }

        return tmp;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
}
