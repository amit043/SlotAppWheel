package com.amit.slottappwheel.Entity;

import android.graphics.Bitmap;

/**
 * Created by user on 23/08/2015.
 */
public class Item {

    private Bitmap bitmap;

    public Item(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
