package com.vrv.sdk.library.conversation;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.model.Chat;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.common.UserInfoConfig;
import com.vrv.sdk.library.common.adapter.BaseRecyclerAdapter;
import com.vrv.sdk.library.common.adapter.BaseRecyclerViewHolder;
import com.vrv.sdk.library.utils.ChatMsgUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 最近联系人Adapter
 */
public class ConversationListAdapter extends BaseRecyclerAdapter<ConversationListAdapter.ConversationViewHolder> {

    private Context context;
    private List<Chat> conversationList = new ArrayList<>();
    private Map<Long, Boolean> atMap = new HashMap<>();
    private ConversationViewHolder viewHolder;

    public ConversationListAdapter(Context context, List<Chat> conversationList) {
        this.context = context;
        if (conversationList != null) {
            this.conversationList = conversationList;
        }
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConversationViewHolder(View.inflate(context, R.layout.vim_item_conversation, null));
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        viewHolder = (ConversationViewHolder) holder;
        bindOnItemClickListener(viewHolder, position);

        Chat bean = conversationList.get(position);

        setTop(bean.isTop());
        //设置时间
        viewHolder.time.setVisibility(View.VISIBLE);
        viewHolder.time.setText(UserInfoConfig.formatConversationTime(bean.getTime()));

        //设置未读
        setUnread(bean.getUnReadNum());
        setWhereFrom(bean);

        if (atMap.containsKey(bean.getId())) {
            viewHolder.status.setText("[有人@我] ");
            viewHolder.status.setTextColor(ContextCompat.getColor(context, R.color.vim_red));
        } else {
            viewHolder.status.setText("");
        }
        String from = "";
        if (ChatMsgApi.isGroup(bean.getId())){
            from = bean.getWhereFrom();
        }
        viewHolder.status.setText(from + ChatMsgUtil.lastMsgBrief(context, bean.getMsgType(), bean.getLastMsg()));
    }

    private void setTop(boolean top) {
        viewHolder.llRoot.setBackgroundResource(top ? R.color.vim_gray : R.color.vim_white);
    }

    private void setUnread(int unReadCount) {
        if (unReadCount != 0) {
            viewHolder.unread.setVisibility(View.VISIBLE);
            viewHolder.unread.setText(unReadCount > 99 ? (99 + "+") : (unReadCount + ""));
        } else {
            viewHolder.unread.setVisibility(View.GONE);
        }
    }

    private void setWhereFrom(Chat bean) {
        if (ChatMsgApi.isSysMsg(bean.getId())) {//验证消息
            viewHolder.name.setText("系统消息");
            UserInfoConfig.loadHead(context, null, viewHolder.imgIcon, R.mipmap.vim_icon_default_system);
        } else if (ChatMsgApi.isGroup(bean.getId())) {//群消息
            UserInfoConfig.loadHead(context, bean.getAvatar(), viewHolder.imgIcon, R.mipmap.vim_icon_default_group);
            viewHolder.name.setText(bean.getName());
            //@提醒
            ArrayList<Long> relatedUsers = bean.getRelatedUsers();
            if (relatedUsers != null && relatedUsers.size() > 0 && bean.getUnReadNum() > 0) {
                if (relatedUsers.contains(bean.getId()) || relatedUsers.contains(RequestHelper.getUserID())) {
                    atMap.put(bean.getId(), true);
                }
            } else if (bean.getUnReadNum() <= 0) {
                atMap.remove(bean.getId());
            }
        } else {
            UserInfoConfig.loadHeadByGender(context, bean.getAvatar(), viewHolder.imgIcon, bean.getGender());
            viewHolder.name.setText(bean.getName());
        }
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    class ConversationViewHolder extends BaseRecyclerViewHolder {

        private TextView name;
        private TextView time;
        private TextView message;
        private TextView status;
        private TextView unread;
        private ImageView imgIcon;
        private LinearLayout llRoot;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            status = (TextView) itemView.findViewById(R.id.tv_status);
            message = (TextView) itemView.findViewById(R.id.tv_message);
            unread = (TextView) itemView.findViewById(R.id.tv_unread);
            imgIcon = (ImageView) itemView.findViewById(R.id.icon);
            llRoot = (LinearLayout) itemView.findViewById(R.id.ll_root);
        }
    }

}
