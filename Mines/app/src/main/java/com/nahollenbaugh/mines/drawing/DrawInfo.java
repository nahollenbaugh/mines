package com.nahollenbaugh.mines.drawing;

import android.graphics.Canvas;
import android.util.Log;

import static com.nahollenbaugh.mines.drawing.DrawImageUtil.*;

public class DrawInfo implements DrawImage{
    public static final int PREFERRED_ASPECT_RATIO = 2;

    protected int color;
    public DrawInfo(int color){
        this.color = color;
    }

    @Override
    public void draw(int width, int height, Canvas c){
        float[] xs = new float[12];
        float[] ys = new float[12];
        float sep = width * 0.03f;
        float letterWidth;
        float letterStart = 0;
        float thickness = width * 0.07f;
        float letterHeight = height * 0.7f;
        float heightStart = (height - letterHeight) / 2;

        // I
        letterWidth = width * 0.16f;
        xs[0]  = 0;                             ys[0]  = 0;
        xs[1]  = letterWidth;                   ys[1]  = 0;
        xs[2]  = letterWidth;                   ys[2]  = thickness;
        xs[3]  = (letterWidth + thickness) / 2; ys[3]  = thickness;
        xs[4]  = (letterWidth + thickness) / 2; ys[4]  = letterHeight - thickness;
        xs[5]  = letterWidth;                   ys[5]  = letterHeight - thickness;
        xs[6]  = letterWidth;                   ys[6]  = letterHeight;
        xs[7]  = 0;                             ys[7]  = letterHeight;
        xs[8]  = 0;                             ys[8]  = letterHeight - thickness;
        xs[9]  = (letterWidth - thickness) / 2; ys[9]  = letterHeight - thickness;
        xs[10] = (letterWidth - thickness) / 2; ys[10] = thickness;
        xs[11] = 0;                             ys[11] = thickness;
        for (int i = 0; i < xs.length; i++){
            xs[i] = xs[i] + letterStart;
            ys[i] = ys[i] + heightStart;
        }
        fill(12, xs, ys, c, color);
        letterStart = letterStart + letterWidth + sep;

        // N
        letterWidth = width * 0.27f;
        float g = 0.86f;
        float s = letterHeight - thickness * g;
        float d = letterWidth - 2 * thickness - thickness * (g - (float)Math.sqrt(1 - g * g));
        xs[0] = 0;                                ys[0] = 0;
        xs[1] = letterWidth - thickness - d;      ys[1] = 0;
        xs[2] = letterWidth - thickness;          ys[2] = s;
        xs[3] = letterWidth - thickness;          ys[3] = 0;
        xs[4] = letterWidth;                      ys[4] = 0;
        xs[5] = letterWidth;                      ys[5] = letterHeight;
        xs[6] = thickness + d;                    ys[6] = letterHeight;
        xs[7] = thickness;                        ys[7] = letterHeight - s;
        xs[8] = thickness;                        ys[8] = letterHeight;
        xs[9] = 0;                                ys[9] = letterHeight;
        for (int i = 0; i < xs.length; i++){
            xs[i] = xs[i] + letterStart;
            ys[i] = ys[i] + heightStart;
        }

        float cornerWidth = thickness * 1.4f;
        float cornerHeight = thickness * 1.5f;
        xs[0] = 0;                         ys[0] = 0;
        xs[1] = cornerWidth;               ys[1] = 0;
        xs[2] = letterWidth - thickness;   ys[2] = letterHeight - cornerHeight;
        xs[3] = letterWidth - thickness;   ys[3] = 0;
        xs[4] = letterWidth;               ys[4] = 0;
        xs[5] = letterWidth;               ys[5] = letterHeight;
        xs[6] = letterWidth - cornerWidth; ys[6] = letterHeight;
        xs[7] = thickness;                 ys[7] = cornerHeight;
        xs[8] = thickness;                 ys[8] = letterHeight;
        xs[9] = 0;                         ys[9] = letterHeight;
        for (int i = 0; i < xs.length; i++){
            xs[i] = xs[i] + letterStart;
            ys[i] = ys[i] + heightStart;
        }
        letterStart = letterStart + letterWidth + sep;
        fill(10,xs,ys,c,color);

        // F
        letterWidth = width * 0.22f;
        xs[0] = 0;                  ys[0] = 0;
        xs[1] = letterWidth;        ys[1] = 0;
        xs[2] = letterWidth;        ys[2] = thickness;
        xs[3] = thickness;          ys[3] = thickness;
        xs[4] = thickness;          ys[4] = (letterHeight - thickness) / 2;
        xs[5] = letterWidth * 0.7f; ys[5] = ys[4];
        xs[6] = xs[5];              ys[6] = (letterHeight + thickness) / 2;
        xs[7] = thickness;          ys[7] = ys[6];
        xs[8] = thickness;          ys[8] = letterHeight;
        xs[9] = 0;                  ys[9] = letterHeight;
        for (int i = 0; i < xs.length; i++){
            xs[i] = xs[i] + letterStart;
            ys[i] = ys[i] + heightStart;
        }
        letterStart = letterStart + sep + letterWidth;
        fill(10,xs,ys,c,color);

        // O
        letterWidth = width - letterStart - sep;
        xs[0] = 0;                       ys[0] = 0;
        xs[1] = letterWidth;             ys[1] = 0;
        xs[2] = letterWidth;             ys[2] = letterHeight;
        xs[3] = 0;                       ys[3] = letterHeight;
        xs[4] = 0;                       ys[4] = 0;
        xs[5] = thickness;               ys[5] = thickness;
        xs[6] = thickness;               ys[6] = letterHeight - thickness;
        xs[7] = letterWidth - thickness; ys[7] = letterHeight - thickness;
        xs[8] = letterWidth - thickness; ys[8] = thickness;
        xs[9] = thickness;               ys[9] = thickness;
        for (int i = 0; i < xs.length; i++){
            xs[i] = xs[i] + letterStart;
            ys[i] = ys[i] + heightStart;
        }
        fill(10,xs,ys,c,color);
    }
}
