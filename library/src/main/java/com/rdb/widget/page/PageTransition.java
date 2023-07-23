package com.rdb.widget.page;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class PageTransition {

    protected abstract void onTransition(Canvas canvas, Paint paint, Bitmap in, Bitmap out, float moveX);
}
