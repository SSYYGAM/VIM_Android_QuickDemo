package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.vrv.imsdk.SDKClient;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.VimConstant;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.common.listener.OnItemClickListener;
import com.vrv.sdk.library.ui.adapter.ContactsAdapter;
import com.vrv.sdk.library.common.view.IndexSideBar;
import com.vrv.sdk.library.utils.DialogUtil;

import java.util.ArrayList;

/**
 * //创建群，邀请群成员，发送名片公用同一个页面
 * 选择联系人列表
 */
public class SelectContactActivity extends BaseActivity {

    private final String TAG = SelectContactActivity.class.getSimpleName();

    private ContactsAdapter adapter;
    //控制是否显示选项框
    private boolean isShowCheckBox = true;
    private boolean isShowGroup = false;
    private long groupID;
    //已经选中的群成员
    private long[] selectedList;
    private IndexSideBar indexBar;
    private LinearLayout llContactsGroup;
    private int type;
    public static final int TYPE_CREATE_GROUP = 1;
    public static final int TYPE_ADD_BLACK = 2;
    private ArrayList<ChatMsg> msgList;


    /**
     * 创建群
     *
     * @param activity
     */
    public static void start(Activity activity) {
        start(activity, TYPE_CREATE_GROUP);
    }

    public static void start(Activity activity, int type) {
        start(activity, type, 0, null);
    }

    /**
     * 邀请群成员
     *
     * @param activity
     * @param groupID
     * @param selectedArray
     */
    public static void start(Activity activity, int type, long groupID, long[] selectedArray) {
        Intent intent = new Intent();
        intent.setClass(activity, SelectContactActivity.class);
        intent.putExtra("selected", selectedArray);
        intent.putExtra("groupID", groupID);
        intent.putExtra("type", type);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, long groupID, long[] selectedArray) {
        start(activity, TYPE_CREATE_GROUP, groupID, selectedArray);
    }

    /**
     * 用于转发消息
     *
     * @param activity
     * @param msgList
     */
    public static void start(Activity activity, int type, ArrayList<ChatMsg> msgList) {
        Intent intent = new Intent();
        intent.setClass(activity, SelectContactActivity.class);
        intent.putParcelableArrayListExtra("msgList", msgList);
        intent.putExtra("type", type);
        activity.startActivity(intent);
    }

    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, SelectContactActivity.class);
        intent.putExtra("isSelect", false);
        intent.putExtra("type", requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle("选择联系人");
        selectedList = getIntent().getLongArrayExtra("selected");
        groupID = getIntent().getLongExtra("groupID", 0);
        isShowCheckBox = getIntent().getBooleanExtra("isSelect", true);
        type = getIntent().getIntExtra("type", TYPE_CREATE_GROUP);
        msgList = getIntent().getParcelableArrayListExtra("msgList");
    }

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context, R.layout.vim_fragment_contacts, null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        RecyclerViewHeader headerView = (RecyclerViewHeader) contentView.findViewById(R.id.header);
        headerView.setVisibility(isShowGroup ? View.VISIBLE : View.GONE);
        llContactsGroup = (LinearLayout) headerView.findViewById(R.id.ll_contacts_group);
        RecyclerView listView = (RecyclerView) contentView.findViewById(R.id.lv_contact);
        indexBar = (IndexSideBar) contentView.findViewById(R.id.side_bar);
        listView.setLayoutManager(new LinearLayoutManager(context));
        headerView.attachTo(listView);
        adapter = new ContactsAdapter(context, SDKClient.instance().getContactService().getList(), selectedList, isShowCheckBox);
        listView.setAdapter(adapter);
        listView.smoothScrollToPosition(0);

        indexBar.setListView(listView);
    }

    @Override
    protected void setListener() {
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(final int position, View view) {
                if (type == VimConstant.TYPE_CARD) {
                    //发送名片返回
                    Intent data = new Intent();
                    data.putExtra("data", adapter.getItemObject(position));
                    setResult(RESULT_OK, data);
                    finish();
                } else if (type == VimConstant.TYPE_MSG_FORWARD) {
                    DialogUtil.buildSelectDialog(context, "确定转发给：", adapter.getItemObject(position).getName(), new MaterialDialog.SingleButtonCallback() {

                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            RequestHelper.transferMsg(adapter.getItemObject(position).getId(), msgList, null);
                            finish();
                        }
                    });
                } else if (isShowCheckBox) {
                    adapter.updateSelectStatus(position);
                }
            }

            @Override
            public boolean OnItemLongClick(int position, View view) {
                return false;
            }
        });
        llContactsGroup.setOnClickListener(this);
    }

    @Override
    protected void setViews() {

    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestHandler != null) {
            requestHandler.sendEmptyMessage(RequestHandler.DIS_PRO);
        }
    }

    @Override
    protected void onDestroy() {
        indexBar.windowRemoveView();
        super.onDestroy();
    }
}
