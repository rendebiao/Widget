package com.rdb.widget.page;

import android.graphics.Bitmap;
import android.graphics.Canvas;

class Page {

    private Bitmap bitmap;
    private final Canvas canvas;

    public Page() {
        canvas = new Canvas();
    }

    public void updateSize(int width, int height) {
        if (bitmap != null) {
            bitmap.recycle();
        }
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
