package com.vrv.sdk.library.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Yang on 2015/12/4 004.
 */
public class PhotosCropView extends RelativeLayout {

    private Context context;
    private int mHorizontalPadding = 20;
    private PhotosZoomView mZoomImageView;
    private PhotosBorderView mClipImageView;

    public PhotosCropView(Context context) {
        super(context);
        init(context);
    }

    public PhotosCropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public PhotosCropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        mZoomImageView = new PhotosZoomView(context);
        mClipImageView = new PhotosBorderView(context);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        /**
         * 这里测试，直接写死了图片，真正使用过程中，可以提取为自定义属性
         */
//        mZoomImageView.setImageDrawable(Utils.getDrawable(context, R.mipmap.vim_pictures_default));

        this.addView(mZoomImageView, lp);
        this.addView(mClipImageView, lp);


        // 计算padding的px
        mHorizontalPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
                        .getDisplayMetrics());
        mZoomImageView.setHorizontalPadding(mHorizontalPadding);
        mClipImageView.setHorizontalPadding(mHorizontalPadding);
    }

    public ImageView getZoomImageView() {
        return mZoomImageView;
    }

    /**
     * 对外公布设置边距的方法,单位为dp
     *
     * @param mHorizontalPadding
     */
    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    /**
     * 裁切图片
     *
     * @return
     */
    public Bitmap crop() {
        return mZoomImageView.crop();
    }


    public class PhotosBorderView extends View {
        /**
         * 水平方向与View的边距
         */
        private int mHorizontalPadding = 20;
        /**
         * 垂直方向与View的边距
         */
        private int mVerticalPadding;
        /**
         * 绘制的矩形的宽度
         */
        private int mWidth;
        /**
         * 边框的颜色，默认为白色
         */
        private int mBorderColor = Color.parseColor("#FFFFFF");
        /**
         * 边框的宽度 单位dp
         */
        private int mBorderWidth = 1;

        private Paint mPaint;

        public PhotosBorderView(Context context) {
            this(context, null);
            init();
        }

        public PhotosBorderView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
            init();
        }

        public PhotosBorderView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();

        }

        private void init() {
            // 计算padding的px
            mHorizontalPadding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
                            .getDisplayMetrics());
            mBorderWidth = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources()
                            .getDisplayMetrics());
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            //计算矩形区域的宽度
            mWidth = getWidth() - 2 * mHorizontalPadding;
            //计算距离屏幕垂直边界 的边距
            mVerticalPadding = (getHeight() - mWidth) / 2;
            mPaint.setColor(Color.parseColor("#aa000000"));
            mPaint.setStyle(Paint.Style.FILL);
            // 绘制左边1
            canvas.drawRect(0, 0, mHorizontalPadding, getHeight(), mPaint);
            // 绘制右边2
            canvas.drawRect(getWidth() - mHorizontalPadding, 0, getWidth(),
                    getHeight(), mPaint);
            // 绘制上边3
            canvas.drawRect(mHorizontalPadding, 0, getWidth() - mHorizontalPadding,
                    mVerticalPadding, mPaint);
            // 绘制下边4
            canvas.drawRect(mHorizontalPadding, getHeight() - mVerticalPadding,
                    getWidth() - mHorizontalPadding, getHeight(), mPaint);
            // 绘制外边框
            mPaint.setColor(mBorderColor);
            mPaint.setStrokeWidth(mBorderWidth);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(mHorizontalPadding, mVerticalPadding, getWidth()
                    - mHorizontalPadding, getHeight() - mVerticalPadding, mPaint);

        }

        public void setHorizontalPadding(int mHorizontalPadding) {
            this.mHorizontalPadding = mHorizontalPadding;
        }

    }
}