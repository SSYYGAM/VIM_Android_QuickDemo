package com.vrv.sdk.library.chat.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vrv.sdk.library.R;
import com.vrv.sdk.library.ui.adapter.ChatOptionsPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatInputOptionView extends LinearLayout implements ViewPager.OnPageChangeListener {

    private final String TAG = ChatInputOptionView.class.getSimpleName();

    private Context context;
    private ViewPager viewPager;
    private LinearLayout llPagerIndex;//滑动小圆点
    private List<ImageView> imgIndexList = new ArrayList<>();//所有的页面标识圆点
    private int index = 0;//圆点显示标识
    private ChatOptionsPagerAdapter optionAdapter;

    public ChatInputOptionView(Context context) {
        super(context);
        initView(context);
    }

    public ChatInputOptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ChatInputOptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        View view = View.inflate(context, R.layout.vim_view_chat_input_option, this);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        llPagerIndex = (LinearLayout) view.findViewById(R.id.ll_input_pager_index);
        viewPager.addOnPageChangeListener(this);

        initOptions();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        viewPager.removeOnPageChangeListener(this);
    }

    //展示操作项布局
    private void initOptions() {
        if (optionAdapter == null) {
            optionAdapter = new ChatOptionsPagerAdapter(context);
        }
        viewPager.setAdapter(optionAdapter);
        viewPager.setCurrentItem(0);
        initPagerIndex(optionAdapter.getCount());
    }

    //pager引导小圆点
    private void initPagerIndex(int pageCount) {
        index = 0;
        if (pageCount <= 1) {
            llPagerIndex.setVisibility(View.GONE);
        } else {
            llPagerIndex.setVisibility(View.VISIBLE);
            llPagerIndex.removeAllViews();
            imgIndexList.clear();
            for (int i = 0; i < pageCount; i++) {
                ImageView imgIndex = new ImageView(context);
                LayoutParams layoutParams = new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(8, 0, 8, 0);
                imgIndex.setLayoutParams(layoutParams);
                imgIndex.setImageResource(R.drawable.vim_pager_index_bg);
                if (i == 0) {
                    imgIndex.setSelected(true);
                }
                // 将小圆点放入到布局中
                llPagerIndex.addView(imgIndex);
                imgIndexList.add(imgIndex);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (llPagerIndex.getVisibility() == VISIBLE && imgIndexList.size() > index && imgIndexList.size() > position) {
            imgIndexList.get(index).setSelected(false);
            imgIndexList.get(position).setSelected(true);
            index = position;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public void setOptionListener(ChatOptionsPagerAdapter.OnOptionListener listener) {
        if (listener != null && optionAdapter != null) {
            optionAdapter.setClickListener(listener);
        }
    }
}
