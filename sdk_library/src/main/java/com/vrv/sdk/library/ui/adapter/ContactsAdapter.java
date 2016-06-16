package com.vrv.sdk.library.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.vrv.imsdk.model.Contact;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactsAdapter extends BaseRecyclerAdapter<ContactsAdapter.ContactsViewHolder> implements SectionIndexer {

    private final String TAG = ContactsAdapter.class.getSimpleName();

    private final int STATUS_SELECTED = 1;
    private final int STATUS_NORMAL = 2;
    private final int STATUS_CHECKED = 3;

    private Context context;
    //是否显示checkbox
    private boolean selected;
    // 操作后的选择列表
    private ArrayList<Long> choiceList = new ArrayList<>();
    // map保存checkbox，方便处理选中效果
    private HashMap<Long, Integer> choiceMap = new HashMap<>();
    private ArrayList<Contact> otherList = new ArrayList<>();//其他好友列表
    private ArrayList<Contact> starList = new ArrayList<>();//星标好友列表

    public ContactsAdapter(Context context, List<Contact> buddyList) {
        this.context = context;
        initDate(buddyList);
    }

    /**
     * 显示checkbox，
     *
     * @param context
     * @param contacts
     * @param selectedList NOT null
     */
    public ContactsAdapter(Context context, List<Contact> contacts, long[] selectedList, boolean selected) {
        this.context = context;
        //初始化 选中列表map
        if (selectedList != null) {
            for (long userID : selectedList) {
                choiceMap.put(userID, STATUS_SELECTED);
            }
        }
        initDate(contacts);
        this.selected = selected;
    }

    private void initDate(List<Contact> contacts) {
        starList.clear();
        otherList.clear();
        for (Contact bean : contacts) {
            if (bean.isStar()) {
                starList.add(bean);
            } else {
                otherList.add(bean);
            }
        }
    }


    /**
     * 好友列表更新
     */
    public void notifyUpdate(List<Contact> contacts) {
        initDate(contacts);
        notifyDataSetChanged();
    }



    //userI查找列表中bean
    private Contact getUpdateItem(long userID, List<Contact> list) {
        for (Contact bean : list) {
            if (userID == bean.getId()) {
                return bean;
            }
        }
        return null;
    }

    /**
     * 获取选项对象
     *
     * @param position
     * @return
     */
    public Contact getItemObject(int position) {
        if (position < 0 || position > getItemCount())
            return null;
        return position >= starList.size() ? otherList.get(position - starList.size()) : starList.get(position);
    }

    /**
     * 更新选中状态
     *
     * @param position
     */
    public void updateSelectStatus(int position) {
        long userID = getItemObject(position).getId();
        if (!choiceMap.containsKey(userID)) {
            return;
        }
        if (choiceMap.get(userID) == STATUS_NORMAL) {//添加
            choiceList.add(userID);
            choiceMap.put(userID, STATUS_CHECKED);
        } else if (choiceMap.get(userID) == STATUS_CHECKED) {//移除
            choiceList.remove(userID);
            choiceMap.put(userID, STATUS_NORMAL);
        } else {//不可选
            return;
        }
        notifyItemChanged(position);
    }

    public ArrayList<Long> getChoiceList() {
        return choiceList;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactsViewHolder(View.inflate(context, R.layout.vim_item_contact_has_index, null));
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        ContactsViewHolder viewHolder = (ContactsViewHolder) holder;
        bindOnItemClickListener(viewHolder, position);
        Contact entity = null;
        //先显示V标好友
        if (position < starList.size()) {
            entity = starList.get(position);
            handleStarList(position, viewHolder);
        } else {//显示普通好友
            entity = otherList.get(position - starList.size());
            String catalog = entity.getPinyin();//好友名称
            handleOtherList(position, viewHolder, catalog);
        }
        // Add xa zxj 2015/12/22 分割线的控制
        if (viewHolder.llCatalog.getVisibility()== View.VISIBLE){
            viewHolder.tv_line.setVisibility(View.GONE);
        }else{
            viewHolder.tv_line.setVisibility(View.VISIBLE);
        }
        viewHolder.tvNick.setText(TextUtils.isEmpty(entity.getRemark()) ? entity.getName() : entity.getRemark());

        Utils.loadHead(context, entity.getAvatar(), viewHolder.imgIcon, R.mipmap.vim_icon_default_user);

        if (selected) {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            if (!choiceMap.containsKey(entity.getId())) {
                choiceMap.put(entity.getId(), STATUS_NORMAL);
            }
            if (choiceMap.get(entity.getId()) == STATUS_SELECTED) {
                viewHolder.checkBox.setChecked(false);
                viewHolder.checkBox.setSelected(true);
            } else if (choiceMap.get(entity.getId()) == STATUS_CHECKED) {
                viewHolder.checkBox.setSelected(false);
                viewHolder.checkBox.setChecked(true);
            } else {
                viewHolder.checkBox.setSelected(false);
                viewHolder.checkBox.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return starList.size() + otherList.size();
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < otherList.size(); i++) {
            String pinyin = otherList.get(i).getPinyin();
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

    //处理 V标好友
    private void handleStarList(int position, ContactsViewHolder viewHolder) {
        if (position == 0) {
            viewHolder.llCatalog.setVisibility(View.VISIBLE);
            viewHolder.tvCatalog.setText("V标好友");
        } else {
            viewHolder.llCatalog.setVisibility(View.GONE);
        }
    }

    private void handleOtherList(int position, ContactsViewHolder viewHolder, String catalog) {
        //如果没有名称，赋值为"#"
        if (TextUtils.isEmpty(catalog)) {
            catalog = "#";
        }
        // Add xa zxj 2015/12/23 判断 catalog 首字母是否是字母
        if (!catalog.trim().substring(0, 1).matches("^[A-Za-z]+")){
            catalog = "#";
        }
        char[] charArray = catalog.toCharArray();
        if (position == starList.size()) {
            viewHolder.llCatalog.setVisibility(View.VISIBLE);
            viewHolder.tvCatalog.setText(charArray[0] + "");
            return;
        }
        String lastCatalog = otherList.get(position - starList.size() - 1).getPinyin();
        if (TextUtils.isEmpty(lastCatalog)) {
            lastCatalog = "#";
        }
        // Add xa zxj 2015/12/23 判断 lastCatalog 首字母是否是字母
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

    class ContactsViewHolder extends BaseRecyclerViewHolder {
        private CheckBox checkBox;//选择
        private TextView tvCatalog;// 通讯录首字母标签布局TextView
        private LinearLayout llCatalog;//通讯录首字母标签布局
        private TextView tvNick; // 昵称
        private ImageView imgIcon;// 头像
        private TextView tvSign;// 个性签名
        private View tv_line;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            tvCatalog = (TextView) itemView.findViewById(R.id.tv_item_catalog);
            llCatalog = (LinearLayout) itemView.findViewById(R.id.ll_item_catalog);
            tvNick = (TextView) itemView.findViewById(R.id.tv_item_nick);
            imgIcon = (ImageView) itemView.findViewById(R.id.img_item_icon);
            tvSign = (TextView) itemView.findViewById(R.id.tv_item_sign);
            checkBox = (CheckBox) itemView.findViewById(R.id.ch_item_select);
            tv_line= itemView.findViewById(R.id.tv_item_line);
        }
    }
}