package com.pxq.myapplication.widget;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class CountDownView extends View {

    private static final String TAG = "CountDownView";

    private Paint mPaint, mTextPaint;

    private Path mPath, mDstPath;
    private PathMeasure mPathMeasure;

    private int mStrokeWidth = 15;

    private float mPathLength = 0;

    private float mDelta = 0f;

    private int mCount = 5;
    private int mCurrentCount = mCount;

    private Rect mTextRect;

    public CountDownView(Context context) {
        super(context);
        init();
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(100);

        mPath = new Path();
        mDstPath = new Path();
        mPathMeasure = new PathMeasure();

        mTextRect = new Rect();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPath.reset();
        int radius= w > h ? h : w;
        //圆环
        mPath.addCircle(getWidth() / 2, getHeight() / 2, (radius- mStrokeWidth) / 2, Path.Direction.CW);
        mPathMeasure.setPath(mPath, false);
        //获取圆的长度
        mPathLength = mPathMeasure.getLength();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //动态计算剩余路径长度
        mPathMeasure.getSegment(mPathLength * mDelta, mPathLength, mDstPath, true);
        canvas.save();
        //画布逆时针旋转90度
        canvas.rotate(-90, getWidth() / 2, getHeight() / 2);
        //画圆
        canvas.drawPath(mDstPath, mPaint);
        mDstPath.reset();
        canvas.restore();
        //draw text
        mTextPaint.setColor(Color.BLACK);
        //计算文本宽高
        getTextBounds(mCurrentCount);
        canvas.drawText(String.valueOf(mCurrentCount), (getWidth() - mTextRect.width()) / 2, (getHeight() + mTextRect.height()) / 2, mTextPaint);
        mTextPaint.setColor(Color.RED);
    }

    private void getTextBounds(int count) {
        mTextPaint.getTextBounds(String.valueOf(count), 0, String.valueOf(count).length(), mTextRect);
    }

    public void setCount(int count) {
        mCount = count;
        startAnim();
    }

    private void startAnim() {
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        ValueAnimator countAnimator = ValueAnimator.ofInt(mCount, 0);
        //用来倒计时
        countAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
//                Log.e(TAG, "onAnimationUpdate: " + value);
                mCurrentCount = value;
            }
        });
        //用来更新圆的长度
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDelta = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.play(valueAnimator)
                .with(countAnimator);
        set.setDuration(mCount * 1000);
        set.setInterpolator(new LinearInterpolator());
        set.start();
    }
}
