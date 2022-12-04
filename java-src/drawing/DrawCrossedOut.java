package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;
import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

public class DrawCrossedOut implements DrawImage{
    protected DrawImage draw;
    protected int color;

    public DrawCrossedOut(DrawImage draw, int color){
        this.draw = draw;
        this.color = color;
    }
    public void draw(int width, int height, Canvas c){
        int dim = width < height ? width : height;
        float thickness = 0.1f*dim;

        draw.draw(width,height,c);
        c.save();
        c.clipRect(0,0,width,height);
        drawLine(0,0,width,height,thickness,c,color);
        drawLine(0,height,width,0,thickness,c,color);
        c.restore();
    }
}
