package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;
import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

public class DrawResetFace implements DrawImage{

    protected int headColor;
    protected int faceColor;
    protected int backgroundColor;
    public int state = HAPPY;

    public DrawResetFace(int headColor, int faceColor, int backgroundColor){
        this.headColor = headColor;
        this.faceColor = faceColor;
        this.backgroundColor = backgroundColor;
    }

    // stuff that should be in drawing methods but gets allocated here so
    // it doesn't have to be allocated in drawing methods.  The internet
    // says so.
    public void draw(int width, int height, Canvas c){
        int dim = Math.min(width,height);
        float thickness = 0.03125f*dim;
        float[] xs = DrawImageUtil.xs;
        float[] ys = DrawImageUtil.ys;

        // background
        xs[0]=0;
        xs[1]=width;
        xs[2]=width;
        xs[3]=0;
        ys[0]=0;
        ys[1]=0;
        ys[2]=height;
        ys[3]=height;
        fill(4,xs,ys,c, backgroundColor);

        // head circle
        fillCircle(width/2f,height/2f,0.375f*dim,
                c,headColor);

        drawCircle(width/2f,height/2f,0.375f*dim,
                thickness, c, faceColor);

        // left eye
        if (state == HAPPY || state == SURPRISED) {
            fillCircle(width / 2f - 0.125f * dim, 0.375f * dim, 0.0625f * dim,
                    c, faceColor);
        } else if (state == DEAD){
            drawLine(width/2f - 0.1875f*dim, 0.3125f*dim,
                    width/2f - 0.0625f*dim, 0.4375f*dim,
                    thickness,
                    c, faceColor);
            drawLine(width/2f - 0.1875f*dim, 0.4375f*dim,
                    width/2f - 0.0625f*dim, 0.3125f*dim,
                    thickness,
                    c, faceColor);
        } else if (state == COOL) {
            fillCircle(width / 2f - 0.125f * dim, 0.3125f * dim, 0.125f * dim,
                    1f, 2f,
                    c, faceColor);
        }

        // right eye
        if (state == HAPPY || state == SURPRISED) {
            fillCircle(width / 2f + 0.125f * dim, 0.375f * dim, 0.0625f * dim,
                    c, faceColor);
        } else if (state == DEAD){
            drawLine(width/2f + 0.1875f*dim, 0.3125f*dim,
                    width/2f + 0.0625f*dim, 0.4375f*dim,
                    thickness,
                    c, faceColor);
            drawLine(width/2f + 0.1875f*dim, 0.4375f*dim,
                    width/2f + 0.0625f*dim, 0.3125f*dim,
                    thickness, c, faceColor);
        } else if (state == COOL) {
            fillCircle(width / 2f + 0.125f * dim, 0.3125f * dim, 0.125f * dim,
                    1f, 2f,
                    c, faceColor);
        }

        // sunglasses arms
        if (state == COOL){
            drawLine(width/2f-0.25f*dim, 0.3125f*dim,
                    width/2f-0.375f*dim, height/2f,
                    thickness,
                    c, faceColor);
            drawLine(width/2f+0.25f*dim, 0.3125f*dim,
                    width/2f+0.375f*dim, height/2f,
                    thickness,
                    c, faceColor);
        }


        // mouth
        if (state == HAPPY || state == COOL) {
            drawCircle(width / 2f, height / 2f - 0.1875f * dim, 0.375f * dim,
                    1.375f, 1.625f, thickness,
                    c, faceColor);
        } else if (state == DEAD){
            drawLine(width/2f - 0.1875f*dim, height/2f + 0.125f*dim,
                    width/2f + 0.1875f*dim, height/2f + 0.125f*dim,
                    thickness,
                    c, faceColor);
        } else if (state == SURPRISED) {
            drawCircle(width / 2f, height/2f + 0.125f*dim, 0.1f * dim,
                    0, 2, thickness,
                    c, faceColor);
        }
    }

    public static final short HAPPY = 0;
    public static final short DEAD = 1;
    public static final short COOL = 2;

    public static final short SURPRISED = 3;

}
