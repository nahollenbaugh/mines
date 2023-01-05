package com.nahollenbaugh.mines.drawing;

import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

import android.graphics.Canvas;

public class DrawZoom implements DrawImage{
    protected int color;
    protected int backgroundColor;

    public DrawZoom(int color, int backgroundColor){
        this.color = color;
        this.backgroundColor = backgroundColor;
    }

    protected float[] xs = new float[4];
    protected float[] ys = new float[4];
    public void draw(int width, int height, Canvas c){
        fillRectangle(0f,0f,(float)width,(float)height,c,backgroundColor);
        drawCircle(0.35f*width,0.35f*width,0.25f*width,
                0.1f*width,c,color);
        drawLine(0.5267f*width,0.5267f*width,0.9f*width,0.9f*width,
                0.1f*width,c,color);
        fillCircle(0.35f*width,0.35f*width,0.08f*width,c,color);
    }
}
