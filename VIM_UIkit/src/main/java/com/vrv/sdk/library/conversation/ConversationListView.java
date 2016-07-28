package com.vrv.sdk.library.conversation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.vrv.imsdk.model.Chat;
import com.vrv.sdk.library.common.view.BaseRecyclerView;
import com.vrv.sdk.library.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 最近联系人列表
 */
public class ConversationListView extends BaseRecyclerView {
    private List<Chat> conversations;

    public ConversationListView(Context context) {
        super(context);
        init();
    }

    public ConversationListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ConversationListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        addItemDecoration(Utils.buildDividerItemDecoration(context));
        conversations = new ArrayList<>();
        adapter = new ConversationListAdapter(context,conversations);
        setAdapter(adapter);
        setLayoutManager(new LinearLayoutManager(context));
    }

    public List<Chat> getData(){
        return this.conversations;
    }
}
