package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;

import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

public class DrawMinus implements DrawImage{
    protected int color;
    protected float t = 0.15f;
    protected float s = 0.1f;

    public DrawMinus(int color){
        this.color = color;
    }

    @Override
    public void draw(int width, int height, Canvas c){
        fillRectangle(s*width,(0.5f-t)*height,(1-s)*width,(0.5f+t)*height,
                c,color);
    }
}
