package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;
import android.graphics.Path;

import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

public class DrawSettings implements DrawImage{
    protected int color;
    protected int backgroundColor;

    protected Path p = new Path();

    public DrawSettings(int color, int backgroundColor){
        this.color = color;
        this.backgroundColor = backgroundColor;
    }
    float ta = 0.2f;
    float r = 0.15f;
    float t = 0.2f;
    float l = 0.1f;
    float q = 0.7853781f;
    @Override
    public void draw(int width, int height, Canvas c) {
        c.clipRect(0,0,width,height);
        c.drawColor(backgroundColor);

        float xCenter = width/2f;
        float yCenter = height/2f;

        drawCircle(xCenter, yCenter, r*width, t*width, c, color);

        float smallSin = ((q-ta)-(q-ta)*(q-ta)*(q-ta)/6f);
        float smallCos = (1f-(q-ta)*(q-ta)*(1f-(q-ta)*(q-ta)/12f)/2);
        float bigSin = ((q+ta)-(q+ta)*(q+ta)*(q+ta)/6f);
        float bigCos = (1f-(q+ta)*(q+ta)*(1f-(q+ta)*(q+ta)/12f)/2);
        float taSin = (ta-ta*ta*ta/6f);
        float taCos = (1f-ta*ta*(1f-ta*ta/12f)/2);
        float smallR=(r+t/2f)*width;
        float bigR=smallR+l*width;
        float[] xSign = {1f,1f,-1f,-1f};
        float[] ySign = {1f,-1f,1f,-1f};
        float[] xs = DrawImageUtil.xs;
        float[] ys = DrawImageUtil.ys;
        for (int i = 0; i < 4; i++){
            xs[0]=xCenter+xSign[i]*bigCos*smallR; xs[1]=xCenter+xSign[i]*bigCos*bigR;
            xs[2]=xCenter+xSign[i]*smallCos*bigR; xs[3]=xCenter+xSign[i]*smallCos*smallR;
            ys[0]=yCenter+ySign[i]*bigSin*smallR; ys[1]=yCenter+ySign[i]*bigSin*bigR;
            ys[2]=yCenter+ySign[i]*smallSin*bigR; ys[3]=yCenter+ySign[i]*smallSin*smallR;
            fill(4,xs,ys,c,color);
        }

        xs[0]=xCenter-taSin*smallR; xs[1]=xCenter+taSin*smallR;
        xs[2]=xCenter+taSin*bigR; xs[3]=xCenter-taSin*bigR;
        ys[0]=yCenter+taCos*smallR; ys[1]=yCenter+taCos*smallR;
        ys[2]=yCenter+taCos*bigR; ys[3]=yCenter+bigR;
        fill(4,xs,ys,c,color);
        ys[0]=yCenter-taCos*smallR; ys[1]=yCenter-taCos*smallR;
        ys[2]=yCenter-taCos*bigR; ys[3]=yCenter-bigR;
        fill(4,xs,ys,c,color);

        ys[0]=yCenter-taSin*smallR; ys[1]=yCenter+taSin*smallR;
        ys[2]=yCenter+taSin*bigR; ys[3]=yCenter-taSin*bigR;
        xs[0]=xCenter+taCos*smallR; xs[1]=xCenter+taCos*smallR;
        xs[2]=xCenter+taCos*bigR; xs[3]=xCenter+bigR;
        fill(4,xs,ys,c,color);
        xs[0]=xCenter-taCos*smallR; xs[1]=xCenter-taCos*smallR;
        xs[2]=xCenter-taCos*bigR; xs[3]=xCenter-bigR;
        fill(4,xs,ys,c,color);

    }
}
