package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.vrv.imsdk.SDKClient;
import com.vrv.imsdk.model.Group;
import com.vrv.imsdk.model.ServiceModel;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.bean.BaseInfoBean;
import com.vrv.sdk.library.common.listener.OnItemClickListener;
import com.vrv.sdk.library.group.GroupListView;
import com.vrv.sdk.library.common.view.IndexSideBar;

public class GroupListActivity extends BaseActivity {

    private GroupListView recycler;

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
        recycler = (GroupListView) contentView.findViewById(R.id.lv_group);
        IndexSideBar indexBar = (IndexSideBar) contentView.findViewById(R.id.side_bar);
        indexBar.setListView(recycler);
    }

    @Override
    protected void setViews() {
        recycler.getData().clear();
        recycler.getData().addAll(SDKClient.instance().getGroupService().getGroups());
        recycler.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void setListener() {
        recycler.setOnItemClick(new OnItemClickListener() {
            @Override
            public void OnItemClick(int position, View view) {
                Group group = recycler.getData().get(position);
                BaseInfoBean infoBean = BaseInfoBean.group2BaseInfo(group);
                ChatBaseActivity.start(context, infoBean, true);
            }

            @Override
            public boolean OnItemLongClick(int position, View view) {
                return false;
            }
        });

        SDKClient.instance().getGroupService().setListener(new ServiceModel.OnChangeListener() {
            @Override
            public void notifyDataChange() {
                setViews();
            }

            @Override
            public void notifyItemChange(int i) {

            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
