package com.rdb.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.IntDef;

public abstract class ShapeProgressBar extends ProgressBar {

    public static final int WRAP = 0;
    public static final int SMALL = 1;
    public static final int MIDDLE = 2;
    public static final int LARGE = 3;
    protected int shapeSize;

    public ShapeProgressBar(Context context) {
        this(context, null);
    }

    public ShapeProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShapeProgressBar,
                    defStyleAttr, 0);
            shapeSize = typedArray.getInt(R.styleable.ShapeProgressBar_shapeSize, 0);
            typedArray.recycle();
        }
    }

    public void setShapeSize(@ShapeSize int shapeSize) {
        this.shapeSize = shapeSize;
    }

    @Override
    protected int getDefaultWidth() {
        float width;
        if (shapeSize == SMALL) {
            width = density * 32;
        } else if (shapeSize == LARGE) {
            width = density * 64;
        } else if (shapeSize == MIDDLE) {
            width = density * 48;
        } else {
            width = density * 48;
        }
        return (int) width;
    }

    @Override
    protected int getDefaultHeight() {
        return getDefaultWidth();
    }

    @IntDef({WRAP, SMALL, MIDDLE, LARGE})
    public @interface ShapeSize {
    }
}
