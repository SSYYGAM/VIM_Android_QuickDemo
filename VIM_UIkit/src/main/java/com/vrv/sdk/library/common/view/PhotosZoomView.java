package com.vrv.sdk.library.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by Yang on 2015/12/4 004.
 */
public class PhotosZoomView extends ImageView implements ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    private Context context;
    public static float SCALE_MAX = 4.0f;
    private static float SCALE_MID = 2.0f;


    //初始化时的缩放比例，如果图片宽或高大于屏幕
    private float initScale = 1.0f;
    private boolean once = true;
    //用于存放矩阵
    private final float[] matrixValues = new float[9];

    //缩放的手势
    private ScaleGestureDetector mScaleGestureDetector = null;
    private final Matrix mScaleMatrix = new Matrix();

    //双击
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;
    private float mLastX;
    private float mLastY;
    private boolean isCanDrag;
    private int lastPointerCount;
    //水平方向与View的边距
    private int mHorizontalPadding;

    public PhotosZoomView(Context context) {
        super(context);
        init(context);
    }

    public PhotosZoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PhotosZoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setScaleType(ScaleType.MATRIX);
        mGestureDetector = new GestureDetector(context, new GestureListener());
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (isAutoScale)
                return true;

            float x = e.getX();
            float y = e.getY();
            postDelayed(new AutoScaleRunnable(getScale() < SCALE_MID ? SCALE_MID : initScale, x, y), 16);
            isAutoScale = true;
            return true;
        }
    }

    /**
     * 自动缩放的
     */
    private class AutoScaleRunnable implements Runnable {
        static final float BIGGER = 1.07f;
        static final float SMALLER = 0.93f;
        private float mTargetScale;
        private float tmpScale;
        private float x;
        private float y;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         *
         * @param targetScale
         */
        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            tmpScale = getScale() < mTargetScale ? BIGGER : SMALLER;
        }

        @Override
        public void run() {
            // 进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorder();
            setImageMatrix(mScaleMatrix);

            final float currentScale = getScale();
            // 如果值在合法范围内，继续缩放
            if (((tmpScale > 1f) && (currentScale < mTargetScale))
                    || ((tmpScale < 1f) && (mTargetScale < currentScale))) {
                postDelayed(this, 16);
            } else {
                // 设置为目标的缩放比例
                final float deltaScale = mTargetScale / currentScale;
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y);
                checkBorder();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null)
            return true;

        /**
         * 缩放的范围控刿
         */
        if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                || (scale > initScale && scaleFactor < 1.0f)) {

            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale;
            }
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }
            /**
             * 设置缩放比例
             */
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());
            checkBorder();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    /**
     * 根据当前图片的Matrix获得图片的范囿
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;
        mScaleGestureDetector.onTouchEvent(event);

        float x = 0, y = 0;
        // 拿到触摸点的个数
        final int pointerCount = event.getPointerCount();
        // 得到多个触摸点的x与y
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;

        /**
         * 每当触摸点发生变化时，重置mLasX , mLastY
         */
        if (pointerCount != lastPointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        lastPointerCount = pointerCount;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                if (isCanDrag) {
                    if (getDrawable() != null) {

                        RectF rectF = getMatrixRectF();
                        // 如果宽度小于屏幕宽度，则禁止左右移动
                        if (rectF.width() <= getWidth() - mHorizontalPadding * 2) {
                            dx = 0;
                        }

                        // 如果高度小雨屏幕高度，则禁止上下移动
                        if (rectF.height() <= getHeight() - getHVerticalPadding()
                                * 2) {
                            dy = 0;
                        }
                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorder();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                break;
        }

        return true;
    }

    /**
     * 获得当前的缩放比便
     *
     * @return
     */
    public final float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    /**
     * 垂直方向与View的边跿
     */
    // private int getHVerticalPadding();
    @Override
    public void onGlobalLayout() {
        if (once) {
            Drawable d = getDrawable();
            if (d == null)
                return;
            // 垂直方向的边跿
            // getHVerticalPadding() = (getHeight() - (getWidth() - 2 *
            // mHorizontalPadding)) / 2;

            int width = getWidth();
            int height = getHeight();
            // 拿到图片的宽和高
            int drawableW = d.getIntrinsicWidth();
            int drawableH = d.getIntrinsicHeight();
            float scale = 1.0f;

            int frameSize = getWidth() - mHorizontalPadding * 2;

            // 大图
            if (drawableW > frameSize && drawableH < frameSize) {
                scale = 1.0f * frameSize / drawableH;
            } else if (drawableH > frameSize && drawableW < frameSize) {
                scale = 1.0f * frameSize / drawableW;
            } else if (drawableW > frameSize && drawableH > frameSize) {
                float scaleW = frameSize * 1.0f / drawableW;
                float scaleH = frameSize * 1.0f / drawableH;
                scale = Math.max(scaleW, scaleH);
            }

            // 太小的图片放大处琿
            if (drawableW < frameSize && drawableH > frameSize) {
                scale = 1.0f * frameSize / drawableW;
            } else if (drawableH < frameSize && drawableW > frameSize) {
                scale = 1.0f * frameSize / drawableH;
            } else if (drawableW < frameSize && drawableH < frameSize) {
                float scaleW = 1.0f * frameSize / drawableW;
                float scaleH = 1.0f * frameSize / drawableH;
                scale = Math.max(scaleW, scaleH);
            }

            initScale = scale;
            SCALE_MID = initScale * 2;
            SCALE_MAX = initScale * 4;
            mScaleMatrix.postTranslate((width - drawableW) / 2,
                    (height - drawableH) / 2);
            mScaleMatrix.postScale(scale, scale, getWidth() / 2,
                    getHeight() / 2);

            // 图片移动至屏幕中忿
            setImageMatrix(mScaleMatrix);
            once = false;
        }
    }

    /**
     * 剪切图片，返回剪切后的bitmap对象
     *
     * @return
     */
    public Bitmap crop() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return Bitmap.createBitmap(bitmap, mHorizontalPadding,
                getHVerticalPadding(), getWidth() - 2 * mHorizontalPadding,
                getWidth() - 2 * mHorizontalPadding);
    }

    /**
     * 边界棿Ջ
     */
    private void checkBorder() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 如果宽或高大于屏幕，则控制范囿; 这里皿.001是因为精度丢失会产生问题，但是误差一般很小，承ۥ我们直接加了丿ت0.01
        if (rect.width() + 0.01 >= width - 2 * mHorizontalPadding) {
            if (rect.left > mHorizontalPadding) {
                deltaX = -rect.left + mHorizontalPadding;
            }

            if (rect.right < width - mHorizontalPadding) {
                deltaX = width - mHorizontalPadding - rect.right;
            }
        }

        if (rect.height() + 0.01 >= height - 2 * getHVerticalPadding()) {
            if (rect.top > getHVerticalPadding()) {
                deltaY = -rect.top + getHVerticalPadding();
            }

            if (rect.bottom < height - getHVerticalPadding()) {
                deltaY = height - getHVerticalPadding() - rect.bottom;
            }
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 是否是拖动行丿
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= 0;
    }

    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    private int getHVerticalPadding() {
        return (getHeight() - (getWidth() - 2 * mHorizontalPadding)) / 2;
    }
}