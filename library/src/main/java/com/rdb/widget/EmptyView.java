package com.rdb.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;

public class EmptyView extends View {

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {

    }

    @Override
    protected int defaultWidth() {
        return 0;
    }

    @Override
    protected int defaultHeight() {
        return 0;
    }
}
