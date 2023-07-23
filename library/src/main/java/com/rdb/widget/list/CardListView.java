package com.rdb.widget.list;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.rdb.widget.AdapterProxy;
import com.rdb.widget.animator.FloatValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by DB on 2017/6/2.
 */

public class CardListView extends ViewGroup implements Adaptable<CardAdapter> {

    private float downY;
    private float density;
    private int minHeight;
    private int curHeight;
    private int cardCount;
    private int cardHeight;
    private int maxScrollY;
    private int cardShowCount;
    private float mTouchSlop;
    private int scrollHeight;
    private int firstPosition;
    private int lastPosition;
    private boolean dataSetChanged;
    private ScrollBar scrollBar;
    private GestureDetectorCompat mDetector;
    private OnItemClickListener itemClickListener;
    private FloatValue scrollValue = new FloatValue();
    private List<Card> recycles = new ArrayList<>();
    private Set<Integer> keyList = new HashSet<>();
    private Map<Integer, Card> cardMap = new HashMap<>();
    private ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);

    public CardListView(Context context) {
        super(context);
        init();
    }

    public CardListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }    private AdapterProxy<CardAdapter> adapterProxy = new AdapterProxy<CardAdapter>() {
        @Override
        protected void onDataSetChanged() {
            cardHeight = adapterProxy.getAdapter().getCardHeight();
            cardCount = adapterProxy.getAdapter().getCardCount();
            cardShowCount = Math.max(Math.min(adapterProxy.getAdapter().getShowCardCount(), cardCount), 3);
            minHeight = cardHeight * cardShowCount;
            scrollHeight = cardCount * cardHeight;
            dataSetChanged = true;
            post(updateCardsRunnable);
        }
    };

    public CardListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        density = getResources().getDisplayMetrics().density;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mDetector = new GestureDetectorCompat(getContext(), new MyGestureListener());
        valueAnimator.addUpdateListener(updateListener);
        valueAnimator.setInterpolator(new DecelerateInterpolator(2));
        valueAnimator.setDuration(500);
        scrollBar = new ScrollBar(getContext());

        TypedArray array = getContext().getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.scrollbarThumbVertical,
                android.R.attr.scrollbarSize
        });
        Drawable scrollbarThumbVertical = array.getDrawable(0);
        scrollBar.setBackgroundDrawable(scrollbarThumbVertical);
        scrollBar.width = array.getDimensionPixelSize(1, 0);
        scrollBar.margin = (int) density;
        addView(scrollBar);
    }    private Runnable updateCardsRunnable = new Runnable() {
        @Override
        public void run() {
            if (cardCount > 0) {
                updatePosition();
                Iterator<Integer> iterator = keyList.iterator();
                while (iterator.hasNext()) {
                    int position = iterator.next();
                    if (dataSetChanged || position < firstPosition || position > lastPosition) {
                        Card card = cardMap.remove(position);
                        removeView(card);
                        recycles.add(card);
                        iterator.remove();
                        cardMap.remove(position);
                    }
                }
                for (int i = firstPosition; i < lastPosition + 1; i++) {
                    if (!cardMap.containsKey(i)) {
                        Card card = getCard();
                        View view = adapterProxy.getAdapter().getView(i, card.getContentView(), card);
                        card.setContentView(view);
                        card.position = i;
                        card.update();
                        addView(card, new LayoutParams(LayoutParams.MATCH_PARENT, cardHeight));
                        cardMap.put(i, card);
                        keyList.add(i);
                    }
                    cardMap.get(i).update();
                }
                if (firstPosition + 1 <= lastPosition) {
                    cardMap.get(firstPosition + 1).bringToFront();
                }
                if (lastPosition - 1 > firstPosition + 1) {
                    cardMap.get(lastPosition - 1).bringToFront();
                }

            }
            scrollBar.bringToFront();
            requestLayout();
            dataSetChanged = false;
        }
    };

    @Override
    public CardAdapter getAdapter() {
        return adapterProxy.getAdapter();
    }

    @Override
    public void setAdapter(CardAdapter adapter) {
        adapterProxy.setAdapter(adapter);
    }    private ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            scrollValue.updateAnimatorValue(value);
            updateCardsRunnable.run();
        }
    };

    @Override
    public AdapterProxy getAdapterProxy() {
        return adapterProxy;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return this.mDetector.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                this.mDetector.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getY() - downY) > mTouchSlop) {
                    return true;
                } else {
                    this.mDetector.onTouchEvent(ev);
                }
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            curHeight = b - t - getPaddingBottom() - getPaddingTop();
            maxScrollY = scrollHeight - curHeight;
            if (scrollValue.getCurValue() > maxScrollY) {
                scrollValue.moveTo(maxScrollY, false);
            }
            post(updateCardsRunnable);
        } else {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof Card) {
                    Card card = (Card) child;
                    card.layout(getPaddingLeft(), card.y, r - getPaddingRight() - l, card.y + cardHeight);
                } else if (child instanceof ScrollBar) {
                    ScrollBar scrollBar = (ScrollBar) child;
                    scrollBar.update();
                    scrollBar.layout(r - getPaddingRight() - l - scrollBar.width - scrollBar.margin, scrollBar.top, r - getPaddingRight() - l - scrollBar.margin, scrollBar.bottom);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureDimension(0, widthMeasureSpec);
        int height = Math.max(measureDimension(minHeight, heightMeasureSpec), minHeight);
        setMeasuredDimension(width, height);
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;   //UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private void updatePosition() {
        boolean getFirst = false;
        for (int i = 0; i < cardCount; i++) {
            if (!getFirst) {
                firstPosition = i;
                if (cardHeight * i - scrollValue.getCurValue() == 0) {
                    getFirst = true;
                } else if (cardHeight * i - scrollValue.getCurValue() > 0) {
                    getFirst = true;
                    firstPosition--;
                }
            } else {
                lastPosition = i;
                if (cardHeight * i - scrollValue.getCurValue() > curHeight) {
                    lastPosition--;
                    break;
                }
            }
        }
    }

    private Card getCard() {
        Card card;
        if (recycles.size() > 0) {
            card = recycles.remove(0);
        } else {
            card = new Card(getContext());
        }
        return card;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            float scrollYTo = 0;
            if (velocityY < 0) {
                scrollYTo = Math.min(maxScrollY, scrollValue.getCurValue() - velocityY * 0.2f);
            } else if (velocityY > 0) {
                scrollYTo = Math.max(0, scrollValue.getCurValue() - velocityY * 0.2f);
            }
            scrollValue.moveTo(scrollYTo, true);
            valueAnimator.start();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (distanceY > 0) {
                scrollValue.moveTo(Math.min(maxScrollY, scrollValue.getCurValue() + distanceY), false);
            } else if (distanceY < 0) {
                scrollValue.moveTo(Math.max(0, scrollValue.getCurValue() + distanceY), false);
            }
            updateCardsRunnable.run();
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    private class Card extends LinearLayout implements OnClickListener {

        private int y;
        private int position;

        public Card(@NonNull Context context) {
            super(context);
            setOnClickListener(this);
        }

        public Card(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            setOnClickListener(this);
        }

        public Card(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setOnClickListener(this);
        }

        public void update() {
            y = (int) (cardHeight * position - scrollValue.getCurValue());
            if (y < 0) {
                y = 0;
            } else if (y > curHeight - cardHeight) {
                y = curHeight - cardHeight;
            }
            y += CardListView.this.getPaddingTop();
        }

        public View getContentView() {
            return getChildAt(0);
        }

        public void setContentView(View view) {
            removeAllViews();
            addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(position);
            }
        }
    }

    private class ScrollBar extends View {

        private int top;
        private int bottom;
        private int width;
        private int margin;

        public ScrollBar(@NonNull Context context) {
            super(context);
        }

        public ScrollBar(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public ScrollBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public void update() {
            float h = cardShowCount * curHeight / cardCount;
            top = (int) (scrollValue.getCurValue() / maxScrollY * (curHeight - h)) + CardListView.this.getPaddingTop();
            bottom = (int) (top + h);
        }
    }








}
