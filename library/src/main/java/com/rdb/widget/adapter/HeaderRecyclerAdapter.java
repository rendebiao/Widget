package com.rdb.widget.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HeaderRecyclerAdapter extends RecyclerView.Adapter {

    private final RecyclerView.Adapter mAdapter;
    private final int SPLITE_POSITON = Integer.MIN_VALUE / 2;
    private List<View> headerViews = new ArrayList<>();
    private List<View> footerViews = new ArrayList<>();

    public HeaderRecyclerAdapter(RecyclerView.Adapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException();
        }
        mAdapter = adapter;
        setHasStableIds(adapter.hasStableIds());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType < SPLITE_POSITON) {
            viewHolder = new FixedViewHolder(headerViews.get(SPLITE_POSITON - viewType));
        } else if (viewType < 0) {
            viewHolder = new FixedViewHolder(footerViews.get(viewType - SPLITE_POSITON));
        } else {
            viewHolder = mAdapter.onCreateViewHolder(parent, viewType);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof FixedViewHolder)) {
            int adjPosition = position - headerViews.size();
            mAdapter.onBindViewHolder(holder, adjPosition);
        }
    }

    @Override
    public int getItemCount() {
        return headerViews.size() + footerViews.size() + mAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        final int numHeaderView = headerViews.size();
        if (position < numHeaderView) {
            return SPLITE_POSITON - position;
        }
        final int adjPosition = position - numHeaderView;
        final int itemCount = mAdapter.getItemCount();
        if (adjPosition >= itemCount) {
            return SPLITE_POSITON + adjPosition - itemCount;
        }
        int itemViewType = mAdapter.getItemViewType(adjPosition);
        if (itemViewType < 0) {
            throw new IllegalArgumentException("Invalid item view type: RecyclerView.Adapter.getItemViewType return " + itemViewType);
        }
        return itemViewType;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        mAdapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        mAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof FixedViewHolder) return;
        mAdapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof FixedViewHolder) return;
        mAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        if (holder instanceof FixedViewHolder) return false;
        return mAdapter.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof FixedViewHolder) return;
        mAdapter.onViewRecycled(holder);
    }

    @Override
    public long getItemId(int position) {
        int adjPosition = position - headerViews.size();
        if (adjPosition >= 0 && adjPosition < mAdapter.getItemCount())
            return mAdapter.getItemId(adjPosition);

        return RecyclerView.NO_ID;
    }

    private boolean isFixedViewType(int viewType) {
        return viewType < 0;
    }

    public void addHeaderView(View view) {
        if (headerViews.add(view)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void removeHeaderView(View view) {
        if (headerViews.remove(view)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void addFooterView(View view) {
        if (footerViews.add(view)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void removeFooterView(View view) {
        if (footerViews.remove(view)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public static class FixedViewHolder extends RecyclerView.ViewHolder {

        public FixedViewHolder(View itemView) {
            super(itemView);
            setIsRecyclable(false);
        }
    }
}
