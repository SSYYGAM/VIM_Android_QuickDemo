package com.vrv.sdk.library.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrv.imsdk.model.Group;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友列表Adapter
 * Created by Yang on 2015/8/28 028.
 */
public class GroupListAdapter extends BaseAdapter {

    private Context context;
    private List<Group> groupList = new ArrayList<Group>();

    public GroupListAdapter(Context context, List<Group> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @Override
    public int getCount() {
        if (groupList == null)
            return 0;
        else
            return groupList.size();
    }

    @Override
    public Object getItem(int position) {
        return groupList.get(position);
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

        Group group = groupList.get(position);
        holder.mTextView.setText(group.getName());
        ImageUtil.loadViewLocalHead(context, group.getAvatar(), holder.mIvHead, R.mipmap.vim_icon_default_group);
        return convertView;
    }

    static class ViewHolder {
        ImageView mIvHead;
        TextView mTextView;

        ViewHolder(View view) {
        }
    }
}