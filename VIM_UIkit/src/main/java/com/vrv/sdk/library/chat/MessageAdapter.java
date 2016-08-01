package com.vrv.sdk.library.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.imsdk.model.GroupMember;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.bean.BaseInfoBean;
import com.vrv.sdk.library.chat.view.ChatMessageView;
import com.vrv.sdk.library.common.UserInfoConfig;
import com.vrv.sdk.library.common.adapter.BaseRecyclerAdapter;
import com.vrv.sdk.library.common.adapter.BaseRecyclerViewHolder;
import com.vrv.sdk.library.listener.ItemDataChangeListener;
import com.vrv.sdk.library.listener.OnReSendChatMsgListener;
import com.vrv.sdk.library.ui.activity.ChatActivity;
import com.vrv.sdk.library.ui.activity.ContactDetailActivity;
import com.vrv.sdk.library.utils.ChatMsgUtil;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseRecyclerAdapter<MessageAdapter.MessageViewHolder> {

    private final String TAG = MessageAdapter.class.getSimpleName();
    private Context context;
    private List<ChatMsg> msgList = new ArrayList<>();
    private List<Long> showTimeMap = new ArrayList<>();
    private OnReSendChatMsgListener reSendListener;
    private BaseInfoBean contact;
    private List<GroupMember> members = new ArrayList<>();
    public static boolean isShowCheckBox = false;
    public static ArrayList<ChatMsg> selectMsgs = new ArrayList<>();
    public boolean onBind = false;//adapter 加载时，不允许刷新 zxj

    public void setReSendListener(OnReSendChatMsgListener listener) {
        this.reSendListener = listener;
    }

    // msg from 收到的消息
    private final int MSG_FROM = 1;
    //msg to 我发送出去的消息
    private final int MSG_TO = 0;

    public MessageAdapter(Context context, List<ChatMsg> msgList) {
        this.context = context;
        this.msgList = msgList;
        selectMsgs.clear();
        setIsShowCheckbox(false);
    }

    //设置单人聊天信息，显示聊天头像
    protected void setBaseInfo(BaseInfoBean bean) {
        if (bean != null) {
            contact = bean;
            notifyDataSetChanged();
        }
    }

    // 设置群聊群成员列表，显示头像名称
    protected void setMembers(List<GroupMember> members) {
        if (members != null) {
            this.members.clear();
            this.members.addAll(members);
            notifyDataSetChanged();
        }
    }

    //检索群成员
    private GroupMember indexMemberByID(long userID) {
        if (members == null || members.isEmpty())
            return null;
        for (GroupMember bean : members) {
            if (bean.getId() == userID) {
                return bean;
            }
        }
        return null;
    }

    /**
     * 添加聊天消息
     *
     * @param messageBean
     */
    private void add(ChatMsg messageBean) {
        if (messageBean == null || msgList == null) {
            return;
        }
        msgList.add(messageBean);
        notifyItemInserted(msgList.size());
    }

    private void add(List<ChatMsg> messageBeans) {
        if (messageBeans == null || msgList.isEmpty()) {
            return;
        }
        int start = msgList.size();
        msgList.addAll(messageBeans);
        notifyItemRangeInserted(start, messageBeans.size());
    }

    private void add(int index, List<ChatMsg> messageBeans) {
        if (messageBeans == null || msgList.isEmpty()) {
            return;
        }
        msgList.addAll(index, messageBeans);
        notifyItemRangeInserted(index, messageBeans.size());
    }

    /**
     * 移除聊天消息
     *
     * @param messageBean
     */
    public void remove(ChatMsg messageBean) {
        if (messageBean == null || msgList == null) {
            return;
        }
        int position = msgList.lastIndexOf(messageBean);
        if (position < 0 || position > msgList.size()) {
            return;
        }
        msgList.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(List<ChatMsg> messageBeans) {
        if (messageBeans == null || messageBeans.isEmpty() || msgList == null) {
            return;
        }
        msgList.removeAll(messageBeans);
        notifyDataSetChanged();
    }

    public void removeAll() {
        if (msgList != null) {
            msgList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        //表示跟我的电脑聊天
        if (isMyPC()) {
            return MSG_FROM;
        }
        long senderID = msgList.get(position).getSendID();
        return RequestHelper.isMyself(senderID) ? MSG_TO : MSG_FROM;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (MSG_FROM == viewType) {
            return new MessageViewHolder(View.inflate(context, R.layout.vim_chat_from_item, null));
        } else {
            return new MessageViewHolder(View.inflate(context, R.layout.vim_chat_to_item, null));
        }
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        ChatMsg msgBean = msgList.get(position);
        final MessageViewHolder viewHolder = (MessageViewHolder) holder;
        handleTime(position, viewHolder.tvTime);
        int msgType = msgBean.getMessageType();
        if (msgType == ChatMsgApi.TYPE_WEAK_HINT || msgType == ChatMsgApi.TYPE_REVOKE) {
            //处理提示消息展示
            handHintMsg(msgBean, viewHolder);
        } else {
            viewHolder.rlMsg.setVisibility(View.VISIBLE);
            handleHead(position, viewHolder.icon);
            viewHolder.chatMsgView.setViews(msgBean, false, false);
            viewHolder.chatMsgView.setReSendListener(reSendListener);
            final ChatMsg finalMsgBean = msgBean;
            viewHolder.chatMsgView.setItemDataChangeListener(new ItemDataChangeListener() {
                @Override
                public void ItemDataChange(boolean isShowCheckbox) {
                    isShowCheckBox = isShowCheckbox;
                    notifyDataSetChanged();//长按消息操作时，会调整消息，需要刷新页面
                    if (isShowCheckBox) {//点击更多时显示
                        mulSelectListener.ShowLlLayout();//通知Activity，显示转发，删除的底部布局
                    }
                }

                @Override
                public void onItemOperation(int type, final ChatMsg msg) {
                }
            });
        }
        setCheckBox(msgBean, viewHolder.chMsg);
    }

    private void handleHead(int position, ImageView imageView) {
        if (isMyPC()) {
            UserInfoConfig.loadHead(context, RequestHelper.getMainAccount().getMyInfo().getAvatar(), imageView, R.mipmap.vim_icon_default_user);
            return;
        }
        final long targetID = msgList.get(position).getTargetID();
        final long userID = msgList.get(position).getSendID();
        String path = "";
        byte gender = UserInfoConfig.UNKNOWN;
        if (RequestHelper.isMyself(userID)) {
            path = RequestHelper.getMyInfo().getAvatar();
            gender = RequestHelper.getMyInfo().getGender();
        } else if (ChatMsgApi.isUser(targetID) || ChatMsgApi.isApp(targetID)) {
            path = contact.getIcon();
            gender = contact.getGender();
        } else if (ChatMsgApi.isGroup(targetID)) {
            final GroupMember member = indexMemberByID(userID);
            if (member != null) {
                path = member.getAvatar();
                gender = member.getGender();
                msgList.get(position).setName(member.getMemberName());
                setIconOnLongClickListener(imageView, member.getId(), member.getMemberName());
            }
        }
        UserInfoConfig.loadHeadByGender(context, path, imageView, gender);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactDetailActivity.start(context, userID);
            }
        });
    }

    private boolean isMyPC() {
        //表示跟我的电脑聊天
        return contact != null && RequestHelper.isMyself(contact.getID());
    }

    private void compareShowTime(long msgTime) {
        try {
            if (msgTime < showTimeMap.get(0) && Math.abs(msgTime - showTimeMap.get(0)) > 3 * 60 * 1000) {
                showTimeMap.add(0, msgTime);
            } else if (msgTime > showTimeMap.get(showTimeMap.size() - 1) && Math.abs(msgTime - showTimeMap.get(showTimeMap.size() - 1)) > 3 * 60 * 1000) {
                showTimeMap.add(msgTime);
            }
        } catch (Exception e) {

        }
    }

    private void handleTime(int position, TextView tvTime) {
        long msgTime = msgList.get(position).getSendTime();
        if ((position == 0 && !showTimeMap.contains(msgTime)) || showTimeMap.size() <= 0) {
            showTimeMap.add(0, msgTime);
        } else {
            compareShowTime(msgTime);
        }
        if (!showTimeMap.contains(msgTime)) {
            tvTime.setVisibility(View.GONE);
        } else {
            tvTime.setVisibility(View.VISIBLE);
            tvTime.setText(UserInfoConfig.formatMessageTime(msgTime));
        }
    }

    //群成员头像长按操作
    private void setIconOnLongClickListener(ImageView imageView, final long targetID, final String name) {
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (RequestHelper.isMyself(targetID)) {
                    return false;
                }
                String atName = TextUtils.isEmpty(name) ? String.valueOf(targetID) : name;
                ((ChatActivity) context).insertAT2Input(targetID, "@" + atName);
                return true;
            }
        });
    }

    private void handHintMsg(ChatMsg msgBean, MessageViewHolder holder) {
        String showContent = "";
        int msgType = msgBean.getMessageType();
        if (msgType == ChatMsgApi.TYPE_WEAK_HINT) {
            showContent = ChatMsgUtil.parseMsgProperties(context, msgBean);
            if (TextUtils.isEmpty(showContent)) {
                showContent = TextUtils.isEmpty(msgBean.getMessage()) ? " (弱提示)" : ChatMsgApi.parseTxtJson(msgBean.getMessage());
            }
        } else if (msgType == ChatMsgApi.TYPE_REVOKE) {
            if (RequestHelper.isMyself(msgBean.getSendID())) {
                showContent = context.getResources().getString(R.string.vim_hint_revoke, "您");
            } else {
                showContent = context.getResources().getString(R.string.vim_hint_revoke, ChatMsgApi.parseTxtJson(msgBean.getMessage()));
            }
        }
        if (holder.tvTime.getVisibility() == View.VISIBLE) {
            holder.tvTime.append(showContent);
        } else {
            holder.tvTime.setText(showContent);
        }
        holder.tvTime.setVisibility(View.VISIBLE);
        holder.rlMsg.setVisibility(View.GONE);
    }

    private void setCheckBox(final ChatMsg chatMsg, CheckBox checkBox) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectMsgs.add(chatMsg);
                } else {
                    selectMsgs.remove(chatMsg);
                }
            }
        });
        checkBox.setVisibility(isShowCheckBox ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    class MessageViewHolder extends BaseRecyclerViewHolder {

        public TextView tvTime;
        public RelativeLayout rlMsg;
        //发送消息，显示发消息人的头像，按键事件
        public ImageView icon;
        public ChatMessageView chatMsgView;
        public CheckBox chMsg/*发送消息，显示item勾选的未选状态*/;

        public MessageViewHolder(View view) {
            super(view);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            rlMsg = (RelativeLayout) view.findViewById(R.id.rl_content);
            icon = (ImageView) view.findViewById(R.id.chat_icon);
            chatMsgView = (ChatMessageView) view.findViewById(R.id.view_chat_message);
            chMsg = (CheckBox) view.findViewById(R.id.ch_chat_selectBox);
        }
    }

    public void setIsShowCheckbox(boolean isShowCheckbox) {
        isShowCheckBox = isShowCheckbox;
        selectMsgs.clear();
    }

    protected <VH extends BaseRecyclerViewHolder> void bindOnItemClickListener(MessageViewHolder holder, final int position) {
        if (mulSelectListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isShowCheckBox) {
                        mulSelectListener.SelectedItem(position, v);
                    }
                }
            });
        }
    }

    //长按消息体，操作消息需要更新页面，控制adapter，notifyDataSetChanged；此处暂时只传递值，
    protected MulSelectListener mulSelectListener;

    public interface MulSelectListener {
        void SelectedItem(int position, View view);

        void ShowLlLayout();

    }

    public void setMulSelectListener(MulSelectListener listener) {
        if (listener != null) {
            this.mulSelectListener = listener;
        }
    }
}
