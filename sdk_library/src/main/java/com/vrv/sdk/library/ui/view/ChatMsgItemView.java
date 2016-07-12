package com.vrv.sdk.library.ui.view;

import android.content.Context;
import android.widget.LinearLayout;

import com.vrv.imsdk.model.ChatMsg;

/**
 * Created by Yang on 2015/11/3 003.
 */
public abstract class ChatMsgItemView extends LinearLayout {

    private final String TAG = ChatMsgItemView.class.getSimpleName();

    protected Context context;
    protected ChatMsg msgBean;
    protected int type;
    protected String encryptKey;

    public ChatMsgItemView(Context context, ChatMsg msgBean) {
        super(context);
        this.msgBean = msgBean;
        this.type = msgBean.getMessageType();
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        loadView();
        handleData();
        display();
    }

    protected abstract void loadView();

    protected abstract void handleData();

    protected abstract void display();

    protected abstract void onClick();

    protected abstract void onLongClick();

    //监听itemview的长按事件，将操作结果返回上去，控制adapter，notifyDataSetChanged；isShowCheckbox，多选控制显示checkbox
    protected ItemDataChangeListener itemDataChangeListener;

    public interface ItemDataChangeListener {
        void ItemDataChange(boolean isShowCheckbox);

        void onItemOperation(int type, ChatMsg msg);
    }

    public void setItemDataChangeListener(ItemDataChangeListener listener) {
        if (listener != null) {
            this.itemDataChangeListener = listener;
        }
    }
}
