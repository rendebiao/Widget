package com.rdb.widget.indicator;

import android.content.Context;
import android.util.AttributeSet;

import androidx.viewpager.widget.ViewPager;

public class ViewPagerIndicator extends Indicator implements ViewPager.OnPageChangeListener {

    private int position;
    private float positionOffset;
    private ViewPager viewPager;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        IndicatorAdapter indicatorAdapter = new IndicatorAdapter() {
            @Override
            public int getCount() {
                if (viewPager != null && viewPager.getAdapter() != null) {
                    return viewPager.getAdapter().getCount();
                }
                return 0;
            }

            @Override
            public int getSelect() {
                return position;
            }

            @Override
            public float getOffset() {
                return positionOffset;
            }
        };
        setAdapter(indicatorAdapter);
    }

    public void bindViewPager(ViewPager viewPager) {
        if (viewPager != null) {
            this.viewPager = viewPager;
            position = this.viewPager.getCurrentItem();
            this.viewPager.addOnPageChangeListener(this);
            getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.position = position;
        this.positionOffset = positionOffset;
        getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
