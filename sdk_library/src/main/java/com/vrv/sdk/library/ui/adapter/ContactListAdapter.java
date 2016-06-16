package com.vrv.sdk.library.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrv.imsdk.model.Contact;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友列表Adapter
 * Created by Yang on 2015/8/28 028.
 */
public class ContactListAdapter extends BaseAdapter {

    private final String TAG = ContactListAdapter.class.getSimpleName();

    private Context context;
    private List<Contact> buddyList = new ArrayList<Contact>();

    public ContactListAdapter(Context context, List<Contact> buddyList) {
        this.context = context;
        this.buddyList = buddyList;
    }

    @Override
    public int getCount() {
        if (buddyList == null)
            return 0;
        else
            return buddyList.size();
    }

    @Override
    public Object getItem(int position) {
        return buddyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.vim_item_contact, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mIvHead = (ImageView) convertView.findViewById(R.id.icon);
        holder.mTextView = (TextView) convertView.findViewById(R.id.name_tv);
        Contact contact = buddyList.get(position);
        holder.mTextView.setText(contact.getName());
        ImageUtil.loadViewLocalHead(context, contact.getAvatar(), holder.mIvHead, R.mipmap.vim_icon_default_user);
        return convertView;
    }

    static class ViewHolder {
        ImageView mIvHead;
        TextView mTextView;

        ViewHolder(View view) {
        }
    }
}