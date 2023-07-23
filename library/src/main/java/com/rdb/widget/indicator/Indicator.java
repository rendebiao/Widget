package com.rdb.widget.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.rdb.widget.R;
import com.rdb.widget.View;

public class Indicator extends View {

    private int count;
    private int align;
    private int space;
    private int radius;
    private Paint paint;
    private int selectColor;
    private int unselectColor;
    private boolean horizontal;
    private RectF tempRectF = new RectF();
    private IndicatorAdapter indicatorAdapter;
    private DataSetObserver dataSetObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            if (indicatorAdapter != null) {
                if (count != indicatorAdapter.getCount()) {
                    count = indicatorAdapter.getCount();
                    requestLayout();
                } else {
                    postInvalidate();
                }
            }
        }
    };

    public Indicator(Context context) {
        this(context, null);
    }

    public Indicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Indicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Indicator,
                defStyle, 0);
        align = typedArray.getInt(R.styleable.Indicator_indicatorAlign, 1);
        space = typedArray.getDimensionPixelSize(R.styleable.Indicator_indicatorSpace, (int) (density * 8));
        radius = typedArray.getDimensionPixelSize(R.styleable.Indicator_indicatorRadius, (int) (density * 4));
        horizontal = typedArray.getBoolean(R.styleable.Indicator_indicatorHorizontal, true);
        selectColor = typedArray.getColor(R.styleable.Indicator_indicatorSelectedColor, getColorAccent());
        unselectColor = typedArray.getColor(R.styleable.Indicator_indicatorUnselectedColor, Color.WHITE);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public IndicatorAdapter getAdapter() {
        return indicatorAdapter;
    }

    public void setAdapter(IndicatorAdapter indicatorAdapter) {
        if (this.indicatorAdapter != null) {
            this.indicatorAdapter.unregisterDataSetObserver(dataSetObserver);
        }
        this.indicatorAdapter = indicatorAdapter;
        if (this.indicatorAdapter != null) {
            this.indicatorAdapter.registerDataSetObserver(dataSetObserver);
            count = indicatorAdapter.getCount();
            requestLayout();
        }
    }

    @Override
    protected void onThemeChanged() {
        selectColor = getColorAccent();
        postInvalidate();
    }

    @Override
    protected void draw(Canvas canvas, RectF rectF) {
        if (indicatorAdapter != null && count > 0) {
            int select = indicatorAdapter.getSelect();
            float offset = indicatorAdapter.getOffset();
            if (select < 0 || select >= count || (select == 0 && offset < 0) || (select == count - 1 && offset > 0)) {
                throw new RuntimeException("error select = " + select + " offset = " + offset);
            } else if (count > 0) {
                int minSize = count * 2 * radius + (count - 1) * space;
                float width = rectF.width();
                float height = rectF.height();
                float centerX;
                float centerY;
                if (horizontal) {
                    centerY = height / 2;
                    if (width < minSize) {
                        throw new RuntimeException("error width < minSize ");
                    }
                    if (align == 0) {
                        centerX = radius;
                    } else if (align == 2) {
                        centerX = width - minSize + radius;
                    } else {
                        centerX = (width - minSize) / 2 + radius;
                    }

                    float selectCenterX = -1;
                    paint.setColor(unselectColor);
                    for (int i = 0; i < count; i++) {
                        boolean isSelect = select == i;
                        if (isSelect) {
                            selectCenterX = centerX + offset * (radius * 2 + space);
                        }
                        tempRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
                        canvas.drawOval(tempRectF, paint);
                        centerX += (2 * radius + space);
                    }
                    if (selectCenterX >= 0) {
                        paint.setColor(selectColor);
                        tempRectF.set(selectCenterX - radius, centerY - radius, selectCenterX + radius, centerY + radius);
                        canvas.drawOval(tempRectF, paint);
                    }
                } else {
                    centerX = width / 2;
                    if (height < minSize) {
                        throw new RuntimeException("error height < minSize ");
                    }
                    if (align == 0) {
                        centerY = radius;
                    } else if (align == 2) {
                        centerY = height - minSize + radius;
                    } else {
                        centerY = (height - minSize) / 2 + radius;
                    }
                    float selectCenterY = -1;
                    paint.setColor(unselectColor);
                    for (int i = 0; i < count; i++) {
                        boolean isSelect = select == i;
                        if (isSelect) {
                            selectCenterY = centerY + offset * (radius * 2 + space);
                        }
                        tempRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
                        canvas.drawOval(tempRectF, paint);
                        centerY += (2 * radius + space);
                    }
                    if (selectCenterY >= 0) {
                        paint.setColor(selectColor);
                        tempRectF.set(centerX - radius, selectCenterY - radius, centerX + radius, selectCenterY + radius);
                        canvas.drawOval(tempRectF, paint);
                    }
                }
            }
        }
    }

    @Override
    protected int defaultWidth() {
        if (horizontal) {
            if (indicatorAdapter == null) {
                return 0;
            } else {
                int count = indicatorAdapter.getCount();
                return count * 2 * radius + (count - 1) * space;
            }
        } else {
            return 2 * radius;
        }
    }

    @Override
    protected int defaultHeight() {
        if (!horizontal) {
            if (indicatorAdapter == null) {
                return 0;
            } else {
                int count = indicatorAdapter.getCount();
                return count * 2 * radius + (count - 1) * space;
            }
        } else {
            return 2 * radius;
        }
    }
}
