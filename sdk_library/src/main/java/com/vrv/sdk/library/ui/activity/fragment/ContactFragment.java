package com.vrv.sdk.library.ui.activity.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrv.imsdk.SDKClient;
import com.vrv.imsdk.model.Contact;
import com.vrv.imsdk.model.ServiceModel;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.listener.OnItemClickListener;
import com.vrv.sdk.library.ui.activity.ContactDetailActivity;
import com.vrv.sdk.library.ui.activity.GroupListActivity;
import com.vrv.sdk.library.ui.adapter.ContactsAdapter;
import com.vrv.sdk.library.ui.view.IndexSideBar;
import com.vrv.sdk.library.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class ContactFragment extends Fragment {

    private Context context;
    private List<Contact> contacts = new ArrayList<Contact>();
    private ContactsAdapter adapter;
    private RecyclerView listView;
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
        listView = (RecyclerView) rootView.findViewById(R.id.lv_contact);
        IndexSideBar indexBar = (IndexSideBar) rootView.findViewById(R.id.side_bar);
        listView.setLayoutManager(new LinearLayoutManager(context));
        contacts.clear();
        contacts.addAll(SDKClient.instance().getContactService().getList());
        listView.setAdapter(adapter = new ContactsAdapter(context, contacts));
        indexBar.setListView(listView);
        View headView = View.inflate(context, R.layout.vim_item_contact, null);
        ImageView imageView = (ImageView) headView.findViewById(R.id.icon);
        TextView name = (TextView) headView.findViewById(R.id.name_tv);
        ImageUtil.loadDefaultHead(context, imageView, R.mipmap.vim_icon_default_group);
        name.setText("群");
        headView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                GroupListActivity.start((Activity) context);
            }
        });
    }

    private void setViews() {
        setNotifyListener();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(int position, View view) {
                ContactDetailActivity.start(context, contacts.get(position).getId());
            }

            @Override
            public boolean OnItemLongClick(int position, View view) {
                return false;
            }
        });
    }

    private void setNotifyListener() {
        SDKClient.instance().getContactService().setListener(new ServiceModel.OnChangeListener() {
            @Override
            public void notifyDataChange() {
                contacts.clear();
                contacts.addAll(SDKClient.instance().getContactService().getList());
                if (adapter != null) {
                    adapter.notifyUpdate(contacts);
                }
            }

            @Override
            public void notifyItemChange(int i) {
            }
        });
    }

}
