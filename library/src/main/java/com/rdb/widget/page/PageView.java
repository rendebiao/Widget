package com.rdb.widget.page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PageView extends View {

    private int width;
    private int height;
    private final Paint paint;
    private float moveX;
    private float downX;
    private final float density;
    private boolean loadNext;
    private boolean inMove;
    private final PageManager pageManager;
    private PageAdapter pageAdapter;
    private PageTransition pageTransition;
    private OnCenterClickListener centerClickListener;
    private final PageDataObserver pageDataObserver = new PageDataObserver() {
        @Override
        public void onChanged() {
            if (pageAdapter != null && canInvalidate()) {
                pageAdapter.drawPage(PageIndex.CUR_PAGE, pageManager.getCurPage().getCanvas());
                postInvalidate();
            }
        }
    };

    public PageView(Context context) {
        this(context, null);
    }    private final Runnable mRunnable = new Runnable() {
        float mLastX;

        @Override
        public void run() {
            if (moveX != mLastX) {
                mLastX = moveX;
                float moveX = Math.abs(PageView.this.moveX);
                if (moveX == width) {
                    if (PageView.this.moveX > 0) {
                        pageAdapter.movePage(PageIndex.PRE_PAGE);
                    } else {
                        pageAdapter.movePage(PageIndex.NEXT_PAGE);
                    }
                    PageView.this.moveX = 0;
                    pageManager.switchPage();
                    inMove = false;
                    invalidate();
                } else {
                    invalidate();
                    float move = width / 15;
                    if (moveX >= width / 5) {
                        inMove = true;
                        if (PageView.this.moveX > 0) {
                            PageView.this.moveX = Math.min(PageView.this.moveX + move, width);
                        } else {
                            PageView.this.moveX = Math.max(PageView.this.moveX - move, -width);
                        }
                    } else {
                        if (PageView.this.moveX > 0) {
                            PageView.this.moveX = Math.max(PageView.this.moveX - move, 0);
                        } else {
                            PageView.this.moveX = Math.min(PageView.this.moveX + move, 0);
                        }
                    }
                    postDelayed(mRunnable, 5);
                }
            }
        }
    };

    public PageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        density = getContext().getResources().getDisplayMetrics().density;
        pageManager = new PageManager();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            width = right - left;
            height = bottom - top;
            pageManager.updateSize(width, height);
            if (pageAdapter != null) {
                pageAdapter.drawPage(PageIndex.CUR_PAGE, pageManager.getCurPage().getCanvas());
                postInvalidate();
            }
        }
    }

    public void setPageTransition(PageTransition pageTransition) {
        this.pageTransition = pageTransition;
        postInvalidate();
    }

    public void setPageAdapter(PageAdapter pageAdapter) {
        if (this.pageAdapter != null) {
            this.pageAdapter.unregisterDataSetObserver(pageDataObserver);
        }
        this.pageAdapter = pageAdapter;
        if (this.pageAdapter != null) {
            this.pageAdapter.registerDataSetObserver(pageDataObserver);
            if (canInvalidate()) {
                this.pageAdapter.drawPage(PageIndex.CUR_PAGE, pageManager.getCurPage().getCanvas());
                postInvalidate();
            }
        }
    }

    private boolean canInvalidate() {
        return width > 0 && height > 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (inMove) return false;
        removeCallbacks(mRunnable);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            moveX = 0;
            loadNext = false;
            downX = event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float moveX = event.getX() - downX;
            if (Math.abs(moveX) > 8 * density) {
                if (this.moveX == 0) {
                    PageIndex page = moveX > 0 ? PageIndex.PRE_PAGE : PageIndex.NEXT_PAGE;
                    loadNext = pageAdapter.hasPage(page);
                    if (loadNext) {
                        pageAdapter.drawPage(page, pageManager.getNextPage().getCanvas());
                    }
                }
            }
            if (loadNext) {
                this.moveX = moveX;
                postInvalidate();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (loadNext) {
                moveX = event.getX() - downX;
            } else {
                float moveX = event.getX() - downX;
                if (Math.abs(moveX) <= 8 * density) {
                    if (downX < width / 3) {
                        if (pageAdapter.hasPage(PageIndex.PRE_PAGE)) {
                            pageAdapter.drawPage(PageIndex.PRE_PAGE, pageManager.getNextPage().getCanvas());
                        }
                        this.moveX = width / 5;
                        loadNext = true;
                    } else if (downX < width * 2 / 3) {
                        if (centerClickListener != null) {
                            centerClickListener.onCenterClick(this);
                        }
                    } else {
                        if (pageAdapter.hasPage(PageIndex.NEXT_PAGE)) {
                            pageAdapter.drawPage(PageIndex.NEXT_PAGE, pageManager.getNextPage().getCanvas());
                        }
                        this.moveX = -width / 5;
                        loadNext = true;
                    }
                }
            }
            if (loadNext) {
                post(mRunnable);
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canInvalidate()) {
            if (pageTransition == null) {
                pageTransition = new SimplePageTransition(TransitionType.SLIDE);
            }
            if (Math.abs(moveX) > 0) {
                pageTransition.onTransition(canvas, paint, pageManager.getNextPage().getBitmap(), pageManager.getCurPage().getBitmap(), moveX);
            } else {
                canvas.drawBitmap(pageManager.getCurPage().getBitmap(), 0, 0, paint);
            }
        }
    }

    public void setOnCenterClickListener(OnCenterClickListener onCenterClickListener) {
        centerClickListener = onCenterClickListener;
    }

    public interface OnCenterClickListener {
        void onCenterClick(PageView view);
    }


}
