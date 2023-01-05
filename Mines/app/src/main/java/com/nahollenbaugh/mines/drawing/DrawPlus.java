package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;
import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

public class DrawPlus extends DrawMinus {
    public DrawPlus(int color){
        super(color);
    }

    public void draw(int width, int height, Canvas c){
        super.draw(width,height,c);
        fillRectangle((0.5f-t)*width,s*height,(0.5f+t)*width,(1-s)*height,
                c,color);
    }
}
