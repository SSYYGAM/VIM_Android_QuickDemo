package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.vrv.imsdk.model.GroupMember;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.common.listener.OnItemClickListener;
import com.vrv.sdk.library.ui.adapter.GroupMemberAdapter;
import com.vrv.sdk.library.common.view.IndexSideBar;

/**
 * 选择群成员，
 */
public class SelectGroupMemberActivity extends BaseActivity {

    private RecyclerView listView;
    private IndexSideBar indexBar;
    private GroupMemberAdapter adapter;

    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, SelectGroupMemberActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle("选择联系人");
    }

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context, R.layout.vim_fragment_contacts, null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        listView = (RecyclerView) contentView.findViewById(R.id.lv_contact);
        indexBar = (IndexSideBar) contentView.findViewById(R.id.side_bar);
    }

    @Override
    protected void setViews() {
        listView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerViewHeader headerView = (RecyclerViewHeader) contentView.findViewById(R.id.header);
        //全体成员
        ((TextView) headerView.findViewById(R.id.name_tv)).setText("全体成员");
        headerView.findViewById(R.id.ll_contacts_group).setOnClickListener(this);
        headerView.attachTo(listView);
        adapter = new GroupMemberAdapter(context);
        listView.setAdapter(adapter);
        listView.smoothScrollToPosition(0);
        indexBar.setListView(listView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ChatBaseActivity.getMemberList().size() > 0) {
            adapter.update();
        }
    }

    @Override
    protected void onStop() {
        indexBar.windowRemoveView();
        super.onStop();
    }

    @Override
    protected void setListener() {
        adapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void OnItemClick(int position, View view) {
                resultFinish(adapter.getItemObject(position));
            }

            @Override
            public boolean OnItemLongClick(int position, View view) {
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_contacts_group) {
            GroupMember member = new GroupMember();
            member.setId(ChatBaseActivity.getChatID());
            member.setName("全体成员");
            resultFinish(member);
        }
    }

    private void resultFinish(GroupMember contact) {
        Intent data = new Intent();
        data.putExtra("data", contact);
        setResult(RESULT_OK, data);
        finish();
    }
}
