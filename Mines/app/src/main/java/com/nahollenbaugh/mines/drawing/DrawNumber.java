package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;

public class DrawNumber implements DrawImage{
    protected int number;
    protected DrawNumberUtil drawNumberUtil;
    protected float aspectRatio = DrawNumberUtil.PREFERRED_ASPECT_RATIO;
    public DrawNumber(int number, int color, int borderColor){
        this.number = number;
        this.drawNumberUtil = new DrawNumberUtil(color, borderColor,
                DrawNumberUtil.PREFERRED_THICKNESS, DrawNumberUtil.PREFERRED_BORDER_THICKNESS,
                0);
    }
    public void draw(int width, int height, Canvas c){
        if (width <= height * aspectRatio){
            drawNumberUtil.drawNumber(0, (height - width / aspectRatio) / 2, width,
                    width / aspectRatio, number, c);
        } else {
            drawNumberUtil.drawNumber((width - height * aspectRatio) / 2, 0,
                    height * aspectRatio, height, number, c);
        }
    }
}
