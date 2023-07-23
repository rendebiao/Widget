package com.rdb.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class View extends android.view.View {

    protected float density;
    protected boolean isFocused;
    protected boolean isAttached;
    private float touchX;
    private float touchY;
    private static List<WeakReference<View>> themeChangedListeners = new ArrayList<>();
    private RectF rectF = new RectF();
    private OnDrawListener drawListener;
    private boolean followTheme;

    public View(Context context) {
        this(context, null);
    }

    public View(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        density = context.getResources().getDisplayMetrics().density;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.View,
                    defStyleAttr, 0);
            setEnabled(typedArray.getBoolean(R.styleable.View_enabled, isEnabled()));
            followTheme = typedArray.getBoolean(R.styleable.View_followTheme, true);
            if (followTheme) {
                themeChangedListeners.add(new WeakReference<View>(this));
            }
            typedArray.recycle();
        }
    }

    protected int getColorAccent() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureDimension(defaultWidth() + getPaddingLeft() + getPaddingRight(), widthMeasureSpec);
        int height = measureDimension(defaultHeight() + getPaddingTop() + getPaddingBottom(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected final void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rectF.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        draw(canvas, rectF);
        if (drawListener != null) {
            drawListener.onDraw(this, canvas, rectF);
        }
    }

    public static void notifyThemeChanged() {
        Iterator<WeakReference<View>> iterator = themeChangedListeners.iterator();
        while (iterator.hasNext()) {
            View view = iterator.next().get();
            if (view != null) {
                view.onThemeChanged();
            } else {
                iterator.remove();
            }
        }
    }

    protected abstract void draw(Canvas canvas, RectF rectF);

    protected abstract int defaultWidth();

    protected abstract int defaultHeight();

    public RectF getRectF() {
        return rectF;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        isFocused = hasWindowFocus;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();
        return super.onTouchEvent(event);
    }

    public float getLastTouchX() {
        return touchX;
    }

    public float getLastTouchY() {
        return touchY;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;   //UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    protected void setTypeface(Paint paint, Typeface typeface, int style) {
        if (style > 0) {
            if (typeface == null) {
                typeface = Typeface.defaultFromStyle(style);
            } else {
                typeface = Typeface.create(typeface, style);
            }
            paint.setTypeface(typeface);
            int typefaceStyle = typeface != null ? typeface.getStyle() : 0;
            int need = style & ~typefaceStyle;
            paint.setFakeBoldText((need & Typeface.BOLD) != 0);
            paint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
        } else {
            paint.setFakeBoldText(false);
            paint.setTextSkewX(0);
            paint.setTypeface(typeface);
        }
    }

    public void setDrawListener(OnDrawListener drawListener) {
        this.drawListener = drawListener;
    }

    protected abstract void onThemeChanged();

    public interface OnDrawListener {
        void onDraw(View view, Canvas canvas, RectF rectF);
    }

    public interface OnThemeChangedListener {
        void onThemeChnaged();
    }
}
