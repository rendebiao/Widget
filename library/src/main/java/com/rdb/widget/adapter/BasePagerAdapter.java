package com.rdb.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePagerAdapter<T> extends PagerAdapter {

    private int layoutId;
    private List<T> list;
    private Context context;
    private LayoutInflater inflater;
    private List<PageItemHolder> holders = new ArrayList<>();
    private List<PageItemHolder> recycleHolders = new ArrayList<>();

    public BasePagerAdapter(Context context, List<T> list, int layoutId) {
        this.context = context;
        this.list = list;
        this.layoutId = layoutId;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public final int getCount() {
        return (list == null || list.size() == 0) ? 0 : list.size();
    }

    @Override
    public final Object instantiateItem(ViewGroup container, int position) {
        PageItemHolder itemHolder;
        if (recycleHolders.isEmpty()) {
            itemHolder = new PageItemHolder(inflatePage(inflater, container, layoutId));
        } else {
            itemHolder = recycleHolders.remove(0);
        }
        itemHolder.setLastPosition(position);
        updateView(position, itemHolder, list.get(position));
        container.addView(itemHolder.getItemView(), 0);
        holders.add(itemHolder);
        return itemHolder;
    }

    protected View inflatePage(LayoutInflater inflater, ViewGroup parent, int layoutId) {
        return inflater.inflate(layoutId, parent, false);
    }

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        PageItemHolder itemHolder = (PageItemHolder) object;
        container.removeView(itemHolder.getItemView());
        holders.remove(itemHolder);
        recycleHolders.add(itemHolder);
    }

    @Override
    public final boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public abstract void updateView(int position, PageItemHolder itemHolder, T data);

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (list.size() > 0) {
            for (int i = 0; i < holders.size(); i++) {
                int position = holders.get(i).getLastPosition();
                updateView(position, holders.get(i), list.get(position));
            }
        }
    }

    public Context getAdapterContext() {
        return context;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public class PageItemHolder extends BaseItemHolder {

        private int lastPosition;

        public PageItemHolder(View itemView) {
            super(itemView);
        }

        public int getLastPosition() {
            return lastPosition;
        }

        protected void setLastPosition(int lastPosition) {
            this.lastPosition = lastPosition;
        }
    }
}
