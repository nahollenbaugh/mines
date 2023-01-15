package com.nahollenbaugh.mines.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.nahollenbaugh.mines.drawing.DrawImage;

public class DrawnCycle extends DrawnButton {
    protected DrawImage[] drawImages;
    int currentImage = 0;
    public DrawnCycle(Context ctxt, AttributeSet attr, int capacity){
        super(ctxt, attr);
        drawImages = new DrawImage[capacity];
        // register the listener that cycles the images.
        setOnClickListener(v -> {});
    }

    protected int imagesAdded = 0;
    public void addImage(DrawImage image){
        if (imagesAdded == 0){
            this.draw = image;
        }
        if (imagesAdded >= drawImages.length){
            throw new IllegalStateException("too many images");
        }
        drawImages[imagesAdded] = image;
        imagesAdded = imagesAdded + 1;
    }

    public void goTo(DrawImage image){
        for (int i = 0; i < drawImages.length; i++, currentImage++){
            if (drawImages[currentImage] == image){
                setDrawImage(drawImages[currentImage]);
                return;
            }
        }
        throw new IllegalArgumentException("The provided image is not in this cycle");
    }

    public void setOnClickListener(OnClickListener listener){
        super.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                currentImage = currentImage + 1;
                if (currentImage == drawImages.length || drawImages[currentImage] == null){
                    currentImage = 0;
                }
                setDrawImage(drawImages[currentImage]);
                listener.onClick(v);
            }
        });
    }
}
