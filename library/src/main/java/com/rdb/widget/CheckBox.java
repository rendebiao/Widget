package com.rdb.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

public class CheckBox extends CompoundView {

    private float size;
    private float radius;
    private int checkColor;
    private FloatValue floatValue;
    private Path path = new Path();
    private Paint paint = new Paint();
    private RectF checkRect = new RectF();

    public CheckBox(Context context) {
        this(context, null);
    }

    public CheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2 * density);
        paint.setStrokeCap(Paint.Cap.ROUND);
        size = density * 16;
        radius = 1.5f * density;
        checkColor = getColorAccent();
        floatValue = new FloatValue();
    }

    public void setCheckColor(int checkColor) {
        this.checkColor = checkColor;
    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {
        if (isEnabled()) {
            paint.setColor(Color.argb(checked ? 255 : 128, Color.red(checkColor), Color.green(checkColor), Color.blue(checkColor)));
        } else {
            paint.setColor(Color.argb(30, Color.red(checkColor), Color.green(checkColor), Color.blue(checkColor)));
        }
        paint.setStyle(Paint.Style.STROKE);
        float value = size * 0.5f * (0.9f + Math.abs(0.5f - floatValue.curValue) * 0.2f);
        checkRect.set(rectF.centerX() - value, rectF.centerY() - value, rectF.centerX() + value, rectF.centerY() + value);
        canvas.drawRoundRect(checkRect, radius, radius, paint);
        if (floatValue.curValue > 0) {
            float startX1 = checkRect.left + checkRect.width() * 0.2f;
            float startY1 = checkRect.bottom - checkRect.height() * 0.5f;
            float startX2 = checkRect.left + checkRect.width() * 0.4f;
            float startY2 = checkRect.bottom - checkRect.height() * 0.3f;
            float startX3 = checkRect.right - checkRect.width() * 0.2f;
            float startY3 = checkRect.top + checkRect.height() * 0.25f;
            path.moveTo(startX1, startY1);
            float ratio = Math.min(0.25f, floatValue.curValue) * 4;
            path.lineTo(startX1 + (startX2 - startX1) * ratio, startY1 + (startY2 - startY1) * ratio);
            if (floatValue.curValue > 0.25f) {
                ratio = (floatValue.curValue - 0.25f) / 0.75f;
                path.lineTo(startX2 + (startX3 - startX2) * ratio, startY2 + (startY3 - startY2) * ratio);
            }
            canvas.drawPath(path, paint);
            path.reset();
        }
    }

    @Override
    protected void onCheckedChanged(boolean anim) {
        floatValue.moveTo(checked ? 1 : 0, anim);
    }

    @Override
    protected int defaultWidth() {
        return (int) (32 * density);
    }

    @Override
    protected int defaultHeight() {
        return (int) (32 * density);
    }

    @Override
    protected void updateAnimatedValue(float animatedValue) {
        floatValue.updateValue(animatedValue);
    }
}
