package com.rdb.widget.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;

import com.rdb.widget.AdapterProxy;
import com.rdb.widget.R;

import java.util.ArrayList;
import java.util.List;

public class GridView extends LinearLayout implements Adaptable<GridAdapter>, OnClickListener, OnLongClickListener {

    public static final int DIVIDER_ALL = 1;
    public static final int DIVIDER_VERTICAL = 2;
    public static final int DIVIDER_HORIZONTAL = 4;
    public static final int DIVIDER_LEFT = 8;
    public static final int DIVIDER_TOP = 16;
    public static final int DIVIDER_RIGHT = 32;
    public static final int DIVIDER_BOTTOM = 64;
    private int numColumns;
    private int dividerSize;
    private int dividerColor;
    private boolean dividerLeft;
    private boolean dividerTop;
    private boolean dividerRight;
    private boolean dividerBottom;
    private boolean dividerVertical;
    private boolean dividerHorizontal;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;
    private List<Divider> recycleDividers = new ArrayList<>();
    private List<RowView> recycleRowViews = new ArrayList<>();
    private List<EmptyView> recycleEmptyViews = new ArrayList<>();
    private SparseArray<List<FrameView>> recycleFrameViews = new SparseArray<>();

    public GridView(Context context) {
        this(context, null);
    }

    public GridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }    private AdapterProxy<GridAdapter> adapterProxy = new AdapterProxy<GridAdapter>() {
        @Override
        protected void onDataSetChanged() {
            updateViews();
        }
    };

    public GridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GridView,
                defStyleAttr, 0);
        numColumns = typedArray.getInt(R.styleable.GridView_gridNumColumns, 1);
        dividerSize = typedArray.getDimensionPixelSize(R.styleable.GridView_gridDividerSize, 0);
        dividerColor = typedArray.getColor(R.styleable.GridView_gridDivider, Color.TRANSPARENT);
        int dividerMode = typedArray.getInt(R.styleable.GridView_gridDividerMode, 0);
        if ((dividerMode & DIVIDER_ALL) == DIVIDER_ALL) {
            dividerTop = true;
            dividerBottom = true;
            dividerLeft = true;
            dividerRight = true;
            dividerVertical = true;
            dividerHorizontal = true;
        } else {
            if ((dividerMode & DIVIDER_VERTICAL) == DIVIDER_VERTICAL) {
                dividerVertical = true;
            }
            if ((dividerMode & DIVIDER_TOP) == DIVIDER_TOP) {
                dividerTop = true;
            }
            if ((dividerMode & DIVIDER_BOTTOM) == DIVIDER_BOTTOM) {
                dividerBottom = true;
            }
            if ((dividerMode & DIVIDER_HORIZONTAL) == DIVIDER_HORIZONTAL) {
                dividerHorizontal = true;
            }
            if ((dividerMode & DIVIDER_LEFT) == DIVIDER_LEFT) {
                dividerLeft = true;
            }
            if ((dividerMode & DIVIDER_RIGHT) == DIVIDER_RIGHT) {
                dividerRight = true;
            }
        }
        typedArray.recycle();
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
    }

    public void setDividerMode(boolean left, boolean top, boolean right, boolean bottom, boolean vertical, boolean horizontal) {
        dividerTop = top;
        dividerBottom = bottom;
        dividerLeft = left;
        dividerRight = right;
        dividerVertical = vertical;
        dividerHorizontal = horizontal;
    }

    @Override
    public void onClick(View view) {
        if (view instanceof FrameView) {
            if (itemClickListener != null) {
                int position = ((FrameView) view).position;
                itemClickListener.onItemClick(this, view, position);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view instanceof FrameView) {
            if (itemLongClickListener != null) {
                int position = ((FrameView) view).position;
                return itemLongClickListener.onItemLongClick(this, view, position);
            }
        }
        return false;
    }

    private void updateViews() {
        recycleView();
        int count = adapterProxy.getAdapter().getCount();
        if (count > 0) {
            int row = count / numColumns + (count % numColumns > 0 ? 1 : 0);
            LayoutParams lp;
            RowView rowView;
            FrameView frameView;
            for (int i = 0; i < row; i++) {
                if (dividerSize > 0 && ((i == 0 && dividerTop) || (i > 0 && dividerHorizontal))) {
                    insertDividerView(true, this);
                }
                rowView = getRowView();
                for (int j = 0; j < numColumns && i * numColumns + j < count; j++) {
                    if (dividerSize > 0 && ((j == 0 && dividerLeft) || (j > 0 && dividerVertical))) {
                        insertDividerView(false, rowView);
                    }
                    lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
                    lp.weight = 1;
                    if (i * numColumns + j < count) {
                        int position = i * numColumns + j;
                        frameView = getFrameView(adapterProxy.getAdapter().getItemViewType(position));
                        View view = adapterProxy.getAdapter().getView(position, frameView.getContentView(), frameView);
                        frameView.setContentView(view);
                        frameView.position = position;
                        frameView.enableClick(itemClickListener != null || itemLongClickListener != null);
                        rowView.addView(frameView, lp);
                    } else {
                        lp.height = 1;
                        rowView.addView(getEmptyView(), lp);
                    }
                    if (dividerSize > 0 && (j == numColumns - 1 || i * numColumns + j == count - 1) && dividerRight) {
                        insertDividerView(false, rowView);
                    }
                }
                lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                addView(rowView, lp);
            }
            if (dividerSize > 0 && dividerBottom) {
                insertDividerView(true, this);
            }
        }
    }

    private void insertDividerView(boolean vertical, LinearLayout parent) {
        LayoutParams lp = new LayoutParams(vertical ? LayoutParams.MATCH_PARENT : dividerSize,
                vertical ? dividerSize : LayoutParams.MATCH_PARENT);
        parent.addView(getDivider(), lp);
    }

    private RowView getRowView() {
        RowView rowView;
        if (recycleRowViews.isEmpty()) {
            rowView = new RowView(getContext());
        } else {
            rowView = recycleRowViews.remove(0);
        }
        return rowView;
    }

    private FrameView getFrameView(int itemType) {
        FrameView frameView;
        List<FrameView> frameViews = recycleFrameViews.get(itemType);
        if (frameViews == null || frameViews.isEmpty()) {
            frameView = new FrameView(getContext(), itemType);
        } else {
            frameView = frameViews.remove(0);
        }
        return frameView;
    }

    private EmptyView getEmptyView() {
        EmptyView emptyView;
        if (recycleEmptyViews.isEmpty()) {
            emptyView = new EmptyView(getContext());
        } else {
            emptyView = recycleEmptyViews.remove(0);
        }
        return emptyView;
    }

    private Divider getDivider() {
        Divider divider;
        if (recycleDividers.isEmpty()) {
            divider = new Divider(getContext());
            divider.setBackgroundColor(dividerColor);
        } else {
            divider = recycleDividers.remove(0);
        }
        return divider;
    }

    private void recycleView() {
        while (getChildCount() > 0) {
            View layout = getChildAt(0);
            if (layout instanceof RowView) {
                RowView rowView = (RowView) layout;
                while (rowView.getChildCount() > 0) {
                    View view = rowView.getChildAt(0);
                    if (view instanceof FrameView) {
                        FrameView frameView = (FrameView) view;
                        List<FrameView> frameViews = recycleFrameViews.get(frameView.itemType);
                        if (frameViews == null) {
                            frameViews = new ArrayList<>();
                            recycleFrameViews.put(frameView.itemType, frameViews);
                        }
                        frameViews.add((FrameView) view);
                    } else if (view instanceof Divider) {
                        recycleDividers.add((Divider) view);
                    } else if (view instanceof EmptyView) {
                        recycleEmptyViews.add((EmptyView) view);
                    }
                    rowView.removeView(view);
                }
                rowView.removeAllViews();
                recycleRowViews.add(rowView);
            } else if (layout instanceof Divider) {
                recycleDividers.add((Divider) layout);
            }
            removeView(layout);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        itemClickListener = onItemClickListener;
        if (adapterProxy.getAdapter() != null) {
            adapterProxy.getAdapter().notifyDataSetChanged();
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        itemLongClickListener = onItemLongClickListener;
        if (adapterProxy.getAdapter() != null) {
            adapterProxy.getAdapter().notifyDataSetChanged();
        }
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public GridAdapter getAdapter() {
        return adapterProxy.getAdapter();
    }

    @Override
    public void setAdapter(GridAdapter adapter) {
        adapterProxy.setAdapter(adapter);
    }

    @Override
    public AdapterProxy getAdapterProxy() {
        return adapterProxy;
    }

    public int getDividerSize() {
        return dividerSize;
    }

    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
    }

    public interface OnItemClickListener {
        void onItemClick(GridView parent, View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(GridView parent, View view, int position);
    }

    class EmptyView extends View {

        public EmptyView(Context context) {
            super(context);
            setVisibility(View.INVISIBLE);
        }
    }

    class RowView extends LinearLayout {

        public RowView(Context context) {
            super(context);
        }
    }

    class Divider extends View {

        public Divider(Context context) {
            super(context);
        }
    }

    class FrameView extends LinearLayout {

        private int itemType;
        private int position;

        public FrameView(Context context, int itemType) {
            super(context);
            this.itemType = itemType;
        }

        public View getContentView() {
            return getChildAt(0);
        }

        public void setContentView(View view) {
            removeAllViews();
            addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        public void enableClick(boolean enable) {
            setOnClickListener(enable ? GridView.this : null);
            setOnLongClickListener(enable ? GridView.this : null);
            setClickable(enable);
            setLongClickable(enable);
        }
    }




}
