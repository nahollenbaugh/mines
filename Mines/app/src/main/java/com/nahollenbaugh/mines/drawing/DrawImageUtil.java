package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;
import android.graphics.Path;

public class DrawImageUtil {
    public static float[] xs = new float[6];
    public static float[] ys = new float[6];
    public static Path p = new Path();
    public static void fill(int numPoints, float[] xs, float[] ys,
                               Canvas c, int color){
        setPathTo(p,numPoints,xs,ys);
        c.save();
        c.clipPath(p);
        c.drawColor(color);
        c.restore();
    }

    public static void setPathTo(Path path, int numPoints, float[] xs, float[] ys) {
        path.reset();
        addToPath(path, numPoints, xs, ys);
    }
    public static void addToPath(Path path, int numPoints, float[] xs, float[] ys){
        path.moveTo(xs[0],ys[0]);
        for (int i=1; i<numPoints; i++){
            path.lineTo(xs[i],ys[i]);
        }
        path.lineTo(xs[0],ys[0]);
    }
    public static void setPathToArc(float xCenter, float yCenter, float radius,
                                       float startDirection, float endDirection,
                                       Path p){
        p.reset();
        addArcToPath(xCenter, yCenter, radius, startDirection, endDirection, p);
    }
    public static void addArcToPath(float xCenter, float yCenter, float radius,
                                       float startDirection, float endDirection,
                                       Path p){

        if ((endDirection - startDirection >= 2) || (startDirection - endDirection >= 2)){
            float halfCircle = endDirection > startDirection ? 1 : -1;
            addArcToPath(xCenter, yCenter, radius, startDirection,
                    startDirection+halfCircle,p);
            addArcToPath(xCenter, yCenter, radius, startDirection+halfCircle,
                    endDirection, p);
        } else {
            p.arcTo(xCenter - radius, yCenter - radius,
                    xCenter + radius, yCenter + radius,
                    -180 * startDirection, -180 * (endDirection - startDirection),
                    false);
        }
    }
    public static void addArcToPath(float xCenter, float yCenter, float radius, Path p){
        addArcToPath(xCenter,yCenter,radius,0,2,p);
    }

    public static void fillCircle(float xCenter, float yCenter, float radius,
                                     Canvas c, int color){
        fillCircle(xCenter,yCenter,radius,0,2,c,color);
    }
    public static void fillCircle(float xCenter, float yCenter, float radius,
                                     float startDirection, float endDirection,
                                     Canvas c, int color){
        setPathToArc(xCenter, yCenter, radius, startDirection, endDirection, p);
        c.save();
        c.clipPath(p);
        c.drawColor(color);
        c.restore();
    }
    public static void drawCircle(float xCenter, float yCenter, float radius,
                                     float thickness,
                                     Canvas c, int color){
        drawCircle(xCenter, yCenter, radius, 0, 2, thickness, c, color);
    }
    public static void drawCircle(float xCenter, float yCenter, float radius,
                                     float startDirection, float endDirection,
                                     float thickness,
                                     Canvas c, int color){
        p.reset();
        addArcToPath(xCenter, yCenter, radius+thickness/2, startDirection, endDirection, p);
        addArcToPath(xCenter, yCenter, radius-thickness/2, endDirection, startDirection, p);
        c.save();
        c.clipPath(p);
        c.drawColor(color);
        c.restore();
    }
    public static void drawLineVertical(float x, float ystart, float yend,
                                           float thickness,
                                           Canvas c, int color){
        drawLineVertical(x,ystart,yend,thickness,NONE,c,color);
    }
    public static void drawLineVertical(float x, float ystart, float yend,
                                           float thickness, int direction,
                                           Canvas c, int color){
        float leftEp;
        float rightEp;
        switch(direction){
            case LEFT:
                leftEp = thickness;
                rightEp = 0;
                break;
            case RIGHT:
                leftEp = 0;
                rightEp = thickness;
                break;
            default:
                leftEp = thickness/2;
                rightEp = thickness/2;
        }
        drawLine(x,ystart,x,yend,-leftEp,rightEp,0,0,c,color);
    }
    public static void drawLineHorizontal(float xstart, float xend, float y,
                                             float thickness,
                                             Canvas c, int color){
        drawLineHorizontal(xstart,xend,y,thickness,NONE,c,color);
    }
    public static void drawLineHorizontal(float xstart, float xend, float y,
                                           float thickness, int direction,
                                           Canvas c, int color){
        float upEp;
        float downEp;
        switch(direction){
            case UP:
                upEp = thickness;
                downEp = 0;
                break;
            case DOWN:
                upEp = 0;
                downEp = thickness;
                break;
            case NONE:
                upEp = thickness/2;
                downEp = thickness/2;
                break;
            default:
                throw new RuntimeException();
        }
        drawLine(xstart,y,xend,y,0,0,-upEp,downEp,c,color);
    }
    public static final int NONE = 0;
    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int RIGHT = 3;
    public static final int LEFT = 4;
    public static void drawLine(float xstart, float ystart, float xend, float yend,
                                   float startToEndXOffset, float endToStartXOffset,
                                   float startToEndYOffset, float endToStartYOffset,
                                   Canvas c, int color){
        p.reset();
        p.moveTo(xstart+startToEndXOffset,ystart+startToEndYOffset);
        p.lineTo(xend+startToEndXOffset,yend+startToEndYOffset);
        p.lineTo(xend+endToStartXOffset,yend+endToStartYOffset);
        p.lineTo(xstart+endToStartXOffset,ystart+endToStartYOffset);
        c.save();
        c.clipPath(p);
        c.drawColor(color);
        c.restore();

    }

    public static void drawLine(float xstart, float ystart, float xend, float yend,
                                   float thickness, Canvas c, int color){
        float dx;
        float dy;
        if (yend == ystart){
            dx=0;
            dy=thickness/2f;
        } else {
            float m = (xend - xstart) / (yend - ystart);
            dy = thickness / 2f / (float) Math.sqrt(1 + m * m);
            dx = -m * dy;
        }
        drawLine(xstart,ystart,xend,yend,
                dx,-dx,dy,-dy,
                c,color);
    }
    public static void fillRectangle(float xstart, float ystart, float xend, float yend,
                                     Canvas c, int color){
        xs[0]=xstart;
        xs[1]=xend;
        xs[2]=xend;
        xs[3]=xstart;
        ys[0]=ystart;
        ys[1]=ystart;
        ys[2]=yend;
        ys[3]=yend;
        fill(4,xs,ys,c,color);

    }
}
