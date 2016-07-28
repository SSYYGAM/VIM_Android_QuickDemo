package com.vrv.sdk.library.group;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.vrv.imsdk.model.Group;
import com.vrv.sdk.library.common.view.BaseRecyclerView;
import com.vrv.sdk.library.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 群列表
 */
public class GroupListView extends BaseRecyclerView {
    private List<Group> groups;

    public GroupListView(Context context) {
        super(context);
        init();
    }

    public GroupListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GroupListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        addItemDecoration(Utils.buildDividerItemDecoration(context));
        groups = new ArrayList<>();
        adapter = new GroupListAdapter(context, groups);
        setAdapter(adapter);
        setLayoutManager(new LinearLayoutManager(context));
    }

    public List<Group> getData(){
        return this.groups;
    }
}
