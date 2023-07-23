package com.rdb.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.math.BigDecimal;

public class SeekBar extends DragBar {

    private int thumbColor;
    private float thumbRadius;
    private float progressHeight;

    public SeekBar(Context context) {
        this(context, null);
    }

    public SeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        thumbRadius = 6 * density;
        progressHeight = 3 * density;
        thumbColor = foregroundColor;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setThumbColor(int thumbColor) {
        this.thumbColor = thumbColor;
        postInvalidate();
    }

    public void setThumbRadius(float thumbRadius) {
        this.thumbRadius = thumbRadius;
        requestLayout();
    }

    public void setProgressHeight(float progressHeight) {
        this.progressHeight = progressHeight;
        requestLayout();
    }

    @Override
    protected void onThemeChanged() {
        thumbColor = getColorAccent();
        super.onThemeChanged();
    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {
        super.draw(canvas, rectF);
        paint.setStrokeWidth(progressHeight);
        float padding = Math.max(progressHeight / 2, thumbRadius);
        float start = rectF.left + padding;
        float width = rectF.right - rectF.left - padding * 2;
        float centerX = start + width * progress.curValue / max;
        paint.setColor(backgroundColor);
        canvas.drawLine(start, rectF.centerY(), start + width, rectF.centerY(), paint);
        paint.setColor(foregroundColor);
        canvas.drawLine(start, rectF.centerY(), centerX, rectF.centerY(), paint);
        paint.setColor(thumbColor);
        paint.setStrokeWidth(0);
        canvas.drawCircle(centerX, rectF.centerY(), thumbRadius, paint);
    }

    @Override
    protected int getDefaultWidth() {
        return (int) (density * 64);
    }

    @Override
    protected int getDefaultHeight() {
        return (int) new BigDecimal(Math.max(thumbRadius * 2, progressHeight)).setScale(0, BigDecimal.ROUND_UP).floatValue();
    }

    @Override
    protected void updateAnimatedValue(float animatedValue) {

    }
}
