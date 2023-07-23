package com.rdb.widget.tab;

import android.content.Context;
import android.database.DataSetObserver;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.rdb.widget.tab.TabLayout.OnTabClickListener;

public class TabViewPager extends ViewPager implements OnTabClickListener {

    private TabLayout tabLayout;
    private OnPageChangeListener pageChangeListener;
    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateTabs();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    };

    private OnPageChangeListener proxyPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (pageChangeListener != null) {
                pageChangeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (tabLayout != null) {
                tabLayout.setIndicatorOffset(position, positionOffset);
            }
            if (pageChangeListener != null) {
                pageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (pageChangeListener != null) {
                pageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };

    public TabViewPager(Context context) {
        super(context);
        super.addOnPageChangeListener(proxyPageChangeListener);
    }

    public TabViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.addOnPageChangeListener(proxyPageChangeListener);
    }

    public void bindTabLayout(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
        if (this.tabLayout != null) {
            this.tabLayout.setOnTabSelectedListener(this);
            updateTabs();
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        pageChangeListener = listener;
    }

    @Override
    public boolean onTabClick(TabLayout tabLayout, View view, int position) {
        if (getAdapter() != null && getAdapter().getCount() > position) {
            setCurrentItem(position);
        }
        return true;
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unregisterDataSetObserver(dataSetObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerDataSetObserver(dataSetObserver);
        }
        if (tabLayout != null) {
            updateTabs();
        }
    }

    private void updateTabs() {
        if (tabLayout != null && getAdapter() != null) {
            int count = getAdapter().getCount();
            tabLayout.removeAllTabs();
            for (int i = 0; i < count; i++) {
                CharSequence title = getAdapter().getPageTitle(i);
                if (TextUtils.isEmpty(title)) {
                    title = "Tab" + i;
                }
                tabLayout.addTab(0, title.toString());
            }
        }
    }
}
