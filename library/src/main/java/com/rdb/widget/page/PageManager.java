package com.rdb.widget.page;

import java.util.ArrayList;
import java.util.List;

public class PageManager {

    private final List<Page> pages = new ArrayList<>();

    public PageManager() {
        pages.add(new Page());
        pages.add(new Page());
    }

    public Page getCurPage() {
        return pages.get(0);
    }

    public Page getNextPage() {
        return pages.get(1);
    }

    public void switchPage() {
        Page page = pages.remove(0);
        pages.add(page);
    }

    public void updateSize(int width, int height) {
        pages.get(0).updateSize(width, height);
        pages.get(1).updateSize(width, height);
    }
}
