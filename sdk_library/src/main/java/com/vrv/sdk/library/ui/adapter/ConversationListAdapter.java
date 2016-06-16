package com.vrv.sdk.library.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.model.Chat;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.utils.ChatMsgUtil;
import com.vrv.sdk.library.utils.ImageUtil;
import com.vrv.sdk.library.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友列表Adapter
 * Created by Yang on 2015/8/28 028.
 */
public class ConversationListAdapter extends BaseAdapter {

    private final String TAG = ConversationListAdapter.class.getSimpleName();

    private Context context;
    private List<Chat> chatList = new ArrayList<>();

    public ConversationListAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public int getCount() {
        if (chatList == null)
            return 0;
        else
            return chatList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.vim_item_conversation, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.icon = (ImageView) convertView.findViewById(R.id.icon);
        holder.nameTv = (TextView) convertView.findViewById(R.id.tv_name);
        holder.timeTv = (TextView) convertView.findViewById(R.id.tv_time);
        holder.unreadTv = (TextView) convertView.findViewById(R.id.tv_unread);
        holder.msgTv = (TextView) convertView.findViewById(R.id.tv_message);
        Chat chat = chatList.get(position);
        holder.nameTv.setText(chat.getName());
        holder.timeTv.setText(TimeUtils.timeStamp2Date(chat.getTime(), 8));
        holder.msgTv.setText(ChatMsgUtil.lastMsgBrief(context, chat.getMsgType(), chat.getLastMsg()));
        if (ChatMsgApi.isGroup(chat.getId())) {
            ImageUtil.loadViewLocalHead(context, chat.getAvatar(), holder.icon, R.mipmap.vim_icon_default_group);
        } else if (ChatMsgApi.isSysMsg(chat.getId())) {
            holder.nameTv.setText("系统消息");
            ImageUtil.loadViewLocalHead(context, chat.getAvatar(), holder.icon, R.mipmap.vim_icon_default_system);
        } else {
            ImageUtil.loadViewLocalHead(context, chat.getAvatar(), holder.icon, R.mipmap.vim_icon_default_user);
        }
        if (chat.getUnReadNum() > 0) {
            holder.unreadTv.setVisibility(View.VISIBLE);
            holder.unreadTv.setText(String.valueOf(chat.getUnReadNum())+ "");
        } else {
            holder.unreadTv.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView icon;
        TextView nameTv;
        TextView timeTv;
        TextView msgTv;
        TextView unreadTv;

        ViewHolder(View view) {
        }
    }
}