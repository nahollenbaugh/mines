package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;
import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

public class DrawQuestionMark implements DrawImage{
    protected int color;
    protected int backgroundColor;
    protected boolean hasBackground;

    public DrawQuestionMark(int color){
        this.color = color;
    }
    public DrawQuestionMark(int color, int backgroundColor){
        this.color = color;
        this.backgroundColor = backgroundColor;
        this.hasBackground = true;
    }

    float a=0.15f;
    float s=1.414213f;
    float b=0.05f;

    float h=0.2f;

    public void draw(int width, int height, Canvas c){
        if (hasBackground){
            fillRectangle(0,0,width, height, c, backgroundColor);
        }
        float thickness = 0.12f*width;
        drawCircle(0.5f*width, (h+a)*width, a*width,
                1f,-0.25f,thickness,c,color);
        drawLine((0.5f+a/s)*width,(h+a*(1+1/s))*width,(0.5f+b)*width,
                (h+a*(1+s)-b)*width, thickness,c,color);
        drawCircle((0.5f+b*s/(s-1))*width,(h+a*(1+s)+b*(2-s)/(s-1))*width,b*s/(s-1)*width,
                0.75f,1,thickness,c,color);
        drawLineVertical(0.5f*width,(h+a*(1+s)+b*(2-s)/(s-1))*width,0.7f*height,
                thickness,c,color);
        drawLineVertical(0.5f*width,0.75f*height,0.85f*height,thickness,c,color);
    }

}
