package com.rdb.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

public abstract class DragBar extends ProgressBar {

    protected RectF dragRectF = new RectF();
    private boolean drag;
    private OnProgressChangeListener onProgressChangeListener;

    public DragBar(Context context) {
        this(context, null);
    }

    public DragBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setProgress(int progress, boolean animateWhenUp, boolean animateWhenDown) {
        super.setProgress(progress, animateWhenUp, animateWhenDown);
        if (onProgressChangeListener != null) {
            onProgressChangeListener.onProgressChanged(this, progress, false);
        }
    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {
        dragRectF.set(rectF);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                drag = dragRectF.contains(event.getX(), event.getY());
                if (drag) {
                    progress.moveTo(moveProgress(event.getX()), false);
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (drag) {
                    progress.moveTo(moveProgress(event.getX()), false);
                    if (onProgressChangeListener != null) {
                        onProgressChangeListener.onProgressChanged(this, progress.curValue, true);
                    }
                    postInvalidate();
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (drag) {
                    progress.moveTo(moveProgress(event.getX()), true);
                    if (onProgressChangeListener != null) {
                        onProgressChangeListener.onProgressChanged(this, progress.curValue, true);
                    }
                    postInvalidate();
                    drag = false;
                }
            }
        }
        return true;
    }

    private int moveProgress(float x) {
        int value = Math.round((x - dragRectF.left) * max / dragRectF.width());
        if (value < 0) {
            return 0;
        } else if (value > max) {
            return max;
        }
        return value;
    }

    @Override
    protected void updateAnimatedValue(float animatedValue) {

    }

    @Override
    public void setOnClickListener(OnClickListener l) {

    }

    public void setOnProgressChangeListener(OnProgressChangeListener onProgressChangeListener) {
        this.onProgressChangeListener = onProgressChangeListener;
    }

    public interface OnProgressChangeListener {
        void onProgressChanged(DragBar dragBar, int progress, boolean fromUser);
    }
}
