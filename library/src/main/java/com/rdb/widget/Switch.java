package com.rdb.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.IntDef;

public class Switch extends CompoundView {

    public static final int CIRCLE = 0;
    public static final int ROUND_RECT = 1;
    private float width;
    private float height;
    private float padding;
    private FloatValue alphaValue;
    private FloatValue offsetValue;
    private Paint paint = new Paint();
    private int thumbStyle;
    private RectF thumbRect = new RectF();
    private int trackColor;
    private int thumbColor;
    private RectF trackRect = new RectF();

    public Switch(Context context) {
        this(context, null);
    }

    public Switch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Switch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Switch,
                    defStyleAttr, 0);
            thumbStyle = typedArray.getInt(R.styleable.Switch_thumbStyle, 0);
            typedArray.recycle();
        }
        width = density * 42;
        height = density * 20;
        padding = density;
        paint.setAntiAlias(true);
        trackColor = getColorAccent();
        thumbColor = Color.WHITE;
        alphaValue = new FloatValue();
        offsetValue = new FloatValue();
        alphaValue.moveTo(checked ? 255 : 128, false);
        offsetValue.moveTo(checked ? 1 : 0, false);
    }

    public void setThumbStyle(@ThumbStyle int thumbStyle) {
        this.thumbStyle = thumbStyle;
    }

    public void setTrackColor(int trackColor) {
        this.trackColor = trackColor;
    }

    public void setThumbColor(int thumbColor) {
        this.thumbColor = thumbColor;
    }

    @Override
    protected void onThemeChanged() {
        trackColor = getColorAccent();
    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {
        if (isEnabled()) {
            paint.setColor(Color.argb((int) alphaValue.curValue, Color.red(trackColor), Color.green(trackColor), Color.blue(trackColor)));
        } else {
            paint.setColor(Color.argb(30, Color.red(trackColor), Color.green(trackColor), Color.blue(trackColor)));
        }
        trackRect.set(rectF.centerX() - width / 2, rectF.centerY() - height / 2, rectF.centerX() + width / 2, rectF.centerY() + height / 2);
        if (thumbStyle == ROUND_RECT) {
            float trackRadius = 2 * density;
            canvas.drawRoundRect(trackRect, trackRadius, trackRadius, paint);
            paint.setColor(thumbColor);
            float thumbWidth = trackRect.width() / 2 - padding;
            float left = trackRect.left + padding + offsetValue.curValue * (trackRect.width() - padding * 2 - thumbWidth);
            thumbRect.set(left, trackRect.top + padding, left + thumbWidth, trackRect.bottom - padding);
            canvas.drawRoundRect(thumbRect, trackRadius, trackRadius, paint);
        } else {
            float trackRadius = trackRect.height() / 2;
            canvas.drawRoundRect(trackRect, trackRadius, trackRadius, paint);
            paint.setColor(thumbColor);
            float thumbWidth = trackRect.height() - 2 * padding;
            float left = trackRect.left + padding + offsetValue.curValue * (trackRect.width() - padding * 2 - thumbWidth);
            thumbRect.set(left, trackRect.top + padding, left + thumbWidth, trackRect.bottom - padding);
            canvas.drawRoundRect(thumbRect, thumbWidth / 2, thumbWidth / 2, paint);
        }
    }

    @Override
    protected void onCheckedChanged(boolean anim) {
        alphaValue.moveTo(checked ? 255 : 128, anim);
        offsetValue.moveTo(checked ? 1 : 0, anim);
    }

    @Override
    protected int defaultWidth() {
        return (int) (46 * density);
    }

    @Override
    protected int defaultHeight() {
        return (int) (32 * density);
    }

    @Override
    protected void updateAnimatedValue(float animatedValue) {
        alphaValue.updateValue(animatedValue);
        offsetValue.updateValue(animatedValue);
    }

    @IntDef({CIRCLE, ROUND_RECT})
    public @interface ThumbStyle {
    }
}
