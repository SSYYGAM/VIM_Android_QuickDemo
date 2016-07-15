package com.vrv.sdk.library.ui.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.api.MsgPosition;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.VimConstant;
import com.vrv.sdk.library.utils.DialogUtil;
import com.vrv.sdk.library.utils.ToastUtil;

/**
 * 聊天位置
 * Created by Yang on 2015/11/12 012.
 */
public class ChatPositionView extends ChatMsgItemView {

    private TextView tvPosition;
    private MsgPosition msgPosition;

    public ChatPositionView(Context context, ChatMsg msgBean) {
        super(context, msgBean);
    }

    @Override
    protected void loadView() {
        View view = View.inflate(context, R.layout.vim_view_chat_position, this);
        tvPosition = (TextView) view.findViewById(R.id.tv_chat_position);
    }

    @Override
    protected void handleData() {
        msgPosition = ChatMsgApi.parsePositionJson(msgBean.getMessage());
    }

    @Override
    protected void display() {
        if (msgPosition != null) {
            tvPosition.setText(msgPosition.getPosition());
        }
    }

    @Override
    protected void onClick() {
        if (msgPosition == null) {
            ToastUtil.showShort(context, "没有位置信息");
            return;
        }
//        LocationActivity.start((ChatActivity) context, Double.valueOf(msgPosition.getLatitude()),
//                Double.valueOf(msgPosition.getLongitude()), msgPosition.getPosition());

    }

    @Override
    protected void onLongClick() {
        CharSequence[] items = new CharSequence[]{"转发", "收藏", "删除", "更多"};
        MaterialDialog.ListCallback itemOperateCallBack = new MaterialDialog.ListCallback() {
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
                        itemDataChangeListener.ItemDataChange(true);
                        break;
                }
            }
        };
        DialogUtil.buildOperateDialog(context, items, itemOperateCallBack).show();
    }
}
