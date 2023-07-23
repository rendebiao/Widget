package com.rdb.widget.page;

import android.graphics.Canvas;

import java.util.HashSet;

public abstract class PageAdapter {

    private final HashSet<PageDataObserver> observers = new HashSet<>();

    public void registerDataSetObserver(PageDataObserver observer) {
        observers.add(observer);
    }

    public void unregisterDataSetObserver(PageDataObserver observer) {
        observers.remove(observer);
    }

    protected abstract void drawPage(PageIndex index, Canvas canvas);

    protected abstract void movePage(PageIndex index);

    protected abstract boolean hasPage(PageIndex index);

    public void notifyDataSetChanged() {
        for (PageDataObserver observer : observers) {
            observer.onChanged();
        }
    }
}
