package com.rdb.widget.drag;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class DragRecyclerView extends RecyclerView {

    private ItemTouchHelper itemTouchHelper;

    public DragRecyclerView(Context context) {
        super(context);
    }

    public DragRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter != null) {
            if (adapter instanceof DragRecyclerAdapter) {
                setDragAdapter((DragRecyclerAdapter) adapter, true);
            } else {
                throw new RuntimeException("the adapter must instanceof DragRecyclerAdapter");
            }
        }
    }

    public void setDragAdapter(DragRecyclerAdapter adapter, boolean draggable) {
        super.setAdapter(adapter);
        itemTouchHelper = adapter.getItemTouchHelper();
        if (draggable) {
            itemTouchHelper.attachToRecyclerView(this);
        }
    }

    public void setDraggable(boolean draggable) {
        itemTouchHelper.attachToRecyclerView(draggable ? this : null);
    }
}
