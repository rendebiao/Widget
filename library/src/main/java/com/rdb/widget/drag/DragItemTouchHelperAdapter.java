package com.rdb.widget.drag;

interface DragItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    boolean onItemDragable(int position);
}
