package com.vrv.sdk.library.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrv.sdk.library.R;
import com.vrv.sdk.library.bean.BaseInfoBean;
import com.vrv.sdk.library.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter extends BaseAdapter {
    private Context context;
    private List<BaseInfoBean> SearchList = new ArrayList<>();

    public SearchListAdapter(Context context, List<BaseInfoBean> SearchList) {
        this.context = context;
        this.SearchList = SearchList;
    }

    @Override
    public int getCount() {
        if (SearchList == null)
            return 0;
        else
            return SearchList.size();
    }

    @Override
    public Object getItem(int position) {
        return SearchList.get(position);
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

        BaseInfoBean bean = SearchList.get(position);
        holder.mTextView.setText(bean.getName());
        ImageUtil.loadViewLocalHead(context, bean.getIcon(), holder.mIvHead, R.mipmap.vim_icon_default_user);
        return convertView;
    }

    static class ViewHolder {
        ImageView mIvHead;
        TextView mTextView;

        ViewHolder(View view) {

        }
    }
}