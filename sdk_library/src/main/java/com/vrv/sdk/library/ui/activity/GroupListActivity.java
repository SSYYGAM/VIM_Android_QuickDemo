package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vrv.imsdk.SDKClient;
import com.vrv.imsdk.model.Group;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.bean.BaseInfoBean;
import com.vrv.sdk.library.ui.adapter.GroupListAdapter;

import java.util.ArrayList;
import java.util.List;

public class GroupListActivity extends BaseActivity {

    private ListView listView;
    private List<Group> groupList = new ArrayList<Group>();
    private GroupListAdapter groupAdapter;

    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, GroupListActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle("群列表");
    }

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context, R.layout.vim_activity_group, null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        listView = (ListView) contentView.findViewById(R.id.lv_group);
    }

    @Override
    protected void setViews() {
        groupList.addAll(SDKClient.instance().getGroupService().getGroups());
        groupAdapter = new GroupListAdapter(context, groupList);
        listView.setAdapter(groupAdapter);
    }

    @Override
    protected void setListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group group = groupList.get(position);
                BaseInfoBean infoBean = new BaseInfoBean();
                infoBean.setID(group.getId());
                infoBean.setName(group.getName());
                infoBean.setIcon(group.getAvatar());
                ChatActivity.start(context, infoBean);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
