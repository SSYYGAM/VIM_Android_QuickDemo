package com.vrv.sdk.library.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.bean.BaseInfoBean;
import com.vrv.sdk.library.listener.OnReSendChatMsgListener;
import com.vrv.sdk.library.ui.view.ChatMessageView;
import com.vrv.sdk.library.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.vrv.sdk.library.utils.TimeUtils.timeStamp2Date;

public class ChatAdapter extends BaseAdapter {

    private static final int TYPE_From_Msg = 0;
    private static final int TYPE_To_Msg = 1;
    private Context activity;
    private List<ChatMsg> msgList = new ArrayList<ChatMsg>();
    private OnReSendChatMsgListener reSendListener;
    private BaseInfoBean baseInfoBean;

    public void setReSendListener(OnReSendChatMsgListener listener) {
        this.reSendListener = listener;
    }

    public ChatAdapter(Context activity, List<ChatMsg> msgList, BaseInfoBean baseInfoBean) {
        this.activity = activity;
        this.msgList = msgList;
        this.baseInfoBean = baseInfoBean;
    }

    @Override
    public int getItemViewType(int position) {
        long senderID = msgList.get(position).getSendID();
        return RequestHelper.isMyself(senderID) ? TYPE_To_Msg : TYPE_From_Msg;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        if (msgList == null)
            return 0;
        else
            return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ChatMsg chatMsg = msgList.get(position);
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_From_Msg:
                    convertView = LayoutInflater.from(activity).inflate(R.layout.vim_chat_from_item, null);
                    holder = new ViewHolder(convertView);
                    break;
                case TYPE_To_Msg:
                    convertView = LayoutInflater.from(activity).inflate(R.layout.vim_chat_to_item, null);
                    holder = new ViewHolder(convertView);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mMessageFromDate = (TextView) convertView.findViewById(R.id.message_from_date);
        holder.mImageView = (ImageView) convertView.findViewById(R.id.imageView);
        holder.mChatFromName = (TextView) convertView.findViewById(R.id.chat_from_name);
        holder.viewMessage = (ChatMessageView) convertView.findViewById(R.id.view_chat_message);

        if (ChatMsgApi.isUser(chatMsg.getTargetID())) {
            String path;
            if (RequestHelper.isMyself(chatMsg.getSendID())) {
                path = RequestHelper.getMyInfo().getAvatar();
            } else {
                path = baseInfoBean.getIcon();
            }
            Utils.loadHead(activity, path, holder.mImageView, R.mipmap.vim_icon_default_user);
        }
        holder.mMessageFromDate.setText(timeStamp2Date(chatMsg.getSendTime(), 3));
        holder.viewMessage.setViews(chatMsg, false, false);
        holder.viewMessage.setReSendListener(reSendListener);
        return convertView;
    }

    static class ViewHolder {
        TextView mMessageFromDate;
        ImageView mImageView;
        TextView mChatFromName;
        ChatMessageView viewMessage;

        ViewHolder(View view) {
        }
    }
}
