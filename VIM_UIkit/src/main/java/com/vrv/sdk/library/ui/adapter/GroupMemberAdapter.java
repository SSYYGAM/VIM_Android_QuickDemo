package com.vrv.sdk.library.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.vrv.imsdk.SDKClient;
import com.vrv.imsdk.model.GroupMember;
import com.vrv.imsdk.model.ServiceModel;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.common.adapter.BaseRecyclerAdapter;
import com.vrv.sdk.library.common.adapter.BaseRecyclerViewHolder;
import com.vrv.sdk.library.utils.ImageUtil;
import com.vrv.sdk.library.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 群成员列表
 */
public class GroupMemberAdapter extends BaseRecyclerAdapter<GroupMemberAdapter.GroupMemberViewHolder> implements SectionIndexer {

    private Context context;
    private GroupMember creator;
    private ArrayList<GroupMember> managerMemberList = new ArrayList<>();
    private ArrayList<GroupMember> memberList = new ArrayList<>();

    public GroupMemberAdapter(Context context) {
        this.context = context;
        update();

        SDKClient.instance().getMemberService().setListener(new ServiceModel.OnChangeListener() {
            @Override
            public void notifyDataChange() {
                update();
            }

            @Override
            public void notifyItemChange(int position) {

            }
        });
    }

    public GroupMember getItemObject(int position) {
        if (creator != null) {
            if (position == 0) {
                return creator;
            } else if (position > 0 && position <= managerMemberList.size()) {
                return managerMemberList.get(position - 1);
            } else {
                return memberList.get(position - 1 - managerMemberList.size());
            }
        } else {
            if (position < managerMemberList.size()) {
                return managerMemberList.get(position);
            } else {
                return memberList.get(position - managerMemberList.size());
            }
        }
    }

    /**
     * 更新
     */
    public void update() {
        creator = SDKClient.instance().getMemberService().getCreator();
        memberList = SDKClient.instance().getMemberService().getMemberList();
        managerMemberList = SDKClient.instance().getMemberService().getManagerList();
//        initData();
        notifyDataSetChanged();
    }

    public long[] getAllMemberIDs() {
        int size = SDKClient.instance().getMemberService().getList().size();
        long[] IDList = new long[size];
        int i = 0;
        for (GroupMember member : SDKClient.instance().getMemberService().getList()) {
            IDList[i] = member.getId();
            i++;
        }
        return IDList;
    }

    private void initData() {
        ArrayList<GroupMember> list = SDKClient.instance().getMemberService().getList();
        Collections.sort(list);
        this.managerMemberList.clear();
        this.memberList.clear();
        for (GroupMember member : list) {
            if (member.isAdmin()) {
                creator = member;
            } else if (member.isManager()) {
                managerMemberList.add(member);
            } else {
                this.memberList.add(member);
            }
        }
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupMemberViewHolder(View.inflate(context, R.layout.vim_view_group_member_item, null));
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        GroupMemberViewHolder viewHolder = (GroupMemberViewHolder) holder;
        bindOnItemClickListener(viewHolder, position);
        GroupMember member;
        if (creator == null) {
            if (position < managerMemberList.size()) {
                member = managerMemberList.get(position);
                handleManagerView(position, viewHolder);
            } else {
                member = memberList.get(position - managerMemberList.size());
                setIndexView(position, viewHolder, member.getPinyin());
            }
        } else {
            if (position == 0) {
                member = creator;
                handleAdminView(viewHolder);
            } else if (position <= managerMemberList.size()) {
                member = managerMemberList.get(position - 1);
                handleManagerView(position, viewHolder);
            } else {
                member = memberList.get(position - 1 - managerMemberList.size());
                setIndexView(position - 1 - managerMemberList.size(), viewHolder, member.getPinyin());
            }
        }
        viewHolder.tvNick.setText(member.getName());
        ImageUtil.loadViewLocalHead(context, member.getAvatar(), viewHolder.imgIcon, R.mipmap.vim_icon_default_user);
    }

    private void handleAdminView(GroupMemberViewHolder viewHolder) {
        viewHolder.llCatalog.setVisibility(View.VISIBLE);
        viewHolder.tvCatalog.setText("群主");
        viewHolder.tvNick.setCompoundDrawablePadding(8);
        Drawable drawable = Utils.getDrawable(context, R.mipmap.vim_icon_group_admin);
        viewHolder.tvNick.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    private void handleManagerView(int position, GroupMemberViewHolder viewHolder) {
        if (creator != null) {
            if (position == 1) {
                viewHolder.llCatalog.setVisibility(View.VISIBLE);
                viewHolder.tvCatalog.setText("管理员");
            } else {
                viewHolder.llCatalog.setVisibility(View.GONE);
            }
        } else {
            if (position == 0) {
                viewHolder.llCatalog.setVisibility(View.VISIBLE);
                viewHolder.tvCatalog.setText("群主");
            } else {
                viewHolder.llCatalog.setVisibility(View.GONE);
            }
        }
        viewHolder.tvNick.setCompoundDrawablePadding(8);
        Drawable drawable = Utils.getDrawable(context, R.mipmap.vim_icon_group_manager);
        viewHolder.tvNick.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    private void setIndexView(int position, GroupMemberViewHolder viewHolder, String catalog) {
        //如果没有名称，赋值为"#"
        if (TextUtils.isEmpty(catalog)) {
            catalog = "#";
        }
        // Add xa zxj 2015/12/23 判断 catalog 首字母是否是字母
        if (!catalog.trim().substring(0, 1).matches("^[A-Za-z]+")) {
            catalog = "#";
        }
        char[] charArray = catalog.toCharArray();
        if (position == 0) {
            viewHolder.llCatalog.setVisibility(View.VISIBLE);
            viewHolder.tvCatalog.setText(charArray[0] + "");
            return;
        }
        //是否需要添加判断
        /*charArray[0] >= 'A' && charArray[0] <= 'Z'*/
        String lastCatalog = memberList.get(position - 1).getPinyin();
        if (TextUtils.isEmpty(lastCatalog)) {
            lastCatalog = "#";
        }
        // Add xa zxj 2015/12/23 判断 lastCatalog 首字母是否是字母
        if (!lastCatalog.trim().substring(0, 1).matches("^[A-Za-z]+")) {
            lastCatalog = "#";
        }
        //如果和上一次好友的拼音首字母相同则不显示新的字母标签
        if (catalog.charAt(0) == lastCatalog.charAt(0)) {
            viewHolder.llCatalog.setVisibility(View.GONE);
        } else {
            viewHolder.llCatalog.setVisibility(View.VISIBLE);
            viewHolder.tvCatalog.setText(charArray[0] + "");
        }
        viewHolder.tvNick.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    @Override
    public int getItemCount() {
        int size = memberList.size();
        if (creator != null) {
            size += 1;
        }
        if (managerMemberList != null) {
            size += managerMemberList.size();
        }
        return size;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < memberList.size(); i++) {
            String pinyin = memberList.get(i).getPinyin();
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



    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    class GroupMemberViewHolder extends BaseRecyclerViewHolder {

        private CheckBox checkBox;//选择
        private TextView tvCatalog;// 通讯录首字母标签布局TextView
        private TextView tvNick; // 昵称
        private ImageView imgIcon;// 头像
        private TextView tvSign;// 个性签名
        private LinearLayout llCatalog;//通讯录首字母标签布局

        public GroupMemberViewHolder(View itemView) {
            super(itemView);
            tvCatalog = (TextView) itemView.findViewById(R.id.tv_item_catalog);
            llCatalog = (LinearLayout) itemView.findViewById(R.id.ll_item_catalog);
            tvNick = (TextView) itemView.findViewById(R.id.tv_item_nick);
            imgIcon = (ImageView) itemView.findViewById(R.id.img_item_icon);
            tvSign = (TextView) itemView.findViewById(R.id.tv_item_sign);
            checkBox = (CheckBox) itemView.findViewById(R.id.ch_item_select);
            tvSign.setVisibility(View.GONE);
            tvNick.setGravity(Gravity.CENTER_VERTICAL);
        }
    }
}
