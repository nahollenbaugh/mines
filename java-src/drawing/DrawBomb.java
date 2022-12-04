package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;
import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

public class DrawBomb implements DrawImage {
    protected int bombColor;
    protected int shineColor;

    public DrawBomb(int bombColor, int shineColor){
        this.bombColor = bombColor;
        this.shineColor = shineColor;
    }

    @Override
    public void draw(int width, int height, Canvas c){
        float dim = Math.min(width, height);

        fillCircle(dim/2f,dim/2f,0.25f*dim,
                0,2,c,bombColor);
        drawLine(0.25f*dim,0.25f*dim,0.75f*dim,0.75f*dim,
                0.1f*dim,c,bombColor);
        drawLine(0.25f*dim,0.75f*dim,0.75f*dim,0.25f*dim,
                0.1f*dim,c,bombColor);
        fillRectangle(0.375f*dim,0.375f*dim,0.4375f*dim,0.4375f*dim,
                c,shineColor);

    }
}
