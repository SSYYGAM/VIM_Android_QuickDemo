package com.vrv.sdk.library.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
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
import com.vrv.sdk.library.chat.MessageListView;
import com.vrv.sdk.library.ui.adapter.ChatOptionsPagerAdapter;
import com.vrv.sdk.library.chat.view.ChatInputOptionView;
import com.vrv.sdk.library.utils.ToastUtil;
import com.vrv.sdk.library.utils.VrvLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout;

public class ChatActivity extends BaseActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final String KEY_INFO = "BaseInfo";
    //是否为阅后即焚消息
    public static boolean isBurn;
    //群成员列表
    private static ArrayList<GroupMember> memberList;

    private MessageListView messageListView;
    private EditText inputEt;
    private KPSwitchPanelLinearLayout panelRootKP;
    private ImageView optionImg;
    private TextView sendTx;
    private ChatInputOptionView chatInputOptionView;

    private static long chatID = 0;
    private boolean groupChat ;//是否群聊
    private BaseInfoBean baseInfo;
    private long lastMsgID = 0;
    private final int msgOffSet = 18;//单次拉取历史记录个数
    private long maxMsgID = 0;
    private Map<Long, String> atUserMap;//@ 相关

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
        groupChat = ChatMsgApi.isGroup(chatID);
        if (!groupChat){
            messageListView.setContact(baseInfo);
        }
    }

    //清除数据
    private void clearData() {
        isBurn = false;
        if (memberList != null) {
            memberList.clear();
            memberList = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNotifyListener();
        //获取群信息
        if (groupChat) {
            getMembers();
        }
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

    @Override
    protected void setToolBar() {
        //baseActivity的toolbar不用，使用keyBoard第三方库，需要把toolbar包含进去
        toolbar.setVisibility(View.GONE);
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
    }

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context, R.layout.vim_activity_chat, null);
        contentLayout.addView(contentView);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void findViews() {
        messageListView = (MessageListView) contentView.findViewById(R.id.message_recycler);
        inputEt = (EditText) contentView.findViewById(R.id.send_edt);
        panelRootKP = (KPSwitchPanelLinearLayout) contentView.findViewById(R.id.panel_root);
        optionImg = (ImageView) contentView.findViewById(R.id.plus_iv);
        sendTx = (TextView) contentView.findViewById(R.id.send_btn);
        chatInputOptionView = (ChatInputOptionView) contentView.findViewById(R.id.chat_option);

        toolbar = (Toolbar) contentView.findViewById(R.id.chat_title);
        toolbar.setTitle(baseInfo.getName());
        toolbar.setTitleTextAppearance(context, R.style.Vim_ToolbarStyle);
        toolbar.setNavigationIcon(R.mipmap.vim_action_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void setViews() {
        keyboardSetting();
        initData();
        getHistoryMsg();
    }

    @Override
    protected void setListener() {
        inputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {// 文本框输入内容
                    String substring = s.toString().substring(start, start + count);
                    if (groupChat && substring.equals("@")) {
                        SelectGroupMemberActivity.startForResult((ChatActivity) context, TYPE_SELECT_MEMBER);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0) {
                    optionImg.setVisibility(View.GONE);
                    sendTx.setVisibility(View.VISIBLE);
                } else {
                    optionImg.setVisibility(View.VISIBLE);
                    sendTx.setVisibility(View.GONE);
                }
            }
        });
        inputEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (atUserMap == null || atUserMap.isEmpty()) {
                    return false;
                }
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) { //当为删除键并且是按下动作时执行
                    int selectionStart = inputEt.getSelectionStart();
                    int lastPosition = 0;
                    String tempString = inputEt.getText().subSequence(0, selectionStart).toString();
                    for (Map.Entry<Long, String> entry : atUserMap.entrySet()) {
                        if ((lastPosition = tempString.indexOf(entry.getValue(), lastPosition)) != -1) {
                            if (selectionStart != 0 && selectionStart >= lastPosition && selectionStart <= (lastPosition + entry.getValue().length())) {
                                inputEt.getText().delete(lastPosition, selectionStart);
                                atUserMap.remove(entry.getKey());
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
        sendTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTxt();
            }
        });
        messageListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    KPSwitchConflictUtil.hidePanelAndKeyboard(panelRootKP);
                }
                return false;
            }
        });
        chatInputOptionView.setOptionListener(new ChatOptionsPagerAdapter.OnOptionListener() {
            @Override
            public void onClick(int position, int type) {
                if (type == VimConstant.TYPE_BURN) {
                    isBurn = true;
                    inputEt.setBackgroundResource(R.drawable.vim_chatting_edit_burn_bg);
                    sendTx.setBackgroundResource(R.drawable.vim_chatting_send_burn_bg);
                } else if (type == VimConstant.TYPE_BURN_CANCEL) {
                    isBurn = false;
                    inputEt.setBackgroundResource(R.drawable.vim_chatting_edit_bg);
                    sendTx.setBackgroundResource(R.drawable.vim_chatting_send_bg);
                }
            }
        });
    }

    //发送文本消息
    private void sendTxt() {
        String text = inputEt.getText().toString();
        if (TextUtils.isEmpty(text.trim())) {
            VrvLog.e(TAG, "send Text is null!");
            return;
        }
        ArrayList<Long> relaterUsers = new ArrayList<>();
        if (atUserMap != null && !atUserMap.isEmpty()) {
            for (Map.Entry<Long, String> entry : atUserMap.entrySet()) {
                relaterUsers.add(entry.getKey());
            }
        }
        RequestHelper.sendTxt(chatID, inputEt.getText().toString(), relaterUsers, new ChatHandler(ChatHandler.TYPE_SEND_MSG));
        inputEt.getText().clear();
        if (atUserMap !=null ){
            atUserMap.clear();
        }
    }

    private final int TYPE_SELECT_MEMBER = 1001;

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
                RequestHelper.sendPosition(chatID, location.getAddress(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new ChatHandler());
                break;
            case TYPE_SELECT_MEMBER:
                GroupMember groupMember = data.getParcelableExtra("data");
                insertAT2Input(groupMember.getId(), groupMember.getName());
                break;
        }
    }

    //输入框插入@数据
    public void insertAT2Input(long userID, String atName) {
        if (inputEt.getText().toString().contains("@")) {
            int index = inputEt.getSelectionStart();
            if (index > 0) {
                if ('@' == (inputEt.getText().charAt(index - 1))) {
                    inputEt.getText().insert(index, atName + " ");
                    if (atUserMap == null) {
                        atUserMap = new HashMap<>();
                    }
                    atUserMap.put(userID, atName);
                }
            }
        }
    }

    /**
     * 操作键盘 ，实现键盘和操作栏无缝切换
     */
    private void keyboardSetting(){
        KeyboardUtil.attach(this, panelRootKP,
                new KeyboardUtil.OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                    }
                });

        KPSwitchConflictUtil.attach(panelRootKP, optionImg, inputEt);
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
        if (chatMsg.getTargetID() == chatID) {
            messageListView.getData().add(chatMsg);
            messageListView.getAdapter().notifyDataSetChanged();
            messageListView.scrollToPosition(messageListView.getData().size()-1);
        }
    }

    //更新消息发送状态
    private void updateMsgStatus(ChatMsg chatMsg) {
        if (chatMsg == null) {
            return;
        }
        int size = messageListView.getData().size();
        for (int i = size - 1; i >= 0; i--) {
            if (chatMsg.getLocalID() == messageListView.getData().get(i).getLocalID()) {
                messageListView.getData().set(i, chatMsg);
                messageListView.getAdapter().notifyItemChanged(i);
                return;
            }
        }
    }

    //获取历史消息
    private void getHistoryMsg() {
        RequestHelper.getChatHistory(chatID, lastMsgID, msgOffSet, new ChatHandler(ChatHandler.TYPE_GET_HISTORY));
    }

    private void getMembers() {
        if (groupChat) {
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
            if (panelRootKP.getVisibility() == View.VISIBLE) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(panelRootKP);
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
            if (list != null && list.size() > 0) {
                int size = list.size();
                messageListView.getData().addAll(0, list);
                messageListView.getAdapter().notifyDataSetChanged();
                if (lastMsgID == 0) {
                    messageListView.scrollToPosition(size - 1);
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
            messageListView.setMembers(members);
        }
    }
}