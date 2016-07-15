package com.vrv.sdk.library.ui.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vrv.imsdk.SDKClient;
import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.ServiceModel;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.bean.BaseInfoBean;
import com.vrv.sdk.library.ui.activity.ChatActivity;
import com.vrv.sdk.library.ui.adapter.ConversationListAdapter;
import com.vrv.sdk.library.utils.ToastUtil;
import com.vrv.sdk.library.utils.VrvLog;

import java.util.ArrayList;

public class ConversationFragment extends Fragment {
    private Context context;
    private ListView lvConversation;
    private ConversationListAdapter adapter;
    private ArrayList<Chat> msgList = new ArrayList<Chat>();
    private View rootView;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = this.getActivity();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.vim_fragment_conversation, container, false);
            initView(rootView);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void initView(View rootView) {
        //获取聊天消息列表
        msgList.addAll(SDKClient.instance().getChatService().getList());
        setNotifyListener();
        lvConversation = (ListView) rootView.findViewById(R.id.lv_conversation);
        adapter = new ConversationListAdapter(context, msgList);
        lvConversation.setAdapter(adapter);
        lvConversation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseInfoBean baseInfoBean = BaseInfoBean.chat2BaseInfo(msgList.get(position));
                if (ChatMsgApi.isUser(baseInfoBean.getID()) //个人
                        || ChatMsgApi.isApp(baseInfoBean.getID())//公众号
                        || ChatMsgApi.isGroup(baseInfoBean.getID())) {//群
                    ChatActivity.start(context, baseInfoBean);
                } else {
                    ToastUtil.showShort(context, "请选择群或者个人聊天");
                }
            }
        });
    }

    private void setNotifyListener() {
        SDKClient.instance().getChatService().setListener(new ServiceModel.OnChangeListener() {
            @Override
            public void notifyDataChange() {
                msgList.clear();
                msgList.addAll(SDKClient.instance().getChatService().getList());
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void notifyItemChange(int position) {
                VrvLog.e("--->>>notify Item position:" + position);
                msgList.set(position, SDKClient.instance().getChatService().getList().get(position));
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
