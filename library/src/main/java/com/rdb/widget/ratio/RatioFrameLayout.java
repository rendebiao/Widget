package com.rdb.widget.ratio;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.rdb.widget.R;

public class RatioFrameLayout extends FrameLayout {

    private float mRatioX = 1;
    private float mRatioY = 1;

    public RatioFrameLayout(Context context) {
        this(context, null);
    }

    public RatioFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Ratio,
                defStyle, 0);
        mRatioX = typedArray.getFloat(R.styleable.Ratio_ratioX, 1);
        mRatioY = typedArray.getFloat(R.styleable.Ratio_ratioY, 1);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childWidthSize = measure(widthMeasureSpec);
        int childHeightSize = (int) (childWidthSize * mRatioY / mRatioX);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int measure(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result;
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = getDefaultSize(0, measureSpec);
        }
        return result;
    }
}
