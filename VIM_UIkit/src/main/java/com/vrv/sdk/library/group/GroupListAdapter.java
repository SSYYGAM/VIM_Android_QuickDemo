package com.vrv.sdk.library.group;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.vrv.imsdk.model.Group;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.common.UserInfoConfig;
import com.vrv.sdk.library.common.adapter.BaseRecyclerAdapter;
import com.vrv.sdk.library.common.adapter.BaseRecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

public class GroupListAdapter extends BaseRecyclerAdapter<GroupListAdapter.GroupViewHolder> implements SectionIndexer {

    private Context context;
    private List<Group> groupList = new ArrayList<>();

    public GroupListAdapter(Context context, List<Group> list) {
        this.context = context;
        this.groupList = list;
    }

    public Group getItemObject(int position) {
        if (position < 0 || position > getItemCount())
            return null;
        return groupList.get(position);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupViewHolder(View.inflate(context, R.layout.vim_item_contact_has_index, null));
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        GroupViewHolder viewHolder = (GroupViewHolder) holder;
        bindOnItemClickListener(viewHolder, position);
        Group bean = groupList.get(position);
        UserInfoConfig.loadHead(context, bean.getAvatar(), viewHolder.imgIcon, R.mipmap.vim_icon_default_group);
        viewHolder.tvName.setText(bean.getName());
        String catalog = bean.getPinyin().toUpperCase();
        handleOtherList(position, viewHolder, catalog);
    }

    private void handleOtherList(int position, GroupViewHolder viewHolder, String catalog) {
        //如果没有名称，赋值为"#"
        if (TextUtils.isEmpty(catalog)) {
            catalog = "#";
        }
        if (!catalog.trim().substring(0, 1).matches("^[A-Za-z]+")){
            catalog = "#";
        }
        char[] charArray = catalog.toCharArray();
        if (position == 0) {
            viewHolder.llCatalog.setVisibility(View.VISIBLE);
            viewHolder.tvCatalog.setText(charArray[0] + "");
            return;
        }
        String lastCatalog = groupList.get(position-1).getPinyin().toUpperCase();
        if (TextUtils.isEmpty(lastCatalog)) {
            lastCatalog = "#";
        }
        if (!lastCatalog.trim().substring(0, 1).matches("^[A-Za-z]+")){
            lastCatalog = "#";
        }
        //如果和上一次好友的拼音首字母相同则不显示新的字母标签
        if (catalog.charAt(0) == lastCatalog.charAt(0)) {
            viewHolder.llCatalog.setVisibility(View.GONE);
        } else {
            viewHolder.llCatalog.setVisibility(View.VISIBLE);
            viewHolder.tvCatalog.setText(charArray[0] + "");

        }
    }


    @Override
    public int getItemCount() {
        return groupList.size();
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }


    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < groupList.size(); i++) {
            String pinyin = groupList.get(i).getPinyin().toUpperCase();
            if (TextUtils.isEmpty(pinyin)) {
                pinyin = "#";
            }
            char firstChar = pinyin.charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    class GroupViewHolder extends BaseRecyclerViewHolder {

        private ImageView imgIcon;
        private TextView tvName;
        private TextView tvCatalog;// 首字母标签布局TextView
        private LinearLayout llCatalog;//首字母标签布局
        public GroupViewHolder(View itemView) {
            super(itemView);
            imgIcon = (ImageView) itemView.findViewById(R.id.img_item_icon);
            tvName = (TextView) itemView.findViewById(R.id.tv_item_nick);
            tvCatalog = (TextView) itemView.findViewById(R.id.tv_item_catalog);
            llCatalog = (LinearLayout) itemView.findViewById(R.id.ll_item_catalog);
        }
    }
}