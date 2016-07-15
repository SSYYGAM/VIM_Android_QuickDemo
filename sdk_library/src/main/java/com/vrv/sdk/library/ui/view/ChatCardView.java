package com.vrv.sdk.library.ui.view;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.vrv.imsdk.SDKClient;
import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.api.MsgCard;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.imsdk.model.Contact;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.VimConstant;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.ui.activity.ContactDetailActivity;
import com.vrv.sdk.library.utils.DialogUtil;
import com.vrv.sdk.library.utils.ImageUtil;
import com.vrv.sdk.library.utils.VrvLog;

/**
 * 聊天名片
 */
public class ChatCardView extends ChatMsgItemView {

    private TextView tvCard;
    private ImageView imgIcon;
    private MsgCard msgCard;
    private long userID;

    public ChatCardView(Context context, ChatMsg msgBean) {
        super(context, msgBean);
    }

    @Override
    protected void loadView() {
        View view = View.inflate(context, R.layout.vim_view_chat_card, this);
        tvCard = (TextView) view.findViewById(R.id.tv_chat_cardInfo);
        imgIcon = (ImageView) view.findViewById(R.id.img_chat_cardIcon);
    }

    @Override
    protected void handleData() {
        msgCard = ChatMsgApi.parseCardJson(msgBean.getMessage());
        if (msgCard != null) {
            try {
                userID = Long.valueOf(msgCard.getMediaUrl());
            } catch (Exception e) {
                VrvLog.e("mediaUrl ---> UserID exception:" + msgCard.getMediaUrl());
            }
        }
    }

    @Override
    protected void display() {
        if (userID != 0) {
            Contact contact = SDKClient.instance().getContactService().findItemByID(userID);
            if (contact == null) {
                RequestHelper.getUserInfo(userID, new CardHandler());
            } else {
                setViews(contact.getName(), contact.getAvatar());
            }
        }
    }

    private void setViews(String name, String avatar) {
        tvCard.setText(name);
        ImageUtil.loadViewLocalHead(context, avatar, imgIcon, R.mipmap.vim_icon_default_user);
    }

    @Override
    protected void onClick() {
        ContactDetailActivity.start(context, userID);
    }

    @Override
    protected void onLongClick() {
        if (RequestHelper.isMyself(msgBean.getSendID()))
            isMe = true;
        CharSequence[] items;
        if (isMe) {
            items = new CharSequence[]{"转发", "收藏", "删除", "更多"};
        } else {
            items = new CharSequence[]{"转发", "收藏", "删除", "撤回", "更多"};
        }
        final MaterialDialog.ListCallback itemOperateCallBack = new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                switch (i) {
                    case 0:
                        itemDataChangeListener.onItemOperation(VimConstant.TYPE_MSG_FORWARD, msgBean);
                        break;
                    case 1:
                        itemDataChangeListener.onItemOperation(VimConstant.TYPE_MSG_COLLECTION, msgBean);
                        break;
                    case 2:
                        itemDataChangeListener.onItemOperation(VimConstant.TYPE_MSG_DELETE, msgBean);
                        break;
                    case 3:
                        if (isMe) {
                            itemDataChangeListener.ItemDataChange(true);
                        } else {
                            itemDataChangeListener.onItemOperation(VimConstant.TYPE_MSG_WITHDRAW, msgBean);
                        }
                        break;
                    default:
                        itemDataChangeListener.ItemDataChange(true);
                        break;
                }
            }
        };
        DialogUtil.buildOperateDialog(context, items, itemOperateCallBack).show();
    }

    class CardHandler extends RequestHandler {

        @Override
        public void handleSuccess(Message msg) {
            Contact contact = msg.getData().getParcelable(KEY_DATA);
            if (contact != null) {
                setViews(contact.getName(), contact.getAvatar());
            }
        }
    }
}
