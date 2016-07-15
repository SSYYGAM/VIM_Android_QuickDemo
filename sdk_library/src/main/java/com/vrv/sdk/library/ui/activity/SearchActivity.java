package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.vrv.imsdk.SDKClient;
import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.api.Constants;
import com.vrv.imsdk.bean.SimpleSearchResult;
import com.vrv.imsdk.model.ItemModel;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.bean.BaseInfoBean;
import com.vrv.sdk.library.ui.adapter.SearchListAdapter;
import com.vrv.sdk.library.utils.ToastUtil;

import java.util.ArrayList;

public class SearchActivity extends BaseActivity {
    private SearchListAdapter adapter;
    private ArrayList<BaseInfoBean> list = new ArrayList<BaseInfoBean>();
    protected RequestHandler requestHandler;
    private EditText editText;
    private Button button;
    private ListView listView;
    private long userID;
    private long groupID;

    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, SearchActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle("添加好友/群");
    }

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context, R.layout.vim_activity_search, null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        editText = (EditText) contentView.findViewById(R.id.et_search);
        button = (Button) contentView.findViewById(R.id.bt_search);
        listView = (ListView) contentView.findViewById(R.id.lv_search);
    }

    @Override
    protected void setViews() {

    }

    @Override
    protected void setListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(editText.getText().toString());
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void search(String key) {
        requestHandler = new SearchHandler(context);
        requestHandler.sendEmptyMessage(RequestHandler.SHOW_PRO);
        boolean search = RequestHelper.searchNet(key, requestHandler);
        if (!search) {
            requestHandler.sendEmptyMessage(RequestHandler.REQUEST_FALSE);
        }
    }

    @Override
    public void onClick(View v) {

    }

    class SearchHandler extends RequestHandler {

        public SearchHandler(Context context) {
            super(context);
        }

        @Override
        public void handleSuccess(Message msg) {
            list.clear();
            SimpleSearchResult searchResult = msg.getData().getParcelable(KEY_DATA);
            ArrayList<ItemModel> result = SDKClient.instance().getContactService().simpleResult2Item(searchResult);
            if (result != null && result.size() > 0) {
                for (ItemModel itemModel : result) {
                    list.add(BaseInfoBean.itemModel2BaseInfo(itemModel));
                }
                updateData();
            }
        }
    }

    private void updateData() {
        if (adapter == null) {
            adapter = new SearchListAdapter(context, list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    BaseInfoBean bean = list.get(position);
                    userID = bean.getID();
                    if (ChatMsgApi.isUser(userID)) {
                        if (SDKClient.instance().getContactService().isFriend(userID)) {
                            ChatActivity.start(context, bean);
                        } else {
                            add(bean);
                        }
                    } else if (ChatMsgApi.isGroup(userID)) {
                        if (SDKClient.instance().getGroupService().findItemByID(userID) != null) {
                            ChatActivity.start(context, bean);
                        } else {
                            add(bean);
                        }
                    }
                }
            });
        }
        adapter.notifyDataSetChanged();
    }

    private void add(final BaseInfoBean bean) {
        AlertDialog.Builder builder = new Builder(context);
        builder.setMessage("您确定要添加" + bean.getName() + "为好友么？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ChatMsgApi.isUser(bean.getID())){
                    getBuddyVerify();
                }else if (ChatMsgApi.isGroup(bean.getID())){
                    ToastUtil.showShort(context,"暂不支持该功能");
                }
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //先拉取验证方式，然后请求添加好友
    private void getBuddyVerify() {
        requestHandler = new GetInfoHandler(GetInfoHandler.GET_VERIFY, context);
        requestHandler.sendEmptyMessage(RequestHandler.SHOW_PRO);
        boolean verify = RequestHelper.getContactVerifyType(userID, requestHandler);
        if (!verify) {
            requestHandler.sendEmptyMessage(RequestHandler.REQUEST_FALSE);
        }
    }

    /**
     * 添加好友
     */
    private void addBuddy() {
        String verifyInfo = RequestHelper.getMyInfo().getName();
        requestHandler = new GetInfoHandler(GetInfoHandler.ADD_BUDDY, context);
        requestHandler.sendEmptyMessage(RequestHandler.SHOW_PRO);
        boolean add = RequestHelper.addContact(userID, verifyInfo, "", requestHandler);
        if (!add) {
            requestHandler.sendEmptyMessage(RequestHandler.REQUEST_FALSE);
        }
    }

    private void getGroupVerifyType() {
        requestHandler = new GroupInfoHandler(GroupInfoHandler.GET_VERIFY, context);
        requestHandler.sendEmptyMessage(RequestHandler.SHOW_PRO);
        boolean verify = RequestHelper.getGroupVerifyType(groupID, requestHandler);
        if (!verify) {
            requestHandler.sendEmptyMessage(RequestHandler.REQUEST_FALSE);
        }
    }

    class GetInfoHandler extends RequestHandler {

        public static final int GET_BUDDY = 1;
        public static final int GET_STRONGER = 2;
        public static final int GET_VERIFY = 3;
        public static final int ADD_BUDDY = 4;
        private int type;

        public GetInfoHandler(int type, Context context) {
            super(context);
            this.type = type;
        }

        @Override
        public void handleSuccess(Message msg) {
            if (type == GET_BUDDY) {//获取好友信息成功
            } else if (type == GET_STRONGER) {

            } else if (type == GET_VERIFY) {//添加好友获取验证方式

                byte verifyType = msg.getData().getByte(KEY_DATA);
                if (verifyType == Constants.TYPE_NOT_ALLOW) {
                    ToastUtil.showShort(context, "对方不允许");
                } else {
                    addBuddy();
                }
            } else if (type == ADD_BUDDY) {
                ToastUtil.showShort(context, "添加好友成功");
            }
        }
    }

    class GroupInfoHandler extends RequestHandler {

        public static final int GET_VERIFY = 1;
        public static final int ADD_GROUP = 2;
        public static final int GET_INFO = 3;
        private int type;

        public GroupInfoHandler(int type, Context context) {
            super(context);
            this.type = type;
        }

        @Override
        public void handleSuccess(Message msg) {
            if (type == GET_VERIFY) {
                byte verifyType = msg.getData().getByte(KEY_DATA);
                if (verifyType != 1) {
                    addGroup();
                } else {
                    ToastUtil.showShort(context, "该群不允许申请");
                }
            } else if (type == ADD_GROUP) {

            } else if (type == GET_INFO) {

            }
        }

        private void addGroup() {
            final String verifyInfo = RequestHelper.getMyInfo().getName();
            requestHandler = new GroupInfoHandler(GroupInfoHandler.ADD_GROUP, context);
            requestHandler.sendEmptyMessage(RequestHandler.SHOW_PRO);
            boolean add = RequestHelper.addGroup(groupID, verifyInfo, requestHandler);
            if (!add) {
                requestHandler.sendEmptyMessage(RequestHandler.REQUEST_FALSE);
            }
        }
    }
}
