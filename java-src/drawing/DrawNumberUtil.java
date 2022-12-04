package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;

import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

public class DrawNumberUtil {
    public static final float PREFERRED_ASPECT_RATIO = 0.6f;
    public static final float PREFERRED_SPACING = 0.12f;
    public static final float PREFERRED_SEPARATION = 0.02f;
    public static final float PREFERRED_THICKNESS = 0.4f;
    public static final float PREFERRED_BORDER_THICKNESS = 0.1f;

    protected float thickness;
    protected float borderThickness;
    protected float s;

    public int color;
    public int borderColor;

    protected float lastWidth = 0;
    protected float a;
    protected float b;
    protected float al;
    protected float be;
    protected float ga;
    protected float de;
    protected float ep;
    protected float et;
    protected float bs;
    protected float als;
    protected float bes;
    protected float gas;
    protected float des;
    protected float eps;
    protected float ets;


    public DrawNumberUtil(int numberColor, int borderColor, float thickness,
                          float borderThickness, float separation){
        this.color = numberColor;
        this.thickness = thickness;
        this.borderColor = borderColor;
        this.borderThickness = borderThickness;
        s=separation;
    }

    public float getThickness(){
        return thickness;
    }

    public void drawNumber(float x, float y, float width, float height,
                           int number, Canvas c){
        boolean top = true;
        boolean topLeft = true;
        boolean topRight = true;
        boolean middle = true;
        boolean bottomLeft = true;
        boolean bottomRight = true;
        boolean bottom = true;
        switch(number) {
            case 0:
                middle = false;
                break;
            case 1:
                top = false; topLeft = false; middle = false; bottomLeft = false; bottom = false;
                break;
            case 2:
                topLeft = false; bottomRight = false;
                break;
            case 3:
                topLeft = false; bottomLeft = false;
                break;
            case 4:
                top = false; bottomLeft = false; bottom = false;
                break;
            case 5:
                bottomLeft = false; topRight = false;
                break;
            case 6:
                topRight = false;
                break;
            case 7:
                topLeft = false; middle = false; bottomLeft = false; bottom = false;
                break;
            case 8:
                break;
            case 9:
                bottomLeft = false; bottom = false;
                break;
        }
        if (top) strokeTop(x,y,width,height,c);
        if (topLeft) strokeTopLeft(x,y,width,height,c);
        if (topRight) strokeTopRight(x,y,width,height,c);
        if (middle) strokeMiddle(x,y,width,height,c);
        if (bottomLeft) strokeBottomLeft(x,y,width,height,c);
        if (bottomRight) strokeBottomRight(x,y,width,height,c);
        if (bottom) strokeBottom(x,y,width,height,c);
    }

    public void strokeTop(float x, float y, float width, float height, Canvas c){
        float[] xs = DrawImageUtil.xs;
        float[] ys = DrawImageUtil.ys;
        if (width != lastWidth) {
            setConstants(width);
        }
        xs[0]=x+eps; xs[1]=x+width-eps; xs[2]=x+width-a-ets; xs[3]=x+a+ets;
        ys[0]=y+bs; ys[1]=y+bs; ys[2]=y+a-bs;ys[3]=y+a-bs;
        fill(4,xs,ys,c,borderColor);
        xs[0]=xs[0]+ep; xs[1]=xs[1]-ep; xs[2]=xs[2]-et; xs[3]=xs[3]+et;
        ys[0]=ys[0]+b; ys[1]=ys[1]+b; ys[2]=ys[2]-b; ys[3]=ys[3]-b;
        fill(4,xs,ys,c,color);
    }
    public void strokeTopLeft(float x, float y, float width, float height, Canvas c){
        float[] xs = DrawImageUtil.xs;
        float[] ys = DrawImageUtil.ys;
        if (width != lastWidth) {
            setConstants(width);
        }
        xs[0]=x+bs; xs[1]=x+a-bs; xs[2]=x+a-bs; xs[3]=x+bs;
        ys[0]=y+eps; ys[1]=y+a+ets; ys[2]=y+(height-a)/2f-des; ys[3]=y+height/2f-gas;
        fill(4,xs,ys,c,borderColor);
        xs[0]=xs[0]+b; xs[1]=xs[1]-b; xs[2]=xs[2]-b; xs[3]=xs[3]+b;
        ys[0]=ys[0]+ep; ys[1]=ys[1]+et; ys[2]=ys[2]-de; ys[3]=ys[3]-ga;
        fill(4,xs,ys,c,color);
    }
    public void strokeTopRight(float x, float y, float width, float height, Canvas c){
        float[] xs = DrawImageUtil.xs;
        float[] ys = DrawImageUtil.ys;
        if (width != lastWidth) {
            setConstants(width);
        }
        xs[0]=x+width-bs; xs[1]=x+width-bs; xs[2]=x+width-a+bs; xs[3]=x+width-a+bs;
        ys[0]=y+eps; ys[1]=y+height/2f-gas; ys[2]=y+(height-a)/2f-des; ys[3]=y+a+ets;
        fill(4,xs,ys,c,borderColor);
        xs[0]=xs[0]-b; xs[1]=xs[1]-b; xs[2]=xs[2]+b; xs[3]=xs[3]+b;
        ys[0]=ys[0]+ep; ys[1]=ys[1]-ga; ys[2]=ys[2]-de; ys[3]=ys[3]+et;
        fill(4,xs,ys,c,color);
    }
    public void strokeMiddle(float x, float y, float width, float height, Canvas c){
        float[] xs = DrawImageUtil.xs;
        float[] ys = DrawImageUtil.ys;
        if (width != lastWidth) {
            setConstants(width);
        }
        xs[0]=x+als; xs[1]=x+a+bes; xs[2]=x+width-a-bes; xs[3]=x+width-als;
        xs[4]=x+width-a-bes; xs[5]=x+a+bes;
        ys[0]=y+height/2f; ys[1]=y+(height-a)/2f+bs; ys[2]=y+(height-a)/2f+bs; ys[3]=y+height/2f;
        ys[4]=y+(height+a)/2f-bs; ys[5]=y+(height+a)/2f-bs;
        fill(6,xs,ys,c,borderColor);
        xs[0]=x+al; xs[1]=xs[1]+be; xs[2]=xs[2]-be; xs[3]=xs[3]-al;
        xs[4]=xs[4]-be; xs[5]=xs[5]+be;
        ys[1]=ys[1]+b; ys[2]=ys[2]+b; ys[4]=ys[4]-b; ys[5]=ys[5]-b;
        fill(6,xs,ys,c,color);

    }
    public void strokeBottomLeft(float x, float y, float width, float height, Canvas c){
        float[] xs = DrawImageUtil.xs;
        float[] ys = DrawImageUtil.ys;
        if (width != lastWidth) {
            setConstants(width);
        }
        xs[0]=x+bs; xs[1]=x+a-bs; xs[2]=x+a-bs; xs[3]=x+bs;
        ys[0]=y+height/2f+gas; ys[1]=y+(height+a)/2f+des; ys[2]=y+height-a+ets;
        ys[3]=y+height-eps;
        fill(4,xs,ys,c,borderColor);
        xs[0]=xs[0]+b; xs[1]=xs[1]-b; xs[2]=xs[2]-b; xs[3]=xs[3]+b;
        ys[0]=ys[0]+ga; ys[1]=ys[1]+de; ys[2]=ys[2]-et; ys[3]=ys[3]-ep;
        fill(4,xs,ys,c,color);
    }
    public void strokeBottomRight(float x, float y, float width, float height, Canvas c){
        float[] xs = DrawImageUtil.xs;
        float[] ys = DrawImageUtil.ys;
        if (width != lastWidth) {
            setConstants(width);
        }
        xs[0]=x+width-bs; xs[1]=x+width-bs; xs[2]=x+width-a+bs; xs[3]=x+width-a+bs;
        ys[0]=y+height/2f+gas; ys[1]=y+height-eps; ys[2]=y+height-a-ets;
        ys[3]=y+(height+a)/2f+des;
        fill(4,xs,ys,c,borderColor);
        xs[0]=xs[0]-b; xs[1]=xs[1]-b; xs[2]=xs[2]+b; xs[3]=xs[3]+b;
        ys[0]=ys[0]+ga; ys[1]=ys[1]-ep; ys[2]=ys[2]-et; ys[3]=ys[3]+de;
        fill(4,xs,ys,c,color);
    }
    public void strokeBottom(float x, float y, float width, float height, Canvas c){
        float[] xs = DrawImageUtil.xs;
        float[] ys = DrawImageUtil.ys;
        if (width != lastWidth) {
            setConstants(width);
        }
        xs[0]=x+eps; xs[1]=x+a+des; xs[2]=x+width-a-des; xs[3]=x+width-eps;
        ys[0]=y+height-bs; ys[1]=y+height-a+bs; ys[2]=y+height-a+bs; ys[3]=y+height-bs;
        fill(4,xs,ys,c, borderColor);
        xs[0]=xs[0]+ep; xs[1]=xs[1]+de; xs[2]=xs[2]-de; xs[3]=x+width-ep;
        ys[0]=ys[0]-b; ys[1]=ys[1]+b; ys[2]=ys[2]+b; ys[3]=ys[3]-b;
        fill(4,xs,ys,c,color);
    }

    protected void setConstants(float width){
        lastWidth = width;
        a = width * thickness;
        b = width * borderThickness;
        ep = b * 2.4142135f; // \sqrt2+1
        et = b * 0.4142135f; // \sqrt2-1
        al = b * 2.2360679f; // \sqrt5
        be = b * 0.2360679f; // \sqrt5-2;
        ga = b * 1.6180339f; // \tfrac12(\sqrt5+1)
        de = b * 0.6180339f; // \tfrac12(\sqrt5-1)
        bs = width * s;
        eps = bs * 2.4142135f; // \sqrt2+1
        ets = bs * 0.4142135f; // \sqrt2-1
        als = bs * 2.2360679f; // \sqrt5
        bes = bs * 0.2360679f; // \sqrt5-2;
        gas = bs * 1.6180339f; // \tfrac12(\sqrt5+1)
        des = bs * 0.6180339f; // \tfrac12(\sqrt5-1)
    }

    public void strokeRectangle(float dim, float xstart, float ystart,
                                float xend, float yend, Canvas c){
        fillRectangle(xstart, ystart, xend, yend, c, borderColor);
        fillRectangle(xstart+borderThickness*dim, ystart+borderThickness*dim,
                xend-borderThickness*dim, yend-borderThickness*dim,
                c,color);
    }
}
