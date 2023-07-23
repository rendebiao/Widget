package com.rdb.widget.drag;

import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class DragRecyclerAdapter<T, K extends DragItemViewHolder> extends RecyclerView.Adapter<K>
        implements DragItemTouchHelperAdapter {

    private List<T> items;
    private boolean isLongPressDragEnabled;
    private ItemTouchHelper itemTouchHelper;

    public DragRecyclerAdapter(List<T> items) {
        this(items, true, true);
    }

    public DragRecyclerAdapter(List<T> items, boolean isLongPressDragEnabled, boolean isItemViewSwipeEnabled) {
        this.items = items;
        this.isLongPressDragEnabled = isLongPressDragEnabled;
        ItemTouchHelper.Callback callback = new DragItemTouchHelperCallback(this, isLongPressDragEnabled, isItemViewSwipeEnabled);
        itemTouchHelper = new ItemTouchHelper(callback);
    }

    @Override
    public final void onBindViewHolder(final K holder, final int position) {
        onBindDragViewHolder(holder, position);
        View swipView = getSwipeView(holder, position);
        if (swipView != null && !isLongPressDragEnabled) {
            swipView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemDragable(position)) {
                        itemTouchHelper.startDrag(holder);
                    }
                    return true;
                }
            });
        }
    }

    protected abstract void onBindDragViewHolder(K holder, int position);

    protected View getSwipeView(K holder, int position) {
        return holder.itemView;
    }

    public ItemTouchHelper getItemTouchHelper() {
        return itemTouchHelper;
    }

    @Override
    public void onItemDismiss(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition >= 0 && fromPosition < items.size() && toPosition >= 0 && toPosition < items.size()) {
            T t = items.remove(fromPosition);
            items.add(toPosition, t);
            notifyItemMoved(fromPosition, toPosition);
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
