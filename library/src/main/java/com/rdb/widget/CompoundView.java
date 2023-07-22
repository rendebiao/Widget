package com.rdb.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.HashSet;
import java.util.Set;

public abstract class CompoundView extends View implements ValueAnimator.AnimatorUpdateListener {

    protected boolean checked;
    private ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
    private Set<OnCheckedChangeListener> onCheckedChangeListeners = new HashSet<>();

    public CompoundView(Context context) {
        this(context, null);
    }

    public CompoundView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompoundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CompoundView,
                    defStyleAttr, 0);
            checked = typedArray.getBoolean(R.styleable.CompoundView_checked, false);
            typedArray.recycle();
        }
        valueAnimator.setDuration(200);
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(this);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        updateCheck();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isEnabled() && (!checked || canCancelCheck())) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    checked = !checked;
                    updateCheck();
                    break;
                default:
                    break;
            }
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    private void updateCheck() {
        for (OnCheckedChangeListener onCheckedChangeListener : onCheckedChangeListeners) {
            onCheckedChangeListener.onCheckedChanged(this, checked);
        }
        boolean anim = isAttached && isShown();
        onCheckedChanged(anim);
        if (anim) {
            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }
            valueAnimator.start();
        } else {
            postInvalidate();
        }
    }

    protected abstract void onCheckedChanged(boolean anim);

    protected abstract void updateAnimatedValue(float animatedValue);

    protected boolean canCancelCheck() {
        return true;
    }

    @Override
    public final void onAnimationUpdate(ValueAnimator animation) {
        float animatedValue = (float) animation.getAnimatedValue();
        updateAnimatedValue(animatedValue);
        postInvalidate();
    }

    public void addOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        if (onCheckedChangeListener != null) {
            onCheckedChangeListeners.add(onCheckedChangeListener);
        }
    }

    public void removeOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        if (onCheckedChangeListener != null) {
            onCheckedChangeListeners.remove(onCheckedChangeListener);
        }
    }

    public void removeAllOnCheckedChangeListener() {
        onCheckedChangeListeners.clear();
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CompoundView compoundView, boolean isChecked);
    }

    public class FloatValue {

        float curValue;
        float fromValue;
        float toValue;

        public void moveTo(float toValue, boolean animateToValue) {
            if (animateToValue) {
                this.toValue = toValue;
                this.fromValue = curValue;
            } else {
                this.fromValue = this.curValue = this.toValue = toValue;
            }
        }

        public boolean updateValue(float animatorValue) {
            if (fromValue != toValue) {
                curValue = fromValue + (toValue - fromValue) * animatorValue;
                return true;
            }
            return false;
        }
    }
}

