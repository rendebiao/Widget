package com.rdb.widget.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rdb.widget.R;

import java.util.ArrayList;
import java.util.List;

public class TabLayout extends FrameLayout implements OnClickListener {

    private float density;
    private int lastPosition;
    private TabStyle tabStyle;
    private View indicatorView;
    private FrameLayout scrollLayout;
    private FrameLayout rootLayout;
    private LinearLayout contentLayout;
    private OnTabClickListener tabClickListener;
    private OnTabDoubleClickListener tabDoubleClickListener;
    private List<TabView> tabViews = new ArrayList<TabView>();
    private List<TabView> recycleTabViews = new ArrayList<TabView>();
    private SparseArray<Long> lastClickTimeArray = new SparseArray<Long>();

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tabStyle = new TabStyle();
        density = getResources().getDisplayMetrics().density;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Tab, defStyleAttr, 0);
        tabStyle.iconSize = typedArray.getDimensionPixelSize(R.styleable.Tab_tabIconSize, -1);
        tabStyle.titleSize = typedArray.getDimensionPixelSize(R.styleable.Tab_tabTitleSize, -1);
        tabStyle.iconAlign = typedArray.getDimensionPixelSize(R.styleable.Tab_tabIconAlign, 0);
        tabStyle.iconMargin = typedArray.getDimensionPixelSize(R.styleable.Tab_tabIconMargin, 0);
        tabStyle.paddingVertical = typedArray.getDimensionPixelSize(R.styleable.Tab_tabPaddingVertical, 0);
        tabStyle.paddingHorizontal = typedArray.getDimensionPixelSize(R.styleable.Tab_tabPaddingHorizontal, 0);
        tabStyle.titleSelectedColor = typedArray.getColor(R.styleable.Tab_tabTitleSelectedColor, Color.BLACK);
        tabStyle.titleUnselectedColor = typedArray.getColor(R.styleable.Tab_tabTitleUnselectedColor, Color.GRAY);
        tabStyle.wrapContent = typedArray.getBoolean(R.styleable.Tab_tabWrapContent, false);
        tabStyle.horizontal = typedArray.getBoolean(R.styleable.Tab_tabHorizontal, true);
        tabStyle.showIndicator = typedArray.getBoolean(R.styleable.Tab_tabShowIndicator, false);
        tabStyle.indicatorSize = typedArray.getDimensionPixelSize(R.styleable.Tab_tabIndicatorSize, -1);
        tabStyle.indicatorAlign = typedArray.getInt(R.styleable.Tab_tabIndicatorAlign, -1);
        tabStyle.indicatorColor = typedArray.getColor(R.styleable.Tab_tabIndicatorBackground, Color.TRANSPARENT);
        typedArray.recycle();
        if (tabStyle.iconSize <= 0) {
            tabStyle.iconSize = (int) (density * 20);
        }
        if (tabStyle.titleSize <= 0) {
            tabStyle.titleSize = (int) (density * 14);
        }
        if (tabStyle.indicatorSize <= 0) {
            tabStyle.indicatorSize = (int) (density * 2);
        }
        if (tabStyle.indicatorAlign < 0) {
            tabStyle.indicatorAlign = 1;
        }
        rootLayout = new FrameLayout(context);
        contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(tabStyle.horizontal ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        if (tabStyle.horizontal) {
            scrollLayout = new HorizontalScrollView(context);
            scrollLayout.setHorizontalScrollBarEnabled(false);
            addView(scrollLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        } else {
            scrollLayout = new ScrollView(context);
            scrollLayout.setVerticalScrollBarEnabled(false);
            addView(scrollLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
        scrollLayout.setOverScrollMode(View.OVER_SCROLL_NEVER);
        updateLayout();
        indicatorView = new View(context);
        indicatorView.setBackgroundColor(tabStyle.indicatorColor);
        LayoutParams lp = new LayoutParams(tabStyle.horizontal ? 0
                : tabStyle.indicatorSize, tabStyle.horizontal ? tabStyle.indicatorSize : 0);
        if (tabStyle.indicatorAlign == 0) {
            lp.gravity = tabStyle.horizontal ? Gravity.TOP : Gravity.LEFT;
        } else {
            lp.gravity = tabStyle.horizontal ? Gravity.BOTTOM : Gravity.RIGHT;
        }
        rootLayout.addView(indicatorView, lp);
        indicatorView.setVisibility(tabStyle.showIndicator ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateLayout() {
        removeView(rootLayout);
        scrollLayout.removeView(rootLayout);
        rootLayout.removeView(contentLayout);
        if (tabStyle.horizontal) {
            if (tabStyle.wrapContent) {
                scrollLayout.addView(rootLayout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
                rootLayout.addView(contentLayout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            } else {
                addView(rootLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                rootLayout.addView(contentLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }
        } else {
            if (tabStyle.wrapContent) {
                scrollLayout.addView(rootLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                rootLayout.addView(contentLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            } else {
                addView(rootLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                rootLayout.addView(contentLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }
        }
        for (TabView tabView : tabViews) {
            layoutTabView(tabView);
        }
    }

    private void layoutTabView(TabView tabView) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabView.getLayoutParams();
        if (tabStyle.horizontal) {
            lp.width = tabStyle.wrapContent ? LayoutParams.MATCH_PARENT : 0;
            lp.height = LayoutParams.MATCH_PARENT;
        } else {
            lp.width = LayoutParams.MATCH_PARENT;
            lp.height = tabStyle.wrapContent ? LayoutParams.MATCH_PARENT : 0;
        }
        lp.weight = tabStyle.wrapContent ? 0 : 1;
        tabView.setLayoutParams(lp);
    }

    public TabStyle getTabStyle() {
        return tabStyle;
    }

    public void addTab(int iconRes, int titleRes) {
        addTab(iconRes, titleRes == 0 ? null : getResources().getString(titleRes));
    }

    public void addTab(int iconRes, String title) {
        TabView tabView;
        if (recycleTabViews.size() > 0) {
            tabView = recycleTabViews.remove(0);
            tabView.setPadding(tabStyle.paddingHorizontal, tabStyle.paddingVertical, tabStyle.paddingHorizontal, tabStyle.paddingVertical);
            tabView.setTitleSize(tabStyle.titleSize);
            tabView.setIconSize(tabStyle.iconSize);
        } else {
            tabView = new TabView(getContext());
        }
        tabView.position = tabViews.size();
        boolean showIcon = false;
        if (iconRes == 0) {
            tabView.iconView.setVisibility(View.GONE);
        } else {
            tabView.iconView.setBackgroundResource(iconRes);
            showIcon = true;
        }
        if (title == null && showIcon) {
            tabView.titleView.setVisibility(View.GONE);
        } else {
            tabView.titleView.setText(title == null ? ("Tab" + tabViews.size()) : title);
            tabView.titleView.setTextColor(tabView.position == lastPosition ? tabStyle.titleSelectedColor : tabStyle.titleUnselectedColor);
        }
        contentLayout.addView(tabView);
        layoutTabView(tabView);
        tabViews.add(tabView);
        tabView.setOnClickListener(this);
    }

    public void removeTab(int position) {
        TabView tabView = tabViews.remove(position);
        contentLayout.removeView(tabView);
        recycleTabViews.add(tabView);
    }

    public void removeAllTabs() {
        recycleTabViews.addAll(tabViews);
        tabViews.clear();
        contentLayout.removeAllViews();
    }


    public void updateTab(int position, int iconRes, int titleRes) {
        updateTab(position, iconRes, titleRes == 0 ? null : getResources().getString(titleRes));
    }

    public void updateTab(int position, int iconRes, String title) {
        updateTab(tabViews.get(position), iconRes, title);
    }

    private void updateTab(TabView tabView, int iconRes, String title) {
        boolean showIcon = false;
        if (iconRes == 0) {
            tabView.iconView.setVisibility(View.GONE);
        } else {
            tabView.iconView.setBackgroundResource(iconRes);
            showIcon = true;
        }
        if (title == null && showIcon) {
            tabView.titleView.setVisibility(View.GONE);
        } else {
            tabView.titleView.setText(title == null ? ("Tab" + tabViews.size()) : title);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && tabViews.size() > 0 && tabStyle.showIndicator) {
            LayoutParams lp = (LayoutParams) indicatorView.getLayoutParams();
            if (tabStyle.horizontal) {
                lp.width = tabViews.get(lastPosition).getWidth();
                lp.leftMargin = 0;
                for (int i = 0; i < lastPosition; i++) {
                    lp.leftMargin += tabViews.get(i).getWidth();
                }
            } else {
                lp.height = tabViews.get(lastPosition).getHeight();
                lp.topMargin = 0;
                for (int i = 0; i < lastPosition; i++) {
                    lp.topMargin += tabViews.get(i).getHeight();
                }
            }
            indicatorView.setLayoutParams(lp);
        }
    }

    @Override
    public void onClick(View view) {
        if (view instanceof TabView) {
            TabView tabView = (TabView) view;
            int position = ((TabView) view).position;
            if (lastPosition != position) {
                if (tabClickListener == null || !tabClickListener.onTabClick(this, view, position)) {
                    setIndicatorOffset(position, 0);
                }
            } else {
                long time = SystemClock.elapsedRealtime();
                Long lastTime = lastClickTimeArray.get(position);
                if (time - (lastTime == null ? 0 : lastTime) < 400) {
                    lastClickTimeArray.put(position, Long.valueOf(0));
                    if (tabDoubleClickListener != null) {
                        tabDoubleClickListener.onTabDoubleClick(this, tabView, position);
                    }
                } else {
                    lastClickTimeArray.put(position, Long.valueOf(time));
                }
            }
        }
    }

    public void setOnTabSelectedListener(OnTabClickListener onTabClickListener) {
        tabClickListener = onTabClickListener;
    }

    public void setOnTabDoubleClickListener(OnTabDoubleClickListener onTabDoubleClickListener) {
        tabDoubleClickListener = onTabDoubleClickListener;
    }

    public void setIndicatorOffset(int position, float offset) {
        if (tabViews.size() > 0) {
            LayoutParams lp = (LayoutParams) indicatorView.getLayoutParams();
            if (tabStyle.horizontal) {
                lp.leftMargin = 0;
                for (int i = 0; i < position; i++) {
                    lp.leftMargin += tabViews.get(i).getWidth();
                }
                lp.leftMargin += tabViews.get(position).getWidth() * offset;
                if (tabStyle.wrapContent) {
                    HorizontalScrollView scrollView = (HorizontalScrollView) scrollLayout;
                    if (lp.leftMargin < scrollView.getScrollX()) {
                        scrollView.scrollTo(lp.leftMargin, 0);
                    } else if (lp.leftMargin + indicatorView.getWidth() > scrollView.getScrollX() + getWidth()) {
                        scrollView.scrollTo(lp.leftMargin + indicatorView.getWidth() - getWidth(), 0);
                    }
                }
            } else {
                lp.topMargin = 0;
                for (int i = 0; i < position; i++) {
                    lp.topMargin += tabViews.get(i).getHeight();
                }
                lp.topMargin += tabViews.get(position).getHeight() * offset;
                if (tabStyle.wrapContent) {
                    ScrollView scrollView = (ScrollView) scrollLayout;
                    if (lp.topMargin < scrollView.getScrollY()) {
                        scrollView.scrollTo(0, lp.topMargin);
                    } else if (lp.topMargin + indicatorView.getHeight() > scrollView.getScrollY() + getHeight()) {
                        scrollView.scrollTo(0, lp.topMargin + indicatorView.getHeight() - getHeight());
                    }
                }
            }
            indicatorView.setLayoutParams(lp);
            if (offset == 0.0f && position != lastPosition) {
                tabViews.get(lastPosition).titleView
                        .setTextColor(tabStyle.titleUnselectedColor);
                tabViews.get(position).titleView
                        .setTextColor(tabStyle.titleSelectedColor);
                tabViews.get(lastPosition).iconView.setSelected(false);
                tabViews.get(position).iconView.setSelected(true);
                lastPosition = position;
            } else {
                tabViews.get(position).titleView
                        .setTextColor(getColor(tabStyle.titleSelectedColor, tabStyle.titleUnselectedColor,
                                offset));
                if (offset > 0) {
                    tabViews.get(position + 1).titleView
                            .setTextColor(getColor(tabStyle.titleUnselectedColor, tabStyle.titleSelectedColor,
                                    offset));
                } else if (offset < 0) {
                    tabViews.get(position - 1).titleView
                            .setTextColor(getColor(tabStyle.titleUnselectedColor, tabStyle.titleSelectedColor,
                                    offset));
                }
            }
        }
    }

    private int getColor(int oldColor, int newColor, float offset) {
        int oldAlpha = Color.alpha(oldColor);
        int oldRed = Color.red(oldColor);
        int oldGreen = Color.green(oldColor);
        int oldBlue = Color.blue(oldColor);
        int newAlpha = Color.alpha(newColor);
        int newRed = Color.red(newColor);
        int newGreen = Color.green(newColor);
        int newBlue = Color.blue(newColor);
        return Color.argb((int) (oldAlpha + (newAlpha - oldAlpha) * offset),
                (int) (oldRed + (newRed - oldRed) * offset),
                (int) (oldGreen + (newGreen - oldGreen) * offset),
                (int) (oldBlue + (newBlue - oldBlue) * offset));
    }

    public interface OnTabClickListener {
        boolean onTabClick(TabLayout tabLayout, View view, int position);
    }

    public interface OnTabDoubleClickListener {
        void onTabDoubleClick(TabLayout tabLayout, View view, int position);
    }

    class TabView extends LinearLayout {

        private int position;
        private View iconView;
        private TextView titleView;

        public TabView(Context context) {
            super(context);
            setGravity(Gravity.CENTER);
            setPadding(tabStyle.paddingHorizontal, tabStyle.paddingVertical, tabStyle.paddingHorizontal, tabStyle.paddingVertical);
            iconView = new View(context);
            titleView = new TextView(context);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabStyle.titleSize);
            LayoutParams lp = new LayoutParams(tabStyle.iconSize, tabStyle.iconSize);
            if (tabStyle.iconAlign == 0) {
                setOrientation(HORIZONTAL);
                lp.rightMargin = tabStyle.iconMargin;
                addView(iconView, lp);
                addView(titleView);
            } else if (tabStyle.iconAlign == 1) {
                setOrientation(VERTICAL);
                lp.bottomMargin = tabStyle.iconMargin;
                addView(iconView, lp);
                addView(titleView);
            } else if (tabStyle.iconAlign == 2) {
                setOrientation(HORIZONTAL);
                lp.leftMargin = tabStyle.iconMargin;
                addView(titleView);
                addView(iconView, lp);
            } else if (tabStyle.iconAlign == 3) {
                setOrientation(VERTICAL);
                lp.topMargin = tabStyle.iconMargin;
                addView(titleView);
                addView(iconView, lp);
            }
        }

        public void setIconSize(int size) {
            LayoutParams lp = (LayoutParams) iconView.getLayoutParams();
            lp.weight = size;
            lp.height = size;
            iconView.setLayoutParams(lp);
        }

        public void setTitleSize(int size) {
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    public class TabStyle {

        private int iconSize;
        private int titleSize;
        private int iconAlign;
        private int iconMargin;
        private boolean horizontal;
        private int indicatorAlign;
        private int indicatorSize;
        private boolean wrapContent;
        private boolean showIndicator;
        private int paddingVertical;
        private int paddingHorizontal;
        private int indicatorColor;
        private int titleSelectedColor;
        private int titleUnselectedColor;

        public TabStyle setWrapContent(boolean wrapContent) {
            if (this.wrapContent != wrapContent) {
                this.wrapContent = wrapContent;
                updateLayout();
            }
            return this;
        }

        public TabStyle setIconSize(int iconSize) {
            this.iconSize = iconSize;
            for (TabView tabView : tabViews) {
                tabView.setIconSize(iconSize);
            }
            return this;
        }

        public TabStyle setTitleSize(int titleSize) {
            this.titleSize = titleSize;
            for (TabView tabView : tabViews) {
                tabView.setTitleSize(titleSize);
            }
            return this;
        }

        public TabStyle setPadding(int paddingVertical, int paddingHorizontal) {
            this.paddingVertical = paddingVertical;
            this.paddingHorizontal = paddingHorizontal;
            for (TabView tabView : tabViews) {
                tabView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
            }
            return this;
        }

        public TabStyle setIndicatorStyle(int indicatorAlign, int indicatorSize) {
            this.indicatorAlign = indicatorAlign;
            this.indicatorSize = indicatorSize;
            LayoutParams lp = (LayoutParams) indicatorView.getLayoutParams();
            if (tabStyle.horizontal) {
                lp.height = indicatorSize;
            } else {
                lp.width = indicatorSize;
            }
            if (tabStyle.indicatorAlign == 0) {
                lp.gravity = tabStyle.horizontal ? Gravity.TOP : Gravity.LEFT;
            } else {
                lp.gravity = tabStyle.horizontal ? Gravity.BOTTOM : Gravity.RIGHT;
            }
            indicatorView.setLayoutParams(lp);
            return this;
        }

        public TabStyle setShowIndicator(boolean showIndicator) {
            this.showIndicator = showIndicator;
            indicatorView.setVisibility(showIndicator ? VISIBLE : INVISIBLE);
            return this;
        }

        public TabStyle setIndicatorColor(int indicatorColor) {
            this.indicatorColor = indicatorColor;
            indicatorView.setBackgroundColor(indicatorColor);
            return this;
        }

        public TabStyle setTitleSelectedColor(int titleSelectedColor) {
            this.titleSelectedColor = titleSelectedColor;
            if (tabViews.size() > 0) {
                for (int i = 0; i < tabViews.size(); i++) {
                    if (i == lastPosition) {
                        tabViews.get(i).titleView
                                .setTextColor(titleSelectedColor);
                    }
                }
            }
            return this;
        }

        public TabStyle setTitleUnselectedColor(int titleUnselectedColor) {
            this.titleUnselectedColor = titleUnselectedColor;
            if (tabViews.size() > 0) {
                for (int i = 0; i < tabViews.size(); i++) {
                    if (i != lastPosition) {
                        tabViews.get(i).titleView
                                .setTextColor(titleUnselectedColor);
                    }
                }
            }
            return this;
        }
    }
}
