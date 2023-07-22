package com.rdb.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

public class RatingBar extends DragBar {

    private int numStars;
    private int starRadius;
    private Path starPath = new Path();

    public RatingBar(Context context) {
        this(context, null);
    }

    public RatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setStyle(Paint.Style.FILL);
        numStars = 5;
        starRadius = (int) (density * 10);
    }

    public void setStarRadius(int starRadius) {
        this.starRadius = starRadius;
        requestLayout();
    }

    public int getNumStars() {
        return numStars;
    }

    public void setNumStars(int numStars) {
        if (numStars <= 0) {
            return;
        }
        this.numStars = numStars;
        requestLayout();
    }

    public float getRating() {
        return getProgress() / getProgressPerStar();
    }

    public void setRating(float rating) {
        setProgress(Math.round(rating * getProgressPerStar()));
    }

    public float getStepSize() {
        return getNumStars() / getMax();
    }

    private float getProgressPerStar() {
        if (numStars > 0) {
            return 1f * getMax() / numStars;
        } else {
            return 1;
        }
    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {
        super.draw(canvas, rectF);
        starRadius = (int) Math.min((rectF.right - rectF.left) / numStars, starRadius * 2) / 2;
        float width = starRadius * 2 * numStars;
        float right = rectF.left + width;
        float centerX = rectF.left + width * progress.curValue / max;
        dragRectF.right = right;
        paint.setColor(foregroundColor);
        int sc1 = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        for (int i = 0; i < numStars; i++) {
            drawStar(canvas, rectF.left + starRadius * (i + 0.5f) * 2, rectF.centerY(), starRadius, paint);
        }
        paint.setColor(backgroundColor);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(centerX, rectF.top, right, rectF.bottom, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(sc1);
    }

    private void drawStar(Canvas canvas, float centerX, float centerY, float startRadius, Paint paint) {
        float minRadius = startRadius / 2;
        starPath.reset();
        starPath.moveTo(centerX, centerY - startRadius);
        starPath.lineTo(getCirclePointX(centerX, minRadius, 306), getCirclePointY(centerY, minRadius, 306));
        starPath.lineTo(getCirclePointX(centerX, startRadius, 342), getCirclePointY(centerY, startRadius, 342));
        starPath.lineTo(getCirclePointX(centerX, minRadius, 18), getCirclePointY(centerY, minRadius, 18));
        starPath.lineTo(getCirclePointX(centerX, startRadius, 54), getCirclePointY(centerY, startRadius, 54));
        starPath.lineTo(centerX, centerY + minRadius);
        starPath.lineTo(getCirclePointX(centerX, startRadius, 126), getCirclePointY(centerY, startRadius, 126));
        starPath.lineTo(getCirclePointX(centerX, minRadius, 162), getCirclePointY(centerY, minRadius, 162));
        starPath.lineTo(getCirclePointX(centerX, startRadius, 198), getCirclePointY(centerY, startRadius, 198));
        starPath.lineTo(getCirclePointX(centerX, minRadius, 234), getCirclePointY(centerY, minRadius, 234));
        starPath.close();
        canvas.drawPath(starPath, paint);
        starPath.reset();
    }

    private float getCirclePointX(float centerX, float radius, float degree) {
        return (float) (centerX + radius * Math.cos(degree * Math.PI / 180));
    }

    private float getCirclePointY(float centerY, float radius, float degree) {
        return (float) (centerY + radius * Math.sin(degree * Math.PI / 180));
    }

    @Override
    protected int getDefaultWidth() {
        return starRadius * 2 * numStars;
    }

    @Override
    protected int getDefaultHeight() {
        return starRadius * 2;
    }
}
