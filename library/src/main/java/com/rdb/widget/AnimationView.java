package com.rdb.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class AnimationView extends View implements ValueAnimator.AnimatorUpdateListener {

    private int position;
    private List<DrawItem> drawItems = new ArrayList<>();
    private ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);

    public AnimationView(Context context) {
        this(context, null);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        valueAnimator.setDuration(500);
        valueAnimator.setRepeatCount(0);
    }

    public void setDuration(int duration) {
        valueAnimator.setDuration(duration);
    }

    public void addDrawItem(DrawItem drawItem) {
        drawItems.add(drawItem);
    }

    public void start() {
        cancel();
        valueAnimator.start();
    }

    public void cancel() {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
    }

    @Override
    protected void onThemeChanged() {

    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {
        for (DrawItem item : drawItems) {
            if (item.startPosition < position) {
                item.draw(canvas, rectF, position);
            } else {
                break;
            }
        }
    }

    @Override
    protected int defaultWidth() {
        return 0;
    }

    @Override
    protected int defaultHeight() {
        return 0;
    }

    @Override
    public final void onAnimationUpdate(ValueAnimator animation) {
        float animatedValue = (float) animation.getAnimatedValue();
        position = (int) (animation.getDuration() * animatedValue);
        postInvalidate();
    }

    public static abstract class DrawItem {

        private int startPosition;
        private int endPosition;
        private Paint paint = new Paint();

        public DrawItem(int startPosition, int endPosition) {
            if (startPosition >= endPosition) {
                throw new RuntimeException("startPosition must < endPosition");
            }
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }

        public Paint getPaint() {
            return paint;
        }

        protected final void draw(Canvas canvas, RectF rectF, int position) {
            draw(canvas, rectF, paint, position > endPosition ? 1 : 1f * (position - startPosition) / (endPosition - startPosition));
        }

        protected abstract void draw(Canvas canvas, RectF rectF, Paint paint, float progress);
    }
}
