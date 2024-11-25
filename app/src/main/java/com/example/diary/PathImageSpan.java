package com.example.diary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class PathImageSpan extends ImageSpan {
    private final String imagePath;
    
    public PathImageSpan(Context context, Bitmap bitmap, String imagePath) {
        super(context, bitmap);
        this.imagePath = imagePath;
    }
    
    public PathImageSpan(Drawable drawable, String imagePath) {
        super(drawable, ImageSpan.ALIGN_BASELINE);
        this.imagePath = imagePath;
    }
    
    public String getImagePath() {
        return imagePath;
    }
} 