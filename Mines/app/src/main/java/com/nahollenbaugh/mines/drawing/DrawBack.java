package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;
import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

public class DrawBack implements DrawImage{
    protected int color;

    public DrawBack(int color){
        this.color = color;
    }

    float s = 0.1f;
    float t = 0.1f;
    @Override
    public void draw(int width, int height, Canvas c){
        drawLine(s*width,height/2f,width-s*width,(t+s)*height,
                0,0,t*height,-t*height,
                c,color);
        drawLine(s*width,height/2f,width-s*width,height-(t+s)*height,
                0,0,t*height,-t*height,
                c,color);
    }
}
