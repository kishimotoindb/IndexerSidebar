package com.example.bear.indexersidebar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class IndexerSideBar extends LinearLayout {
    private static final String[] INDEXERS =
            {
                    "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                    "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
            };
    private static final int UNFOCUSED_INDEX = 0;
    private static final String FOCUSED_TEXT_COLOR = "#f09a37";
    private static final String UNFOCUSED_TEXT_COLOR = "##999999";
    private static final float TEXT_SCALE_FACTOR = 0.8f;

    public IndexerSideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout();
        initIndexer();
    }

    private void initLayout() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
    }

    private void initIndexer() {
        for (String indexer : INDEXERS) {
            TextView indexerView = new TextView(getContext());
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.height = 0;
            layoutParams.weight = 1;
            indexerView.setLayoutParams(layoutParams);
            indexerView.setPadding(dp2px(5), 0, dp2px(5), 0);
            indexerView.setGravity(Gravity.CENTER);
            indexerView.setText(indexer);
            indexerView.setTextColor(Color.parseColor(UNFOCUSED_TEXT_COLOR));
            addView(indexerView);
        }
    }

    private float[] mBounds;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mBounds == null) {
            mBounds = new float[INDEXERS.length];
            View child;
            for (int i = 0; i < getChildCount(); i++) {
                child = getChildAt(i);
                mBounds[i] = child.getBottom();
            }

            for (int i = 0; i < getChildCount(); i++) {
                ((TextView) getChildAt(i)).setTextSize(px2sp((int) ((mBounds[1] - mBounds[0]) * TEXT_SCALE_FACTOR)));
            }
        }

        super.dispatchDraw(canvas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    private int mFocusedChildIndex = UNFOCUSED_INDEX;
    private int mOldHighLightChildIndex = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float y = event.getY();
        switch (event.getActionMasked()) {
            case ACTION_DOWN:
                mFocusedChildIndex = getFocusedChildIndex(y);

                if (mFocusedChildIndex != UNFOCUSED_INDEX) {
//                    if (mOldHighLightChildIndex != 0) {
//                        ((TextView) getChildAt(mOldHighLightChildIndex)).setTextColor(Color.parseColor(UNFOCUSED_TEXT_COLOR));
//                    }
//                    ((TextView) getChildAt(mFocusedChildIndex)).setTextColor(Color.parseColor(FOCUSED_TEXT_COLOR));

                    if (mOnIndexerChangeListener != null) {
                        mOnIndexerChangeListener.onIndexerChange(INDEXERS[mFocusedChildIndex]);
                    }
                }
                break;
            case ACTION_MOVE:
                /*
                 * 以下分析的前提是字母A的上边和Z的下边有空白区域（IndexerSideBar区域内）
                 * 1.第一次点击的是顶部空白区域，然后挪到A：回调A
                 * 2.从A挪到顶部空白区域，不触发新的回调，仍然展示A
                 * 3.第2种情况下，从空白区域又挪回A，不触发新的回调，仍然显示A
                 *
                 * 4.第一次点击的是底部空白区域，然后挪到Z：回调Z
                 * 5.从Z挪到顶部空白区域，不触发新的回调，仍然展示Z
                 * 6.第5种情况下，从空白区域又挪回Z，不触发新的回调，仍然显示Z
                 */
                int newFocusedChildIndex = getFocusedChildIndex(y);
                if (newFocusedChildIndex != UNFOCUSED_INDEX && newFocusedChildIndex != mFocusedChildIndex) {
//                    ((TextView) getChildAt(mFocusedChildIndex)).setTextColor(Color.parseColor(UNFOCUSED_TEXT_COLOR));
//                    ((TextView) getChildAt(newFocusedChildIndex)).setTextColor(Color.parseColor(FOCUSED_TEXT_COLOR));
                    mFocusedChildIndex = newFocusedChildIndex;

                    if (mOnIndexerChangeListener != null) {
                        mOnIndexerChangeListener.onIndexerChange(INDEXERS[mFocusedChildIndex]);
                    }
                }
                break;
            case ACTION_UP:
            case ACTION_CANCEL:
                mFocusedChildIndex = UNFOCUSED_INDEX;
                if (mOnIndexerChangeListener != null) {
                    mOnIndexerChangeListener.onFingerUp();
                }
                break;
        }
        return true;
    }

    private int getFocusedChildIndex(float y) {
        for (int i = 0; i < mBounds.length; i++) {
            if (y <= mBounds[i]) {
                if (i == 0) {
                    return UNFOCUSED_INDEX;
                } else {
                    return i;
                }
            }
        }
        return UNFOCUSED_INDEX;
    }

    private int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (density * dp + 0.5f);
    }

    private int px2sp(int px) {
        float scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / scaledDensity + 0.5f);
    }

    private OnTouchIndexerListener mOnIndexerChangeListener;

    public void setOnIndexerChangeListener(OnTouchIndexerListener listener) {
        mOnIndexerChangeListener = listener;
    }

    public interface OnTouchIndexerListener {
        void onIndexerChange(String indexer);

        void onFingerUp();
    }
}
