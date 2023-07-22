package com.rdb.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;

public class HorizontalProgressBar extends ProgressBar {

    private float center0;
    private float center1;
    private long animatedCount;
    private FloatValue widthValue0;
    private FloatValue widthValue1;
    private float lastAnimatedValue = 1;

    public HorizontalProgressBar(Context context) {
        this(context, null);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        widthValue0 = new FloatValue();
        widthValue1 = new FloatValue();
    }

    @Override
    public void setIndeterminate(boolean indeterminate) {
        super.setIndeterminate(indeterminate);
    }

    @Override
    public void showBackground(boolean showBackground) {
        super.showBackground(showBackground);
    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {
        super.draw(canvas, rectF);
        paint.setStrokeWidth(rectF.height());
        float start = rectF.left + paint.getStrokeWidth() / 2;
        float width = rectF.right - rectF.left - paint.getStrokeWidth();
        if (showBackground == Boolean.TRUE || (showBackground == null && !indeterminate)) {
            paint.setColor(backgroundColor);
            canvas.drawLine(start, rectF.centerY(), start + width, rectF.centerY(), paint);
        }
        paint.setColor(foregroundColor);
        if (isIndeterminate()) {
            float leftValue, rigthValue;
            if (widthValue0.curValue > 0) {
                leftValue = center0 - widthValue0.curValue / 2;
                rigthValue = center0 + widthValue0.curValue / 2;
                if ((leftValue > 0 && leftValue < 1) || (rigthValue > 0 && rigthValue < 1)) {
                    canvas.drawLine(Math.max(leftValue * width, start), rectF.centerY(), Math.min(rigthValue * width, start + width), rectF.centerY(), paint);
                }
            }
            if (widthValue1.curValue > 0) {
                leftValue = center1 - widthValue1.curValue / 2;
                rigthValue = center1 + widthValue1.curValue / 2;
                if ((leftValue > 0 && leftValue < 1) || (rigthValue > 0 && rigthValue < 1)) {
                    canvas.drawLine(Math.max(leftValue * width, start), rectF.centerY(), Math.min(rigthValue * width, start + width), rectF.centerY(), paint);
                }
            }
        } else {
            canvas.drawLine(start, rectF.centerY(), start + width * progress.curValue / max, rectF.centerY(), paint);
        }
    }

    @Override
    protected int getDefaultWidth() {
        return (int) (density * 48);
    }

    @Override
    protected int getDefaultHeight() {
        return (int) (density * 5);
    }

    @Override
    protected void updateAnimatedValue(float animatedValue) {
        if (isIndeterminate()) {
            boolean value = animatedCount % 2 == 0;
            if (lastAnimatedValue > 0.5f && animatedValue < 0.5f) {
                widthValue0.curValue = widthValue0.fromValue = value ? 1 : 0.8f;
                widthValue0.toValue = value ? 0.2f : 0;
                animatedCount++;
            } else if (lastAnimatedValue < 0.5f && animatedValue >= 0.5f) {
                widthValue1.curValue = widthValue1.fromValue = value ? 0.2f : 0;
                widthValue1.toValue = value ? 1 : 0.8f;
            }
            center0 = -0.5f + animatedValue * 2;
            center1 = -0.5f + (animatedValue > 0.5f ? (animatedValue - 0.5f) : (animatedValue + 0.5f)) * 2;
            widthValue0.updateValue(animatedValue);
            widthValue1.updateValue((animatedValue + 0.5f) % 1f);
        }
        lastAnimatedValue = animatedValue;
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
