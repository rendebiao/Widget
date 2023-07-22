package com.rdb.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.IdRes;

public class RadioButton extends CompoundView {

    private float size;
    private int radioColor;
    private FloatValue floatValue;
    private Paint paint = new Paint();
    private Group group;

    public RadioButton(Context context) {
        this(context, null);
    }

    public RadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2 * density);
        paint.setStrokeCap(Paint.Cap.ROUND);
        size = density * 16;
        radioColor = getColorAccent();
        floatValue = new FloatValue();
    }

    public void setRadioColor(int radioColor) {
        this.radioColor = radioColor;
    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {
        if (isEnabled()) {
            paint.setColor(Color.argb(checked ? 255 : 128, Color.red(radioColor), Color.green(radioColor), Color.blue(radioColor)));
        } else {
            paint.setColor(Color.argb(30, Color.red(radioColor), Color.green(radioColor), Color.blue(radioColor)));
        }
        paint.setStyle(Paint.Style.STROKE);
        float value = size * 0.56f * (0.9f + Math.abs(0.5f - floatValue.curValue) * 0.2f);
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), value, paint);
        if (floatValue.curValue > 0) {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(rectF.centerX(), rectF.centerY(), size * 0.31f * floatValue.curValue, paint);
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

    @Override
    protected boolean canCancelCheck() {
        return false;
    }

    public void group(OnGroupCheckedChangeListener onCheckedChangeListener, RadioButton... radioButtons) {
        if (group != null) {
            group.clear();
        }
        group = new Group(this, onCheckedChangeListener, radioButtons);
    }

    public interface OnGroupCheckedChangeListener {
        void onCheckedChanged(@IdRes int checkedId);
    }

    private class Group implements OnCheckedChangeListener {

        private RadioButton radioButton;
        private RadioButton[] radioButtons;
        private OnGroupCheckedChangeListener onCheckedChangeListener;

        public Group(RadioButton radioButton, OnGroupCheckedChangeListener onCheckedChangeListener, RadioButton... radioButtons) {
            this.radioButton = radioButton;
            this.radioButtons = radioButtons;
            this.onCheckedChangeListener = onCheckedChangeListener;
            for (RadioButton radio : radioButtons) {
                radio.addOnCheckedChangeListener(this);
            }
            radioButton.addOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundView compoundView, boolean isChecked) {
            if (isChecked) {
                for (RadioButton radio : radioButtons) {
                    if (radio != compoundView) {
                        radio.setChecked(false);
                    }
                }
                if (radioButton != compoundView) {
                    radioButton.setChecked(false);
                }
                onCheckedChangeListener.onCheckedChanged(compoundView.getId());
            }
        }

        public void clear() {
            for (RadioButton radio : radioButtons) {
                radio.removeOnCheckedChangeListener(this);
            }
            radioButton.removeOnCheckedChangeListener(this);
        }
    }
}
