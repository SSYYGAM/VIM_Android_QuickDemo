package com.vrv.sdk.library.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vrv.imsdk.model.ChatMsg;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.listener.ItemDataChangeListener;
import com.vrv.sdk.library.listener.OnReSendChatMsgListener;
import com.vrv.sdk.library.utils.DateTimeUtils;


/**
 * Created by Yang on 2015/8/15 015.
 */
public abstract class ChatMessageView extends LinearLayout implements View.OnClickListener, View.OnLongClickListener {

    private final String TAG = ChatMessageView.class.getSimpleName();

    protected Context context;
    protected ChatMsg messageBean;
    protected boolean showName = false;//显示名称
    private boolean encrypt = false;//消息是否加密
    private boolean readBurn = false;//是否为阅后即焚

    protected OnReSendChatMsgListener reSendListener;

    public void setReSendListener(OnReSendChatMsgListener listener) {
        this.reSendListener = listener;
    }

    private MsgStatus status = MsgStatus.NORMAL;

    public enum MsgStatus {
        NORMAL,//正常消息
        REMIND,//提醒消息
        DELAY//延时消息
    }

    protected TextView tvFromName;//发消息人
    protected LinearLayout llMsgStatusTime;//延时提醒消息时间
    protected TextView tvMsgStatus;//延迟或提醒
    protected TextView tvMsgStatusTime;//延时提醒消息时间
    protected FrameLayout flMsg;//
    protected ChatMsgItemView itemView;
    protected ProgressBar proSend;//发送消息

    public ChatMessageView(Context context) {
        super(context);
        initView(context);
    }

    public ChatMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ChatMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    protected abstract View loadContentView();

    private void initView(Context context) {
        this.context = context;
        View view = loadContentView();
        findViews(view);

    }

    private void findViews(View view) {
        //点击消息控件（包括所有的消息类型）
        flMsg = (FrameLayout) view.findViewById(R.id.fl_chat_content);
        //显示名称/昵称
        tvFromName = (TextView) view.findViewById(R.id.tv_chat_fromName);
        //消息活动类型（延迟。提醒）
        llMsgStatusTime = (LinearLayout) view.findViewById(R.id.ll_chat_remindTime);
        tvMsgStatusTime = (TextView) view.findViewById(R.id.tv_chat_delayOrRemind_time);
        tvMsgStatus = (TextView) view.findViewById(R.id.tv_chat_delayOrRemind);
        proSend = (ProgressBar) view.findViewById(R.id.progress_chat_send);
    }

    /**
     * 设置消息展示
     *
     * @param encrypt  加密消息
     * @param readBurn 阅后即焚
     */
    public void setViews(ChatMsg messageBean, boolean encrypt, boolean readBurn) {
        this.messageBean = messageBean;
        this.encrypt = encrypt;
        this.readBurn = readBurn;
        setProSend();//我发出去的消息可能失败，需要重新计算出msgType
        setName();
        setMsgStatus();
        setBackground();
        setMsgDisplay();
        setListeners();
    }

    protected void setProSend() {
        //在ChatMessageToView中设置
    }

    /**
     * 显示聊天人名称
     */
    protected abstract void setName();

    protected abstract void setBackground();

    /**
     * 设置消息延迟或提醒时间
     */
    private void setMsgStatus() {
        if (status == MsgStatus.NORMAL) {
            llMsgStatusTime.setVisibility(View.GONE);
        } else {
            llMsgStatusTime.setVisibility(View.VISIBLE);
            tvMsgStatusTime.setText(DateTimeUtils.formatDateWeek(context, System.currentTimeMillis()));
            if (status == MsgStatus.DELAY) {
                tvMsgStatus.setText(R.string.vim_chat_delay_time);
            } else if (status == MsgStatus.REMIND) {
                tvMsgStatus.setText(R.string.vim_chat_remind_time);
            }
        }
    }

    /**
     * 设置消息展示
     */
    private void setMsgDisplay() {
        if (encrypt) {
        } else {
            if (readBurn) {
            } else {
                displayNormalMsg();
            }
        }
    }

    /**
     * 显示正常消息
     */
    protected void displayNormalMsg() {
        flMsg.removeAllViews();
        itemView = ChatMsgItemFactory.createItemView(context, messageBean);
        flMsg.addView(itemView);
    }

    @Override
    public void onClick(View v) {
        itemView.onClick();
    }

    @Override
    public boolean onLongClick(View view) {
        itemView.onLongClick();
        return true;
    }

    private void clickMsg() {
        if (readBurn) {
            //// TODO: 展示弹出框？ or 读秒继续展示
            setMsgDisplay();
        } else {
            if (encrypt) {
            } else {
                itemView.onClick();
            }
        }
    }

    protected void setListeners() {
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        itemView.setItemDataChangeListener(new ItemDataChangeListener() {
            @Override
            public void ItemDataChange(boolean isShowCheckbox) {
                itemDataChangeListener.ItemDataChange(isShowCheckbox);
            }

            @Override
            public void onItemOperation(int type, ChatMsg msg) {
                itemDataChangeListener.onItemOperation(type, msg);
            }
        });

    }

    //长按消息体，操作消息需要更新页面，控制adapter，notifyDataSetChanged；此处暂时只传递值，
    protected ItemDataChangeListener itemDataChangeListener;

    public void setItemDataChangeListener(ItemDataChangeListener listener) {
        if (listener != null) {
            this.itemDataChangeListener = listener;
        }
    }
}
