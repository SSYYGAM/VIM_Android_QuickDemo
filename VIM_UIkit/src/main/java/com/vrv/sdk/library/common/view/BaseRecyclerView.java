package com.vrv.sdk.library.common.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.vrv.sdk.library.common.adapter.BaseRecyclerAdapter;
import com.vrv.sdk.library.common.listener.OnItemClickListener;

public class BaseRecyclerView extends RecyclerView {

    public Context context;
    public BaseRecyclerAdapter adapter;
    public OnItemClickListener listener;

    public BaseRecyclerView(Context context) {
        super(context);
        this.context = context;
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void setOnItemClick(OnItemClickListener listener) {
        if (listener != null) {
            this.listener = listener;
            if (adapter != null) {
                adapter.setOnItemClickListener(this.listener);
            }
        }
    }

    public BaseRecyclerAdapter getAdapter() {
        return adapter;
    }
}
