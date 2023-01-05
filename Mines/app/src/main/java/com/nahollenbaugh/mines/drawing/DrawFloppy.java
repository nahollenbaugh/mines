package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;
import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

public class DrawFloppy implements DrawImage{
    protected int color;

    public DrawFloppy(int color){
        this.color = color;
    }


    public void draw(int width, int height, Canvas canvas){
        float s = 0.15f*width;
        float t = 0.15f*height;
        float a = 0.25f*height;
        float b = 0.15f*height;
        float c = 0.2f*width;
        float d = 0.05f*width;
        float e = 0.2f*width;
        float f = 0.3f*width;
        float g = 0.15f*width;
        float h = (a-b)/2;
        float ta = 0.06f*width;
        drawLineHorizontal(s,width-s-c,t,ta,canvas,color);
        drawLine(width-s-c,t,width-s,t+c,ta,canvas,color);
        drawLineVertical(width-s,t+c,height-t,ta,canvas,color);
        drawLineHorizontal(width-s,s,height-t,ta,canvas,color);
        drawLineVertical(s,height-t,t,ta,canvas,color);

        // thickness \geq 0.5 makes this not have a hole but whatever
        drawLineVertical(s+e,t,t+a,ta,canvas,color);
        drawLineHorizontal(s+e,s+e+f,t+a,ta,canvas,color);
        drawLineVertical(s+e+f,t+a,t,ta,canvas,color);

        drawLineVertical(s+e+g,t+h,t+h+b,ta,canvas,color);
        drawLineHorizontal(s+e+g,s+e+g+d,t+h+b,ta,canvas,color);
        drawLineVertical(s+e+g+d,t+h+b,t+h,ta,canvas,color);
        drawLineHorizontal(s+e+g+d,s+e+g,t+h,ta,canvas,color);
    }
}
