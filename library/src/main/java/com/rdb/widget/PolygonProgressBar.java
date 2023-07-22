package com.rdb.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class PolygonProgressBar extends ShapeProgressBar {

    private float value;
    private float[] degrees;
    private float sideWidth;
    private Path backgroundPath = new Path();
    private Path foregroundPath = new Path();
    private List<Point> points = new ArrayList<>();

    public PolygonProgressBar(Context context) {
        this(context, null);
    }

    public PolygonProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolygonProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int sideCount = 3;
        float startDegree = 0;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PolygonProgressBar,
                    defStyleAttr, 0);
            sideCount = typedArray.getInt(R.styleable.PolygonProgressBar_sideCount, 0);
            startDegree = typedArray.getFloat(R.styleable.PolygonProgressBar_startDegree, 0);
            sideWidth = typedArray.getDimension(R.styleable.PolygonProgressBar_sideWidth, 0);
            typedArray.recycle();
        }
        setStyle(sideCount, startDegree);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void setStyle(int sideCount, float startDegree) {
        if (degrees == null || degrees.length != sideCount) {
            degrees = new float[sideCount];
        }
        for (int i = 0; i < degrees.length; i++) {
            degrees[i] = 360 * i / sideCount + startDegree;
        }
    }

    public void setSideWidth(float sideWidth) {
        this.sideWidth = sideWidth;
    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {
        super.draw(canvas, rectF);
        float centerX = rectF.centerX();
        float centerY = rectF.centerY();
        float size = Math.min(rectF.width(), rectF.height());
        float strokeWidth = sideWidth > 0 ? sideWidth : Math.max(size * 0.08f, density);
        paint.setStrokeWidth(strokeWidth);
        float radius = (size - strokeWidth) / 2;
        float perValue = 1f;
        for (int i = 0; i < degrees.length; i++) {
            Point point;
            if (points.size() > i) {
                point = points.get(i);
            } else {
                point = new Point();
                points.add(point);
            }
            point.x = getCirclePointX(centerX, radius, degrees[i]);
            point.y = getCirclePointY(centerY, radius, degrees[i]);
            if (i == 0) {
                backgroundPath.moveTo(point.x, point.y);
            } else {
                backgroundPath.lineTo(point.x, point.y);
            }
        }
        float startValue;
        float endValue;
        if (isIndeterminate()) {
            startValue = value * degrees.length;
            endValue = (value + 0.5f) * degrees.length;
        } else {
            startValue = 0;
            endValue = progress.curValue * degrees.length * 1f / max;
        }
        int startSide = (int) startValue;
        int endSide = (int) endValue;
        Point prePoint = points.get(startSide % degrees.length);
        Point nextPoint = points.get((startSide + 1) % degrees.length);
        float x = getValue(prePoint.x, nextPoint.x, startValue % perValue);
        float y = getValue(prePoint.y, nextPoint.y, startValue % perValue);
        foregroundPath.moveTo(x, y);
        if (startSide == endSide) {
            x = getValue(prePoint.x, nextPoint.x, endValue % perValue);
            y = getValue(prePoint.y, nextPoint.y, endValue % perValue);
            foregroundPath.lineTo(x, y);
        } else {
            foregroundPath.lineTo(nextPoint.x, nextPoint.y);
            for (int i = startSide + 1; i <= endSide; i++) {
                prePoint = points.get(i % degrees.length);
                nextPoint = points.get((i + 1) % degrees.length);
                if (i < endSide) {
                    foregroundPath.lineTo(nextPoint.x, nextPoint.y);
                } else if (i == endSide) {
                    x = getValue(prePoint.x, nextPoint.x, endValue % perValue);
                    y = getValue(prePoint.y, nextPoint.y, endValue % perValue);
                    foregroundPath.lineTo(x, y);
                }
            }
        }
        backgroundPath.close();
        if (showBackground == Boolean.TRUE || (showBackground == null && !indeterminate)) {
            paint.setColor(backgroundColor);
            canvas.drawPath(backgroundPath, paint);
        }
        paint.setColor(foregroundColor);
        canvas.drawPath(foregroundPath, paint);
        backgroundPath.reset();
        foregroundPath.reset();
    }

    @Override
    protected void updateAnimatedValue(float animatedValue) {
        value = animatedValue;
    }

    private float getValue(float start, float end, float ratio) {
        return start + (end - start) * ratio;
    }

    private float getCirclePointX(float centerX, float radius, float degree) {
        return (float) (centerX + radius * Math.cos(degree * Math.PI / 180));
    }

    private float getCirclePointY(float centerY, float radius, float degree) {
        return (float) (centerY + radius * Math.sin(degree * Math.PI / 180));
    }

    public class Point {
        float x;
        float y;
    }
}
