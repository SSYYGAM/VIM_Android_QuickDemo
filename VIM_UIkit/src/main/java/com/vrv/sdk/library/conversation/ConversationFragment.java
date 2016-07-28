package com.vrv.sdk.library.conversation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vrv.imsdk.SDKClient;
import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.model.ServiceModel;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.bean.BaseInfoBean;
import com.vrv.sdk.library.common.listener.OnItemClickListener;
import com.vrv.sdk.library.ui.activity.ChatActivity;
import com.vrv.sdk.library.utils.ToastUtil;

public class ConversationFragment extends Fragment {

    private Context context;
    private ConversationListView recyclerView;
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
        setNotifyListener();
        recyclerView = (ConversationListView) rootView.findViewById(R.id.recycler);
        recyclerView.getData().addAll(SDKClient.instance().getChatService().getList());
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.setOnItemClick(new OnItemClickListener() {
            @Override
            public void OnItemClick(int position, View view) {
                BaseInfoBean baseInfoBean = BaseInfoBean.chat2BaseInfo(recyclerView.getData().get(position));
                if (ChatMsgApi.isUser(baseInfoBean.getID()) //个人
                        || ChatMsgApi.isApp(baseInfoBean.getID())//公众号
                        || ChatMsgApi.isGroup(baseInfoBean.getID())) {//群
                    ChatActivity.start(context, baseInfoBean);
                } else {
                    ToastUtil.showShort(context, "请选择群或者个人聊天");
                }
            }

            @Override
            public boolean OnItemLongClick(int position, View view) {
                return false;
            }
        });
    }

    private void setNotifyListener() {
        SDKClient.instance().getChatService().setListener(new ServiceModel.OnChangeListener() {
            @Override
            public void notifyDataChange() {
                recyclerView.getData().clear();
                recyclerView.getData().addAll(SDKClient.instance().getChatService().getList());
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void notifyItemChange(int position) {
                recyclerView.getData().set(position, SDKClient.instance().getChatService().getList().get(position));
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

}
