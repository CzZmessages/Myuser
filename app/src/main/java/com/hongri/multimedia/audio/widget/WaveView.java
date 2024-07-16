package com.hongri.multimedia.audio.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;

/**
 * @author cpc$
 * @version 1.0
 * @description: TODO
 * @date $ $
 */
public class WaveView extends View {
    private Path mPath;

    private Paint mPaintBezier;

    private int mWaveLength;
    private int mScreenHeight;
    private int mScreenWidth;
    private int mCenterY;
    private int mWaveCount;

    private ValueAnimator mValueAnimator;
    //波浪流动X轴偏移量
    private int mOffsetX;
    //波浪升起Y轴偏移量
    private int mOffsetY;
    private int count = 0;

    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaintBezier = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBezier.setColor(Color.rgb(66,185,239));
        mPaintBezier.setStrokeWidth(8);
        mPaintBezier.setStyle(Paint.Style.STROKE);

        mWaveLength = 800;

    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPath = new Path();


        mScreenHeight = h;
        mScreenWidth = w;
        mCenterY = h / 2;//设定波浪在屏幕中央处显示

        //此处多加1，是为了预先加载屏幕外的一个波浪，持续报廊移动时的连续性
        mWaveCount = (int) Math.round(mScreenWidth / mWaveLength + 1.5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPath.reset();
        //Y坐标每次绘制时减去偏移量，即波浪升高
        mPath.moveTo(-mWaveLength + mOffsetX, mCenterY);
//        LogUtils.e("开始绘制！" + mWaveLength+"     conut====>"+mWaveCount+"    ===="+mOffsetX);
        //每次循环绘制两个二阶贝塞尔曲线形成一个完整波形（含有一个上拱圆，一个下拱圆）
        for (int i = 0; i < mWaveCount; i++) {
            //此处的60是指波浪起伏的偏移量，自定义为60
           /*
            mPath.quadTo(-mWaveLength * 3 / 4 + i * mWaveLength + mOffsetX, mCenterY + 60, -mWaveLength / 2 + i * mWaveLength + mOffset, mCenterY);
            mPath.quadTo(-mWaveLength / 4 + i * mWaveLength + mOffsetX, mCenterY - 60, i * mWaveLength + mOffset, mCenterY);
            */
            //第二种写法：相对位移
            mPath.rQuadTo(mWaveLength / 4, -60, mWaveLength / 2, 0);
            mPath.rQuadTo(mWaveLength / 4, +60, mWaveLength / 2, 0);

        }
//        mPath.lineTo(mScreenWidth, mScreenHeight);
//        mPath.lineTo(0, mScreenHeight);
//        mPath.close();
        canvas.drawPath(mPath, mPaintBezier);
    }

    public void startAn() {
        //设置动画运动距离
        mValueAnimator = ValueAnimator.ofInt(0, mWaveLength);
        mValueAnimator.setDuration(1000);
        //设置播放数量无限循环
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        mValueAnimator.setRepeatCount(1);
        //设置线性运动的插值器
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取偏移量，绘制波浪曲线的X横坐标加上此偏移量，产生移动效果
                mOffsetX = (int) valueAnimator.getAnimatedValue();
                count++;
//                LogUtils.e("开启动画！"+valueAnimator.getAnimatedValue());
                invalidate();
            }
        });
        mValueAnimator.start();
    }

    public void stopAn() {
        mValueAnimator.cancel();
    }
}
