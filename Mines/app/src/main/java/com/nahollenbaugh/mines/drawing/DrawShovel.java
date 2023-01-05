package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;
import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

public class DrawShovel implements DrawImage {
    protected int color;
    public DrawShovel(int color){
        this.color = color;
    }
    protected static float[] xs = new float[3];
    protected static float[] ys = new float[3];
    @Override
    public void draw(int width, int height, Canvas c) {
        float radius = 0.125f * width;
        float handleCenter = 0.3f * width;
        float thickness = 0.05f * width;
        xs[0] = 0.2f * width; xs[1] = 0.5f * width; xs[2] = 0.3f * width;
        ys[0] = height - 0.2f * width; ys[1] = height - 0.3f * width;
        ys[2] = height - 0.5f * width;
        fill(3,xs,ys,c,color);
        drawLine((xs[1] + xs[2]) / 2, (ys[1] + ys[2]) / 2,
                width - handleCenter - (0.707106f * radius),
                handleCenter + (0.707106f * radius),
                thickness, c, color);
        drawCircle(width - handleCenter, handleCenter, radius-thickness/2f,
                0.75f, 1.75f, thickness, c, color);
        drawLine(width - handleCenter - (0.707106f * radius),
                handleCenter - (0.707106f * radius),
                width - handleCenter + (0.707106f * radius),
                handleCenter + (0.707106f * radius), thickness, c, color);
    }
}
