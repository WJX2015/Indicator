package com.example.lenovo_g50_70.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wjx on 2017/7/8.
 */

public class ViewPagerIndicator extends LinearLayout {

    private Paint mPaint;
    private Path mPath;

    private int mTriangleWidth;
    private int mTriangleHeight;

    private static final float RADIO_TRIANGLE_WIDTH = 1 / 6F;
    private final int DIMENSION_TRIANGLE_WIDTH_MAX = (int) (getScreenWidth() / 3 * RADIO_TRIANGLE_WIDTH);

    private int mInitTranslationX;  //三角形初始时的偏移量
    private int mTranslationX;//手指滑动平移的距离

    private int mTabVisibleCount;
    private static final int COUNT_DEFAULT_TAB = 4;

    private List<String> mTitles;
    private static final int COLOR_TEXT_NORMAL = 0x77FFFFFF;
    private static final int COLOR_TEXT_HIGH_LIGHT = 0xFFFFFFFF;

    private ViewPager mViewPager;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        //获取自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mTabVisibleCount = array.getInt(R.styleable.ViewPagerIndicator_visible_tab_count, COUNT_DEFAULT_TAB);
        if (mTabVisibleCount < 0) {
            mTabVisibleCount = COUNT_DEFAULT_TAB;
        }
        array.recycle();

        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.parseColor("#ffffffff"));
        mPaint.setStyle(Paint.Style.FILL);
        //免得三角形的角过于尖锐
        mPaint.setPathEffect(new CornerPathEffect(3));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.save();
        canvas.translate(mInitTranslationX + mTranslationX, getHeight() + 2);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //w是控件的宽，h是控件的高
        super.onSizeChanged(w, h, oldw, oldh);

        mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGLE_WIDTH);
        mTriangleWidth = Math.min(mTriangleWidth, DIMENSION_TRIANGLE_WIDTH_MAX);
        mInitTranslationX = w / mTabVisibleCount / 2 - mTriangleWidth / 2;

        initTriangle();
    }

    /**
     * 初始化三角形
     */
    private void initTriangle() {
        //以锐角为45度的等腰直角三角形为例
        mTriangleHeight = mTriangleWidth / 2;
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();
    }

    /**
     * 指示器跟随手指滚动
     *
     * @param position
     * @param positionOffset
     */
    public void scroll(int position, float positionOffset) {
        //getWidth()是获取当前控件的宽度
        int tabWidth = getWidth() / mTabVisibleCount;
        //指示器滑动的距离
        mTranslationX = (int) (tabWidth * (positionOffset + position));

        //容器移动，在tab处于移动至最后一个时
        if (position >= (mTabVisibleCount - 2) && positionOffset > 0 && getChildCount() > mTabVisibleCount) {
            if (mTabVisibleCount != 1) {
                this.scrollTo((position - (mTabVisibleCount - 2)) * tabWidth + (int) (tabWidth * positionOffset), 0);
            } else {
                this.scrollTo(position * tabWidth + (int) (tabWidth * positionOffset), 0);
            }

        }

        //重绘
        invalidate();
    }

    @Override
    protected void onFinishInflate() {
        //当布局文件加载完成后回调的方法
        super.onFinishInflate();
        //获取子控件个数
        int count = getChildCount();
        if (count == 0) return;

        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            LinearLayout.LayoutParams Lp = (LayoutParams) view.getLayoutParams();
            Lp.weight = 0;
            Lp.width = getScreenWidth() / mTabVisibleCount;
            view.setLayoutParams(Lp);
        }

        //设置tab点击事件
        setItemClick();
    }

    /**
     * 获得屏幕的宽度
     *
     * @return
     */
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 在代码里动态添加tab
     *
     * @param titles
     */
    public void setTabItem(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            this.removeAllViews();
            mTitles = titles;
            for (String title : mTitles) {
                addView(generateTextView(title));
            }
            //设置tab点击事件
            setItemClick();
        }
    }

    /**
     * 设置可见tab的数量
     *
     * @param count
     */
    public void setVisibleTabCount(int count) {
        mTabVisibleCount = count;
    }

    /**
     * 根据title创建tab
     *
     * @param title
     * @return
     */
    private View generateTextView(String title) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams Lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Lp.width = getScreenWidth() / mTabVisibleCount;
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTextColor(COLOR_TEXT_NORMAL);
        tv.setLayoutParams(Lp);
        return tv;
    }

    /**
     * 设置关联的ViewPager
     *
     * @param vp
     * @param
     */
    public void setViewPager(ViewPager vp, int pos) {
        mViewPager = vp;
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //tabWidth*positionOffset+position*tabWidth
                scroll(position, positionOffset);

                if (mListener != null) {
                    mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (mListener != null) {
                    mListener.onPageSelected(position);
                }
                highLightTextView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mListener != null) {
                    mListener.onPageScrollStateChanged(state);
                }
            }
        });

        mViewPager.setCurrentItem(pos);
        highLightTextView(pos);
    }

    public PageOnchangeListener mListener;

    /**
     * 设置页面滑动监听
     *
     * @param listener
     */
    public void setOnPageChangeListener(PageOnchangeListener listener) {
        mListener = listener;
    }

    public interface PageOnchangeListener {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }

    /**
     * 选中tab文本高亮
     *
     * @param pos
     */
    private void highLightTextView(int pos) {
        resetTextViewColor();
        View view = getChildAt(pos);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(COLOR_TEXT_HIGH_LIGHT);
        }
    }

    /**
     * 重置tab文本颜色
     */
    public void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }

    /**
     * 设置TabItem点击事件,与ViewPager联动
     */
    private void setItemClick() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final int j = i;

            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }
}
