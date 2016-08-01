package com.vrv.sdk.library.contact;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.vrv.imsdk.SDKClient;
import com.vrv.imsdk.model.ServiceModel;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.VimConstant;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.common.listener.OnItemClickListener;
import com.vrv.sdk.library.ui.activity.ContactDetailActivity;
import com.vrv.sdk.library.ui.activity.GroupListActivity;
import com.vrv.sdk.library.common.view.IndexSideBar;
import com.vrv.sdk.library.utils.ImageUtil;

@SuppressLint("NewApi")
public class ContactFragment extends Fragment {

    private Context context;
    private ContactsView recycler;
    private View rootView;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = this.getActivity();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.vim_fragment_contacts, container, false);
            findView(rootView);
            setViews();
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void findView(View rootView) {
        recycler = (ContactsView) rootView.findViewById(R.id.lv_contact);
        IndexSideBar indexBar = (IndexSideBar) rootView.findViewById(R.id.side_bar);
        indexBar.setListView(recycler);
        RecyclerViewHeader headerView = (RecyclerViewHeader) rootView.findViewById(R.id.header);
        // 群
        headerView.findViewById(R.id.ll_contacts_group).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupListActivity.start((Activity) context);
            }
        });
        ImageView imageView = (ImageView) headerView.findViewById(R.id.icon);
        TextView name = (TextView) headerView.findViewById(R.id.name_tv);
        ImageUtil.loadDefaultHead(context, imageView, R.mipmap.vim_icon_default_group);
        name.setText("群");
        headerView.attachTo(recycler);
    }

    private void setViews() {
        setNotifyListener();
        recycler.getData().addAll(SDKClient.instance().getContactService().getList());
        ((ContactsAdapter) recycler.getAdapter()).notifyUpdate(recycler.getData());
        recycler.setOnItemClick(new OnItemClickListener() {
            @Override
            public void OnItemClick(int position, View view) {
                ContactDetailActivity.start(context, recycler.getData().get(position).getId());
            }

            @Override
            public boolean OnItemLongClick(int position, View view) {
                return false;
            }
        });

        //默认机器人不是好友添加好友
        if (!SDKClient.instance().getContactService().isFriend(VimConstant.DEFAULT_APP)) {
            RequestHelper.addContact(VimConstant.DEFAULT_APP, "hi, echo", "", null);
        }

    }

    private void setNotifyListener() {
        SDKClient.instance().getContactService().setListener(new ServiceModel.OnChangeListener() {
            @Override
            public void notifyDataChange() {
                recycler.getData().clear();
                recycler.getData().addAll(SDKClient.instance().getContactService().getList());
                ((ContactsAdapter) recycler.getAdapter()).notifyUpdate(recycler.getData());
            }

            @Override
            public void notifyItemChange(int i) {
            }
        });
    }

}
