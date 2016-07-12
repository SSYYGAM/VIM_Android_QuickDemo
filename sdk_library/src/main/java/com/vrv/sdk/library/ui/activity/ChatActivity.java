package com.vrv.sdk.library.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vrv.imsdk.SDKClient;

import com.vrv.imsdk.model.ChatMsg;

import com.vrv.imsdk.model.ChatMsgService;
import com.vrv.imsdk.model.Contact;
import com.vrv.imsdk.model.GroupMember;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.bean.BaseInfoBean;
import com.vrv.sdk.library.bean.OptionBean;
import com.vrv.sdk.library.ui.adapter.ChatAdapter;
import com.vrv.sdk.library.utils.ImageUtil;
import com.vrv.sdk.library.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();
    private Context context;
    private ListView listView;
    private EditText mSendEdt;
    private KPSwitchPanelLinearLayout mPanelRoot;
    private TextView mSendImgTv;
    private TextView mSendBurnTv;
    private TextView mTakeImgTv;
    private ImageView mPlusIv;
    private Toolbar toolbar;
    private TextView mSendBtn;

    private ChatAdapter mChatAdapter;
    private static final String KEY_INFO = "BaseInfo";
    private static long chatID = 0;
    private BaseInfoBean baseInfo;
    private boolean isRefreshing;//是否加载历史聊天记录
    private String photoPath;
    private static ArrayList<GroupMember> memberList = new ArrayList<>();
    private static ArrayList<ChatMsg> msgList = new ArrayList<>();
    public static boolean isBurn = false;

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
        if (memberList == null) {
            memberList = new ArrayList<>();
        }
        return memberList;
    }

    public static ArrayList<ChatMsg> getMsgList() {
        if (msgList == null) {
            msgList = new ArrayList<>();
        }
        return msgList;
    }

    public static GroupMember getMemberBean(long userID) {
        for (GroupMember bean : getMemberList()) {
            if (bean.getId() == userID) {
                return bean;
            }
        }
        return null;
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
        isBurn = false;
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
        msgList.clear();
        memberList.clear();
        if (mChatAdapter == null) {
            mChatAdapter = new ChatAdapter(context, msgList, baseInfo);
        }
        listView.setAdapter(mChatAdapter);
        if (msgList.size() > 0) {
            listView.setSelection(msgList.size() - 1);
        }
        getHistoryMsg();
    }

    private void findViews() {
        listView = (ListView) findViewById(R.id.content_ryv);
        mSendEdt = (EditText) findViewById(R.id.send_edt);
        mPanelRoot = (KPSwitchPanelLinearLayout) findViewById(R.id.panel_root);
        mTakeImgTv = (TextView) findViewById(R.id.take_img_tv);
        mSendImgTv = (TextView) findViewById(R.id.send_img_tv);
        mSendBurnTv = (TextView) findViewById(R.id.send_burn_tv);
        mPlusIv = (ImageView) findViewById(R.id.plus_iv);
        mSendBtn = (TextView) findViewById(R.id.send_btn);
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
                    RequestHelper.sendTxt(chatID, mSendEdt.getText().toString(), null, new SendMsgHandler());
                    mSendEdt.getText().clear();
                }
            }
        });
        mTakeImgTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPath = ImageUtil.takePic((Activity) context, OptionBean.TYPE_TAKE_PIC);
            }
        });
        mSendImgTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotosThumbnailActivity.startForResult((ChatActivity) context, OptionBean.TYPE_PIC);
            }
        });
        mSendBurnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBurn = !isBurn;
                if (isBurn) {
                    mSendEdt.setBackgroundResource(R.drawable.vim_chatting_edit_burn_bg);
                    mSendBtn.setBackgroundResource(R.drawable.vim_chatting_send_burn_bg);
                } else {
                    mSendEdt.setBackgroundResource(R.drawable.vim_chatting_edit_bg);
                    mSendBtn.setBackgroundResource(R.drawable.vim_chatting_send_bg);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK /*|| data == null*/)
            return;
        if (requestCode == OptionBean.TYPE_PIC) {// 返回值自定 ，得到图片路径
            ArrayList<String> imgList = data.getStringArrayListExtra("data");
            for (String path : imgList) {
                RequestHelper.sendImg(chatID, path, new SendMsgHandler());
            }
        } else if (requestCode == OptionBean.TYPE_TAKE_PIC) {
            File file = new File(photoPath);
            if (file.exists()) {
                RequestHelper.sendImg(chatID, photoPath, new SendMsgHandler());
            }
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
                        Log.d(TAG, String.format("Keyboard is %s", isShowing ? "showing" : "hiding"));
                    }
                });

        KPSwitchConflictUtil.attach(mPanelRoot, mPlusIv, mSendEdt);
    }

    //发送
    class SendMsgHandler extends RequestHandler {

        @Override
        public void handleSuccess(Message msg) {

        }

        @Override
        public void handleFailure(int code, String message) {
            super.handleFailure(code, message);
        }
    }

    private void setNotifyListener() {
        SDKClient.instance().getChatMsgService().setReceiveListener(chatID, new ChatMsgService.OnReceiveChatMsgListener() {
            @Override
            public void onReceive(ChatMsg msg) {
                addChatMsg(msg);
            }

            @Override
            public void onUpdate(ChatMsg msg) {
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

    private long lastMsgID = 0;
    private final int msgOffSet = 12;
    protected RequestHandler requestHandler;

    private void getHistoryMsg() {
        isRefreshing = true;
        requestHandler = new HistoryMsgHandler();
        RequestHelper.getChatHistory(chatID, lastMsgID, msgOffSet, requestHandler);
    }

    class HistoryMsgHandler extends RequestHandler {

        @SuppressLint("NewApi")
        @Override
        public void handleSuccess(Message msg) {
            isRefreshing = false;
            ArrayList<ChatMsg> list = msg.getData().getParcelableArrayList(KEY_DATA);
            if (mChatAdapter != null && list != null && list.size() > 0) {
                int size = list.size();
                msgList.addAll(0, list);
                mChatAdapter.notifyDataSetChanged();
                if (lastMsgID == 0) {
                    listView.setSelection(msgList.size() - 1);
                }
                lastMsgID = list.get(0).getMessageID() - 1;
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
}