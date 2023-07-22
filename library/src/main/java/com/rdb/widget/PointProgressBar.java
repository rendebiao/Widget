package com.rdb.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.IntDef;

public class PointProgressBar extends ProgressBar {

    public static final int CIRCLE = 0;
    public static final int RECT = 1;
    public static final int ROUND_RECT = 2;
    public static final int OBLIQUE_RECT = 3;
    public static final int OBLIQUE_ROUND_RECT = 4;
    public static final int SQUARE = 5;
    public static final int ROUND_SQUARE = 6;
    public static final int OBLIQUE_SQUARE = 7;
    public static final int OBLIQUE_ROUND_SQUARE = 8;
    private int style;
    private CornerPathEffect cornerPathEffect;
    private float[] pointRadius = new float[]{0, 0, 0, 0};

    public PointProgressBar(Context context) {
        this(context, null);
    }

    public PointProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PointProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PointProgressBar,
                    defStyleAttr, 0);
            style = typedArray.getInt(R.styleable.PointProgressBar_pointStyle, 0);
            typedArray.recycle();
        }
        pointRadius[0] = pointRadius[1] = pointRadius[2] = 5 * density;
        pointRadius[3] = 2 * density;
        cornerPathEffect = new CornerPathEffect(density * 2);
        setIndeterminate(true);
    }

    public void setStyle(@PointStyle int style) {
        this.style = style;
    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {
        super.draw(canvas, rectF);
        paint.setPathEffect(null);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(foregroundColor);
        float centerX1 = rectF.left + rectF.width() / 4;
        float centerX2 = rectF.left + rectF.width() / 2;
        float centerX3 = rectF.left + rectF.width() * 3 / 4;
        float widthScale = 0.8f;
        if (style == CIRCLE) {
            canvas.drawCircle(centerX1, rectF.centerY(), pointRadius[0], paint);
            canvas.drawCircle(centerX2, rectF.centerY(), pointRadius[1], paint);
            canvas.drawCircle(centerX3, rectF.centerY(), pointRadius[2], paint);
        } else if (style == RECT) {
            canvas.drawRect(new RectF(centerX1 - pointRadius[0] * widthScale, rectF.centerY() - pointRadius[0], centerX1 + pointRadius[0] * widthScale, rectF.centerY() + pointRadius[0]), paint);
            canvas.drawRect(new RectF(centerX2 - pointRadius[1] * widthScale, rectF.centerY() - pointRadius[1], centerX2 + pointRadius[1] * widthScale, rectF.centerY() + pointRadius[1]), paint);
            canvas.drawRect(new RectF(centerX3 - pointRadius[2] * widthScale, rectF.centerY() - pointRadius[2], centerX3 + pointRadius[2] * widthScale, rectF.centerY() + pointRadius[2]), paint);
        } else if (style == ROUND_RECT) {
            canvas.drawRoundRect(new RectF(centerX1 - pointRadius[0] * widthScale, rectF.centerY() - pointRadius[0], centerX1 + pointRadius[0] * widthScale, rectF.centerY() + pointRadius[0]), pointRadius[3], pointRadius[3], paint);
            canvas.drawRoundRect(new RectF(centerX2 - pointRadius[1] * widthScale, rectF.centerY() - pointRadius[1], centerX2 + pointRadius[1] * widthScale, rectF.centerY() + pointRadius[1]), pointRadius[3], pointRadius[3], paint);
            canvas.drawRoundRect(new RectF(centerX3 - pointRadius[2] * widthScale, rectF.centerY() - pointRadius[2], centerX3 + pointRadius[2] * widthScale, rectF.centerY() + pointRadius[2]), pointRadius[3], pointRadius[3], paint);
        } else if (style == OBLIQUE_RECT) {
            drawObliqueRect(canvas, centerX1, rectF.centerY(), pointRadius[0], widthScale, paint);
            drawObliqueRect(canvas, centerX2, rectF.centerY(), pointRadius[1], widthScale, paint);
            drawObliqueRect(canvas, centerX3, rectF.centerY(), pointRadius[2], widthScale, paint);
        } else if (style == OBLIQUE_ROUND_RECT) {
            paint.setPathEffect(cornerPathEffect);
            drawObliqueRect(canvas, centerX1, rectF.centerY(), pointRadius[0], widthScale, paint);
            drawObliqueRect(canvas, centerX2, rectF.centerY(), pointRadius[1], widthScale, paint);
            drawObliqueRect(canvas, centerX3, rectF.centerY(), pointRadius[2], widthScale, paint);
        } else if (style == SQUARE) {
            canvas.drawRect(new RectF(centerX1 - pointRadius[0], rectF.centerY() - pointRadius[0], centerX1 + pointRadius[0], rectF.centerY() + pointRadius[0]), paint);
            canvas.drawRect(new RectF(centerX2 - pointRadius[1], rectF.centerY() - pointRadius[1], centerX2 + pointRadius[1], rectF.centerY() + pointRadius[1]), paint);
            canvas.drawRect(new RectF(centerX3 - pointRadius[2], rectF.centerY() - pointRadius[2], centerX3 + pointRadius[2], rectF.centerY() + pointRadius[2]), paint);
        } else if (style == ROUND_SQUARE) {
            canvas.drawRoundRect(new RectF(centerX1 - pointRadius[0], rectF.centerY() - pointRadius[0], centerX1 + pointRadius[0], rectF.centerY() + pointRadius[0]), pointRadius[3], pointRadius[3], paint);
            canvas.drawRoundRect(new RectF(centerX2 - pointRadius[1], rectF.centerY() - pointRadius[1], centerX2 + pointRadius[1], rectF.centerY() + pointRadius[1]), pointRadius[3], pointRadius[3], paint);
            canvas.drawRoundRect(new RectF(centerX3 - pointRadius[2], rectF.centerY() - pointRadius[2], centerX3 + pointRadius[2], rectF.centerY() + pointRadius[2]), pointRadius[3], pointRadius[3], paint);
        } else if (style == OBLIQUE_SQUARE) {
            drawObliqueRect(canvas, centerX1, rectF.centerY(), pointRadius[0], 1, paint);
            drawObliqueRect(canvas, centerX2, rectF.centerY(), pointRadius[1], 1, paint);
            drawObliqueRect(canvas, centerX3, rectF.centerY(), pointRadius[2], 1, paint);
        } else if (style == OBLIQUE_ROUND_SQUARE) {
            paint.setPathEffect(cornerPathEffect);
            drawObliqueRect(canvas, centerX1, rectF.centerY(), pointRadius[0], 1, paint);
            drawObliqueRect(canvas, centerX2, rectF.centerY(), pointRadius[1], 1, paint);
            drawObliqueRect(canvas, centerX3, rectF.centerY(), pointRadius[2], 1, paint);
        }
    }

    private void drawObliqueRect(Canvas canvas, float centerX, float centerY, float radius, float widthScale, Paint paint) {
        path.reset();
        path.moveTo(centerX - radius * 1.2f * widthScale, centerY + radius);
        path.lineTo(centerX - radius * 0.8f * widthScale, centerY - radius);
        path.lineTo(centerX + radius * 1.2f * widthScale, centerY - radius);
        path.lineTo(centerX + radius * 0.8f * widthScale, centerY + radius);
        path.close();
        canvas.drawPath(path, paint);
    }

    @Override
    protected int getDefaultWidth() {
        return (int) (density * 48);
    }

    @Override
    protected int getDefaultHeight() {
        return (int) (density * 12);
    }

    @Override
    protected void updateAnimatedValue(float animatedValue) {
        pointRadius[0] = 5 * density * (animatedValue > 0.5f ? 0.75f : (1 - Math.abs(animatedValue - 0.25f)));
        pointRadius[1] = 5 * density * ((animatedValue < 0.25f || animatedValue > 0.75f) ? 0.75f : (1 - Math.abs(animatedValue - 0.5f)));
        pointRadius[2] = 5 * density * (animatedValue < 0.5f ? 0.75f : (1 - Math.abs(animatedValue - 0.75f)));
    }

    @IntDef({CIRCLE, RECT, ROUND_RECT, OBLIQUE_RECT, OBLIQUE_ROUND_RECT, SQUARE, ROUND_SQUARE, OBLIQUE_SQUARE, OBLIQUE_ROUND_SQUARE})
    public @interface PointStyle {
    }
}
