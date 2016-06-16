package com.vrv.sdk.library.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.vrv.imsdk.util.VIMLog;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.utils.Utils;

/**
 * 字母检索侧边栏
 */
public class IndexSideBar extends View {

    private Context context;
    private char[] indexList;//索引列表
    private int choose = -1;// 选中
    private OnTouchingLetterChangedListener touchingListener;
    private WindowManager windowManager;
    private WindowManager.LayoutParams lp;
    private SectionIndexer sectionIndex = null;
    private RecyclerView recyclerView;
    private TextView dialogText;
    private int itemHeight;

    public IndexSideBar(Context context) {
        super(context);
        init(context);
    }

    public IndexSideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IndexSideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        indexList = new char[]{'#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                itemHeight = getHeight() / indexList.length;
                return true;
            }
        });
    }

    private void windowAddView() {
        dialogText = (TextView) View.inflate(context, R.layout.vim_view_index, null);
        dialogText.setVisibility(View.INVISIBLE);
        lp = new WindowManager.LayoutParams(Utils.dip2px(context, 76), Utils.dip2px(context, 76), WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(dialogText, lp);
    }

    private void windowUpdateView() {
        if (windowManager != null && dialogText != null && lp != null) {
            windowManager.updateViewLayout(dialogText, lp);
        }
    }

    public void windowRemoveView() {
        try {
            if (dialogText != null && windowManager != null) {
                windowManager.removeViewImmediate(dialogText);
                dialogText = null;
                windowManager = null;
            }
        } catch (Exception e) {
            VIMLog.e("--->indexBar remove dialog Exception:" + e.toString());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        windowRemoveView();
        super.onDetachedFromWindow();
    }

    public void setListView(RecyclerView recyclerView) {
        windowAddView();
        this.recyclerView = recyclerView;
        sectionIndex = (SectionIndexer) recyclerView.getAdapter();
    }

    public void setOnTouchingChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.touchingListener = onTouchingLetterChangedListener;
    }

    /**
     * 重写这个方法
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        // 获取焦点改变背景颜色.
        for (int i = 0; i < indexList.length; i++) {
            paint.setColor(getResources().getColor(R.color.vim_tab_selected));
            paint.setTypeface(Typeface.DEFAULT);
            paint.setAntiAlias(true);
            paint.setTextSize(Utils.sp2px(context, 14));
            // 选中的状态
            if (i == choose) {
                paint.setColor(getResources().getColor(R.color.vim_colorPrimary));
                paint.setFakeBoldText(true);
            }
            // x坐标等于中间-字符串宽度的一半.
            float xPos = getWidth() / 2 - paint.measureText(String.valueOf(indexList[i])) / 2;
            float yPos = itemHeight * (i + 1);
            canvas.drawText(String.valueOf(indexList[i]), xPos, yPos, paint);
            paint.reset();// 重置画笔
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDownOrMove((int) event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                actionDownOrMove((int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
                actionUp();
                break;
            default:
                return super.onTouchEvent(event);
        }
        invalidate();
        return true;
    }

    private void actionUp() {
        choose = -1;//
        setBackgroundColor(getResources().getColor(R.color.vim_transparent));
        if (dialogText != null) {
            dialogText.setVisibility(View.INVISIBLE);
        }
    }

    private void actionDownOrMove(int y) {
        int selection = y / itemHeight;
        if (selection >= indexList.length) {
            selection = indexList.length - 1;
        } else if (selection < 0) {
            selection = 0;
        }
        setBackgroundColor(getResources().getColor(R.color.vim_tx_gray));
        if (selection >= 0 && selection < indexList.length) {
            if (touchingListener != null) {
                touchingListener.onTouchingLetterChanged(String.valueOf(indexList[selection]));
            }
            if (dialogText != null) {
                dialogText.setText(String.valueOf(indexList[selection]));
                dialogText.setVisibility(View.VISIBLE);
                dialogText.invalidate();
                windowUpdateView();
            }

            if (sectionIndex != null && recyclerView != null) {
                int position = sectionIndex.getPositionForSection(indexList[selection]);
                if (position >= 0) {
                    recyclerView.scrollToPosition(position);
                }
            }
            choose = selection;
        }
    }


    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }
}
