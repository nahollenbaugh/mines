package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;
import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

public class DrawWithBackground implements DrawImage {
    protected int color;
    protected DrawImage draw;

    public DrawWithBackground(int color, DrawImage draw){
        this.color = color;
        this.draw = draw;
    }

    public void draw(int width, int height, Canvas c){
        fillRectangle(0,0,width,height,c,color);
        draw.draw(width,height,c);
    }
}
