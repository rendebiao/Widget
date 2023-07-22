package com.rdb.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;

public class ArrowView extends View {

    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;
    private @Direction
    int arrowDirection = 0;
    private @ColorInt
    int arrowColor;
    private @ColorInt
    int arrowDisableColor;
    private float strokeWidth;
    private Path path = new Path();
    private Paint paint = new Paint();

    public ArrowView(Context context) {
        this(context, null);
    }

    public ArrowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArrowView,
                    defStyleAttr, 0);
            arrowColor = typedArray.getColor(R.styleable.ArrowView_arrowColor, Color.TRANSPARENT);
            arrowDisableColor = typedArray.getColor(R.styleable.ArrowView_arrowDisableColor, Color.TRANSPARENT);
            strokeWidth = typedArray.getDimension(R.styleable.ArrowView_arrowStroke, density * 1.5f);
            arrowDirection = typedArray.getInt(R.styleable.ArrowView_arrowDirection, 0);
            typedArray.recycle();
        }
        if (arrowColor == Color.TRANSPARENT) {
            arrowColor = getColorAccent();
        }
        if (arrowDisableColor == Color.TRANSPARENT) {
            arrowDisableColor = Color.argb(128, Color.red(arrowColor), Color.green(arrowColor), Color.blue(arrowColor));
        }
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void setArrowColor(int arrowColor) {
        setArrowColor(arrowColor, Color.argb(128, Color.red(arrowColor), Color.green(arrowColor), Color.blue(arrowColor)));
    }

    public void setArrowColor(int arrowColor, int arrowDisableColor) {
        this.arrowColor = arrowColor;
        this.arrowDisableColor = arrowDisableColor;
        postInvalidate();
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        postInvalidate();
    }

    public void setArrowDirection(@Direction int arrowDirection) {
        this.arrowDirection = arrowDirection;
        requestLayout();
    }

    public void setStrokeCap(Paint.Cap cap) {
        paint.setStrokeCap(cap);
    }

    public void setStrokeJoin(Paint.Join join) {
        paint.setStrokeJoin(join);
    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {
        paint.setColor(isEnabled() ? arrowColor : arrowDisableColor);
        path.reset();
        float halfStroke = strokeWidth / 2;
        float left = rectF.left + halfStroke;
        float top = rectF.top + halfStroke;
        float right = rectF.right - halfStroke;
        float bottom = rectF.bottom - halfStroke;
        if (arrowDirection == LEFT) {
            path.moveTo(right, top);
            path.lineTo(left, rectF.centerY());
            path.lineTo(right, bottom);
        } else if (arrowDirection == TOP) {
            path.moveTo(left, bottom);
            path.lineTo(rectF.centerX(), top);
            path.lineTo(right, bottom);
        } else if (arrowDirection == RIGHT) {
            path.moveTo(left, top);
            path.lineTo(right, rectF.centerY());
            path.lineTo(left, bottom);
        } else {
            path.moveTo(left, top);
            path.lineTo(rectF.centerX(), bottom);
            path.lineTo(right, top);
        }
        canvas.drawPath(path, paint);
    }

    @Override
    protected int defaultWidth() {
        return (int) (density * (arrowDirection == TOP || arrowDirection == BOTTOM ? 16 : 10));
    }

    @Override
    protected int defaultHeight() {
        return (int) (density * (arrowDirection == TOP || arrowDirection == BOTTOM ? 10 : 16));
    }

    @IntDef({LEFT, TOP, RIGHT, BOTTOM})
    public @interface Direction {
    }
}
