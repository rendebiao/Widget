package com.rdb.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;

public class RingProgressBar extends ShapeProgressBar {

    private static final int OFFSET = 2;
    private int count;
    private float startAngle;
    private float sweepAngle;
    private RectF drawRectF = new RectF();
    private float ringWidth;
    private float lastAnimatedValue;

    public RingProgressBar(Context context) {
        this(context, null);
    }

    public RingProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RingProgressBar,
                    defStyleAttr, 0);
            ringWidth = typedArray.getInt(R.styleable.RingProgressBar_ringWidth, 0);
            typedArray.recycle();
        }
    }

    public void setRingWidth(float ringWidth) {
        this.ringWidth = ringWidth;
    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {
        super.draw(canvas, rectF);
        float size = (shapeSize == WRAP) ? Math.min(rectF.width(), rectF.height()) : defaultWidth;
        float stroke = ringWidth > 0 ? ringWidth : Math.max(size * 0.08f, density);
        float radius = size / 2 - stroke;
        drawRectF.set(rectF.centerX() - radius, rectF.centerY() - radius, rectF.centerX() + radius, rectF.centerY() + radius);
        paint.setStrokeWidth(stroke);
        if (showBackground == Boolean.TRUE || (showBackground == null && !indeterminate)) {
            paint.setColor(backgroundColor);
            canvas.drawArc(drawRectF, 0, 360, false, paint);
        }
        paint.setColor(foregroundColor);
        if (!isIndeterminate()) {
            startAngle = 0;
            sweepAngle = progress.curValue * 360f / max;
        }
        canvas.drawArc(drawRectF, startAngle, sweepAngle, false, paint);
    }

    @Override
    protected void updateAnimatedValue(float animatedValue) {
        if (isIndeterminate()) {
            if (animatedValue < lastAnimatedValue) {
                count = (count + 1) % OFFSET;
            }
            sweepAngle = 45 + 540 * (0.5f - Math.abs(animatedValue - 0.5f));//45-315
            startAngle = (count + animatedValue) * (360 + 360 / OFFSET) - sweepAngle / 2;
            lastAnimatedValue = animatedValue;
        }
    }
}
