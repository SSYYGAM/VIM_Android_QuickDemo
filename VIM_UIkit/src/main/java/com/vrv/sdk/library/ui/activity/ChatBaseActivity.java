package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.vrv.sdk.library.utils.ToastUtil;
import com.vrv.sdk.library.utils.VrvLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sj.keyboard.widget.FuncLayout;

/**
 * 聊天界面
 * Created by zxj on 16-7-28.
 */
public class ChatBaseActivity extends BaseActivity implements FuncLayout.OnFuncKeyBoardListener {
    private static final String TAG = ChatBaseActivity.class.getSimpleName();
    private static final String KEY_INFO = "BaseInfo";
    //聊天对象ID
    private static long chatID = 0;
    private BaseInfoBean baseInfo;
    private boolean isGroupChat;
    private boolean isRefreshing;//是否加载历史聊天记录
    private Group group;//群详细信息
    private static ArrayList<GroupMember> memberList;
    //Note:监听ListView是否在底部，滑动看历史消息后接收到新消息不滑动到最底部
    private boolean isBottom = true;
    private static ArrayList<ChatMsg> msgList = new ArrayList<>();

    private ArrayList<Long> relateUsers = new ArrayList<>();
    public static HashMap<Long, String> atUserMap = new HashMap<Long, String>();

    private static final int FILE_SELETE_REQUEST_CODE = 1200;
    //打开群设置，退出群或者删除群需要关闭当前页面
    private final int REQUEST_GROUP = 11;
    //发送@回掉选中的联系人
    public static final int SELECT_GROUP_MEMBER = 12;
    //清空聊天记录列表
    public static final int RESULTCODE_CLEAR_DATA = 1100;
    // 拍照返回
    public static final int TAKEPHOTO_CODE = 1101;
    private final int CROP_CODE = 1102;
    public static final int TYPE_FORWORD_MSG = 1103;
    public static String takePhotoPath = "";

    private SwipeRefreshLayout refreshLayout;
    private long lastMsgID = 0;
    private final int msgOffSet = 12;
    private long maxMsgID = 0;

    protected MessageListView messageListView;
    private boolean groupChat;//是否群聊
    //是否为阅后即焚消息
    public static boolean isBurn;
    private EditText useEdit;


    public static void start(Context context, BaseInfoBean baseInfoBean, boolean qqLayout) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (qqLayout) {
            intent.setClass(context, ChatQqActivity.class);
        } else {
            intent.setClass(context, ChatWxActivity.class);
        }
        intent.putExtra(KEY_INFO, baseInfoBean);
        context.startActivity(intent);
    }

    public static void start(Activity context, BaseInfoBean baseInfoBean, boolean qqLayout) {
        Intent intent = new Intent();
        if (qqLayout) {
            intent.setClass(context, ChatQqActivity.class);
        } else {
            intent.setClass(context, ChatWxActivity.class);
        }
        intent.putExtra(KEY_INFO, baseInfoBean);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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
        if (!groupChat) {
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


    private void getHistoryMsg() {
        isRefreshing = true;
        requestHandler = new ChatHandler(ChatHandler.TYPE_GET_HISTORY);
        RequestHelper.getChatHistory(chatID, lastMsgID, msgOffSet, requestHandler);
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
        toolbar.setTitle(baseInfo.getName());
    }

    @Override
    protected void loadContentLayout() {

    }

    @Override
    protected void findViews() {
        messageListView = (MessageListView) contentView.findViewById(R.id.message_recycler);
    }

    @Override
    protected void setViews() {
        initData();
        getHistoryMsg();
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View view) {

    }

    //发送文本消息
    protected void sendTxt(EditText inputEt) {
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
        if (atUserMap != null) {
            atUserMap.clear();
        }
    }

    /**
     * @param inputEt
     * @param btnSend 用于微信界面中 发送和更多的切换
     * @param btnPlug
     */
    protected void inputEtListener(final EditText inputEt, final Button btnSend, final ImageView btnPlug) {
        inputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {// 文本框输入内容
                    String substring = s.toString().substring(start, start + count);
                    if (groupChat && substring.equals("@")) {
                        useEdit = inputEt;
                        SelectGroupMemberActivity.startForResult((ChatBaseActivity) context, SELECT_GROUP_MEMBER);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (btnPlug == null || btnSend == null)
                    return;
                if (s.toString().trim().length() > 0) {
                    btnPlug.setVisibility(View.GONE);
                    btnSend.setVisibility(View.VISIBLE);
                } else {
                    btnPlug.setVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.GONE);
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
    }

    @Override
    public void OnFuncPop(int i) {
        scrollToBottom();
    }

    @Override
    public void OnFuncClose() {

    }

    protected void scrollToBottom() {
        messageListView.requestLayout();
        messageListView.post(new Runnable() {
            @Override
            public void run() {
                if (messageListView.adapter.getItemCount() > 0) {
                    messageListView.smoothScrollToPosition(messageListView.adapter.getItemCount() - 1);
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
                RequestHelper.sendPosition(chatID, location.getAddress(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new ChatHandler());
                break;
            case REQUEST_GROUP://群退出删除解散操作后关闭页面
                finish();
                break;
            case SELECT_GROUP_MEMBER:
                GroupMember groupMember = data.getParcelableExtra("data");
                insertAT2Input(groupMember.getId(), groupMember.getName());
                break;
            case TAKEPHOTO_CODE:
                PhotosCropActivity.startForResult(activity, takePhotoPath, CROP_CODE);
                break;
            case CROP_CODE:
                ArrayList<String> paths = new ArrayList<>();
                paths.add(data.getStringExtra("data"));
                for (String photo : paths) {
                    RequestHelper.sendImg(chatID, photo, new ChatHandler(0));
                }
                break;
            case TYPE_FORWORD_MSG:
                //                transferMsg(data.getLongExtra("data", 0),ChatAdapter.selectMsgs);
                break;
            case FILE_SELETE_REQUEST_CODE:
                FileBean fileBean = data.getParcelableExtra("data");
                RequestHelper.sendFile(chatID, fileBean.getPath(), new ChatHandler());

                break;
        }
    }

    //输入框插入@数据
    public void insertAT2Input(long userID, String atName) {
        if (useEdit == null)
            return;
        if (useEdit.getText().toString().contains("@")) {
            int index = useEdit.getSelectionStart();
            if (index > 0) {
                if ('@' == (useEdit.getText().charAt(index - 1))) {
                    useEdit.getText().insert(index, atName + " ");
                    if (atUserMap == null) {
                        atUserMap = new HashMap<>();
                    }
                    atUserMap.put(userID, atName);
                }
            }
        }
    }

    private class ChatHandler extends RequestHandler {
        public static final int TYPE_GROUP_INFO = 1;
        public static final int TYPE_GROUP_MEMBER = 2;
        public static final int TYPE_GROUP_MEMBER_INFO = 3;
        public static final int TYPE_MSG_WITHDRAW = 4;
        public static final int TYPE_GET_URL_INFO = 5;
        public static final int TYPE_GET_HISTORY = 6;
        public static final int TYPE_SEND_MSG = 7;
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
            messageListView.scrollToPosition(messageListView.getData().size() - 1);
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
}
