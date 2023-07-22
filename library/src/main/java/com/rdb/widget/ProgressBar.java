package com.rdb.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

public abstract class ProgressBar extends View implements ValueAnimator.AnimatorUpdateListener {

    protected int max;
    protected Path path;
    protected Paint paint;
    protected int defaultWidth;
    protected int defaultHeight;
    protected int backgroundColor;
    protected int foregroundColor;
    protected boolean indeterminate;
    protected Boolean showBackground;
    protected int indeterminateDutation = 1500;
    protected Progress progress = new Progress();
    private ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);

    public ProgressBar(Context context) {
        this(context, null);
    }

    public ProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        max = 100;
        foregroundColor = getColorAccent();
        backgroundColor = Color.argb(80, Color.red(foregroundColor), Color.green(foregroundColor), Color.blue(foregroundColor));
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CProgressBar,
                    defStyleAttr, 0);
            indeterminate = typedArray.getBoolean(R.styleable.CProgressBar_indeterminate, false);
            if (typedArray.hasValue(R.styleable.CProgressBar_showBackground)) {
                showBackground = typedArray.getBoolean(R.styleable.CProgressBar_showBackground, false);
            }
            max = typedArray.getInt(R.styleable.CProgressBar_max, 100);
            int progress = typedArray.getInt(R.styleable.CProgressBar_progress, 0);
            setProgress(progress, false, false);
            typedArray.recycle();
        }
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        valueAnimator.addUpdateListener(this);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setInterpolator(new LinearInterpolator());
        updateIndeterminate();
    }

    protected void updateIndeterminate() {
        if (isIndeterminate()) {
            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }
            valueAnimator.setDuration(indeterminateDutation);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            if (isAttached && isShown()) {
                valueAnimator.start();
            }
        } else {
            valueAnimator.setDuration(200);
            valueAnimator.setRepeatCount(0);
        }
        requestLayout();
    }

    public void setProgressColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
        postInvalidate();
    }

    public void setStrokeCap(Paint.Cap cap) {
        paint.setStrokeCap(cap);
        postInvalidate();
    }

    public void setProgressBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        postInvalidate();
    }

    protected void showBackground(boolean showBackground) {
        this.showBackground = showBackground;
        postInvalidate();
    }

    public int getProgress() {
        return progress.toValue;
    }

    public void setProgress(int progress) {
        setProgress(progress, true, true);
    }

    public float getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0 && this.max != max) {
            this.max = max;
            int progress = checkProgress(this.progress.toValue);
            this.progress.moveTo(progress, false);
        }
    }

    public void setProgress(int progress, boolean animateWhenUp, boolean animateWhenDown) {
        progress = checkProgress(progress);
        if (isIndeterminate()) {
            this.progress.moveTo(progress, false);
        } else {
            boolean animate = isAttached && isShown() && (progress - this.progress.curValue > 0 ? animateWhenUp : animateWhenDown);
            this.progress.moveTo(progress, animate);
            if (animate) {
                if (valueAnimator.isRunning()) {
                    valueAnimator.cancel();
                }
                valueAnimator.start();
            } else {
                postInvalidate();
            }
        }
    }

    protected int checkProgress(int progress) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > max) {
            progress = max;
        }
        return progress;
    }

    public void setIndeterminateDutation(int indeterminateDutation) {
        this.indeterminateDutation = indeterminateDutation;
        updateIndeterminate();
    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {

    }

    public boolean isIndeterminate() {
        return indeterminate;
    }

    protected void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
        updateIndeterminate();
    }

    protected abstract int getDefaultWidth();

    protected abstract int getDefaultHeight();

    @Override
    protected final int defaultWidth() {
        defaultWidth = getDefaultWidth();
        return defaultWidth;
    }

    @Override
    protected final int defaultHeight() {
        defaultHeight = getDefaultHeight();
        return defaultHeight;
    }

    @Override
    public final void onAnimationUpdate(ValueAnimator animation) {
        float animatedValue = (float) animation.getAnimatedValue();
        if (!indeterminate) {
            progress.updateValue(animatedValue);
        }
        updateAnimatedValue(animatedValue);
        postInvalidate();
    }

    protected abstract void updateAnimatedValue(float animatedValue);

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isShown() && isIndeterminate()) {
            if (!valueAnimator.isRunning()) {
                valueAnimator.start();
            }
        }
    }

    @Override
    protected void onVisibilityChanged(android.view.View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (isIndeterminate()) {
            if (isAttached && isShown()) {
                if (!valueAnimator.isRunning()) {
                    valueAnimator.start();
                }
            } else {
                if (valueAnimator.isRunning()) {
                    valueAnimator.cancel();
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (isIndeterminate()) {
            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }
        }
        super.onDetachedFromWindow();
    }

    public class Progress {

        int curValue;
        int fromValue;
        int toValue;

        public void moveTo(int toValue, boolean animateToValue) {
            if (animateToValue) {
                this.toValue = toValue;
                this.fromValue = curValue;
            } else {
                this.fromValue = this.curValue = this.toValue = toValue;
            }
        }

        public boolean updateValue(float animatorValue) {
            if (fromValue != toValue) {
                curValue = Math.round(fromValue + (toValue - fromValue) * animatorValue);
                return true;
            }
            return false;
        }
    }
}
