package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;
import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

public class DrawFlag implements DrawImage {
    int groundColor;
    int flagColor;
    int flagpoleColor;
    public DrawFlag(int groundColor,
                    int flagpoleColor,
                    int flagColor){
        this.groundColor = groundColor;
        this.flagpoleColor = flagpoleColor;
        this.flagColor = flagColor;
    }

    public void draw(int width, int height, Canvas c){
        // ground
        ys[0]=height;
        ys[1]=0.66f*height;
        ys[2]=0.66f*height;
        ys[3]=height;
        xs[0]=0;
        xs[1]=0.46875f*width;
        xs[2]=0.53125f*width;
        xs[3]=width;
        fill(4,xs,ys,c,groundColor);

        //flagpole
        fillRectangle(0.46875f*width,0.125f*height,0.53125f*width,
                0.66f*height,c,flagpoleColor);

        //flag
        xs[0]=0.125f*width;
        xs[1]=0.46875f*width;
        xs[2]=0.46875f*width;
        ys[0]=0.5f*height;
        ys[1]=0.125f*height;
        ys[2]=0.5f*height;
        fill(3,xs,ys,c,flagColor);

    }
}
