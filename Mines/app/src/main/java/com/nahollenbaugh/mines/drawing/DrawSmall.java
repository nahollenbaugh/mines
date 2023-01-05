package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;

public class DrawSmall implements DrawImage {
    protected DrawImage draw;
    protected float a;
    protected float b;
    public DrawSmall(DrawImage draw, float a, float b){
        this.draw = draw;
        this.a = a;
        this.b = b;
    }

    public void draw(int width, int height, Canvas c){
        c.save();
        c.translate((1-a)/2f*width,(1-b)/2f*height);
        draw.draw((int)(width*a),(int)(height*b),c);
        c.restore();
    }
}
