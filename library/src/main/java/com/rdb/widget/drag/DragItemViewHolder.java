package com.rdb.widget.drag;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class DragItemViewHolder extends RecyclerView.ViewHolder implements
        DragItemTouchHelperViewHolder {

    public DragItemViewHolder(View itemView) {
        super(itemView);
    }
}