package com.vrv.sdk.library.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vrv.imsdk.SDKClient;
import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.imsdk.model.ChatMsgService;
import com.vrv.imsdk.model.Contact;
import com.vrv.imsdk.model.Group;
import com.vrv.imsdk.model.GroupMember;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.VimConstant;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.bean.BaseInfoBean;
import com.vrv.sdk.library.bean.FileBean;
import com.vrv.sdk.library.bean.LocationBean;
import com.vrv.sdk.library.ui.adapter.ChatAdapter;
import com.vrv.sdk.library.ui.adapter.ChatOptionsPagerAdapter;
import com.vrv.sdk.library.ui.view.ChatInputOptionView;
import com.vrv.sdk.library.utils.ToastUtil;

import java.util.ArrayList;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();
    //是否为阅后即焚消息
    public static boolean isBurn;
    //群成员列表
    private static ArrayList<GroupMember> memberList;
    //历史消息
    private static ArrayList<ChatMsg> msgList;

    private Context context;
    private ListView listView;
    private EditText mSendEdt;
    private KPSwitchPanelLinearLayout mPanelRoot;
    private ImageView mPlusIv;
    private Toolbar toolbar;
    private TextView mSendBtn;
    private ChatInputOptionView chatInputOptionView;

    private ChatAdapter mChatAdapter;
    private static final String KEY_INFO = "BaseInfo";
    private static long chatID = 0;
    private BaseInfoBean baseInfo;
    private String photoPath;
    private long lastMsgID = 0;
    private final int msgOffSet = 18;
    private long maxMsgID = 0;

    public static void start(Context context, BaseInfoBean baseInfoBean) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(context, ChatActivity.class);
        intent.putExtra(KEY_INFO, baseInfoBean);
        context.startActivity(intent);
    }

    public static long getChatID() {
        return chatID;
    }

    public static ArrayList<GroupMember> getMemberList() {
        return memberList == null ? new ArrayList<GroupMember>() : memberList;
    }

    public static ArrayList<ChatMsg> getMsgList() {
        return msgList == null ? new ArrayList<ChatMsg>() : msgList;
    }

    public static GroupMember indexMemberByID(long userID) {
        for (GroupMember bean : getMemberList()) {
            if (bean.getId() == userID) {
                return bean;
            }
        }
        return null;
    }

    //初始化数据
    private void initData() {
        isBurn = false;
        memberList = new ArrayList<>();
        msgList = new ArrayList<>();
    }

    //清除数据
    private void clearData() {
        isBurn = false;
        if (memberList != null) {
            memberList.clear();
            memberList = null;
        }
        if (msgList != null) {
            msgList.clear();
            msgList = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vim_activity_chat);
        context = this;
        setToolbar();
        findViews();
        setView();
        operateKeyboard();
        setListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        RequestHelper.setMsgRead(chatID, maxMsgID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearData();
    }

    private void setToolbar() {
        try {
            baseInfo = getIntent().getParcelableExtra(KEY_INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (baseInfo == null) {
            ToastUtil.showShort(context, "数据错误");
            finish();
            return;
        }
        chatID = baseInfo.getID();
        toolbar = (Toolbar) findViewById(R.id.chat_title);
        toolbar.setTitleTextAppearance(context, R.style.Vim_ToolbarStyle);
        toolbar.setTitle(baseInfo.getName());
        toolbar.setNavigationIcon(R.mipmap.vim_action_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setView() {
        initData();
        if (mChatAdapter == null) {
            mChatAdapter = new ChatAdapter(context, msgList, baseInfo);
        }
        listView.setAdapter(mChatAdapter);
        if (msgList.size() > 0) {
            listView.setSelection(msgList.size() - 1);
        }
        getHistoryMsg();
        getMembers();
    }

    private void findViews() {
        listView = (ListView) findViewById(R.id.content_ryv);
        mSendEdt = (EditText) findViewById(R.id.send_edt);
        mPanelRoot = (KPSwitchPanelLinearLayout) findViewById(R.id.panel_root);
        mPlusIv = (ImageView) findViewById(R.id.plus_iv);
        mSendBtn = (TextView) findViewById(R.id.send_btn);
        chatInputOptionView = (ChatInputOptionView) findViewById(R.id.chat_option);
    }

    private void setListener() {
        setNotifyListener();
        mSendEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0) {
                    mPlusIv.setVisibility(View.GONE);
                    mSendBtn.setVisibility(View.VISIBLE);
                } else {
                    mPlusIv.setVisibility(View.VISIBLE);
                    mSendBtn.setVisibility(View.GONE);
                }
            }
        });
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mSendEdt.getText().toString().trim())) {
                    RequestHelper.sendTxt(chatID, mSendEdt.getText().toString(), null, new ChatHandler(ChatHandler.TYPE_SEND_MSG));
                    mSendEdt.getText().clear();
                }
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    KPSwitchConflictUtil.hidePanelAndKeyboard(mPanelRoot);
                }
                return false;
            }
        });
        chatInputOptionView.setOptionListener(new ChatOptionsPagerAdapter.OnOptionListener() {
            @Override
            public void onClick(int position, int type) {
                if (type == VimConstant.TYPE_BURN){
                    isBurn = true;
                    mSendEdt.setBackgroundResource(R.drawable.vim_chatting_edit_burn_bg);
                    mSendBtn.setBackgroundResource(R.drawable.vim_chatting_send_burn_bg);
                }else if (type == VimConstant.TYPE_BURN_CANCEL){
                    isBurn = false;
                    mSendEdt.setBackgroundResource(R.drawable.vim_chatting_edit_bg);
                    mSendBtn.setBackgroundResource(R.drawable.vim_chatting_send_bg);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK /*|| data == null*/)
            return;
        switch (requestCode) {
            case VimConstant.TYPE_PIC://图片返回
                ArrayList<String> imgList = data.getStringArrayListExtra("data");
                for (String path : imgList) {
                    RequestHelper.sendImg(chatID, path, new ChatHandler(0));
                }
                break;
            case VimConstant.TYPE_FILE://文件返回
                FileBean fileDataBean = data.getParcelableExtra("data");
                String path = fileDataBean.getPath();
                RequestHelper.sendFile(chatID, path, new ChatHandler(0));
                break;
            case VimConstant.TYPE_CARD:
                Contact contact = data.getParcelableExtra("data");
                RequestHelper.sendCard(chatID, contact.getId(), new ChatHandler(0));
                break;
            case VimConstant.TYPE_POSITION:
                LocationBean location = data.getParcelableExtra("data");
                RequestHelper.sendPosition(chatID, location.getAddrStr(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new ChatHandler());
                break;
        }

    }

    /**
     * 操作键盘 ，实现键盘和操作栏无缝切换
     */
    private void operateKeyboard() {
        KeyboardUtil.attach(this, mPanelRoot,
                new KeyboardUtil.OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                    }
                });

        KPSwitchConflictUtil.attach(mPanelRoot, mPlusIv, mSendEdt);
    }

    //设置SDK推送消息监听
    private void setNotifyListener() {
        SDKClient.instance().getChatMsgService().setReceiveListener(chatID, new ChatMsgService.OnReceiveChatMsgListener() {
            @Override
            public void onReceive(ChatMsg msg) {
                //TODO 当前聊天界面接收到新消息
                addChatMsg(msg);
            }

            @Override
            public void onUpdate(ChatMsg msg) {
                //TODO 接收到消息更新状态
                updateMsgStatus(msg);
            }
        });
    }

    //添加更新聊天消息
    private void addChatMsg(ChatMsg chatMsg) {
        if (chatMsg == null) {
            return;
        }
        if (chatMsg.getTargetID() == chatID && mChatAdapter != null && msgList != null) {
            int index = msgList.size();
            msgList.add(chatMsg);
            mChatAdapter.notifyDataSetChanged();
        }
    }

    //更新消息发送状态
    private void updateMsgStatus(ChatMsg chatMsg) {
        if (chatMsg == null) {
            return;
        }
        int size = msgList.size();
        for (int i = size - 1; i >= 0; i--) {
            if (chatMsg.getLocalID() == msgList.get(i).getLocalID()) {
                msgList.set(i, chatMsg);
                mChatAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    //获取历史消息
    private void getHistoryMsg() {
        RequestHelper.getChatHistory(chatID, lastMsgID, msgOffSet, new ChatHandler(ChatHandler.TYPE_GET_HISTORY));
    }

    private void getMembers() {
        if (ChatMsgApi.isGroup(chatID)) {
            Group group = SDKClient.instance().getGroupService().findItemByID(chatID);
            if (group != null) {
                RequestHelper.getGroupMembers(group, new ChatHandler(ChatHandler.TYPE_GROUP_MEMBER));
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP &&
                event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mPanelRoot.getVisibility() == View.VISIBLE) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(mPanelRoot);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ChatHandler extends RequestHandler {

        public static final int TYPE_SEND_MSG = 1;
        public static final int TYPE_GET_HISTORY = 2;
        public static final int TYPE_GROUP_MEMBER = 3;
        private int type;

        public ChatHandler() {
            this.type = 0;
        }

        public ChatHandler(int type) {
            this.type = type;
        }

        @Override
        public void handleSuccess(Message msg) {
            if (type == TYPE_GET_HISTORY) {
                handleHistoryMsg(msg);
            } else if (type == TYPE_GROUP_MEMBER) {
                handleMember(msg);
            }
        }

        //历史消息返回结果处理
        private void handleHistoryMsg(Message msg) {
            ArrayList<ChatMsg> list = msg.getData().getParcelableArrayList(KEY_DATA);
            if (mChatAdapter != null && list != null && list.size() > 0) {
                int size = list.size();
                msgList.addAll(0, list);
                mChatAdapter.notifyDataSetChanged();
                if (lastMsgID == 0) {
                    listView.setSelection(msgList.size() - 1);
                    maxMsgID = list.get(size - 1).getMessageID();
                }
                lastMsgID = list.get(0).getMessageID() - 1;
            }
        }

        //群成员列表返回结果处理
        private void handleMember(Message msg) {
            ArrayList<GroupMember> members = msg.getData().getParcelableArrayList(RequestHandler.KEY_DATA);
            memberList.clear();
            memberList.addAll(members);
            if (mChatAdapter != null) {
                mChatAdapter.notifyDataSetChanged();
            }
        }
    }
}