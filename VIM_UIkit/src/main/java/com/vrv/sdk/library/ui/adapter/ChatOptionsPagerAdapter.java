package com.vrv.sdk.library.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vrv.sdk.library.R;
import com.vrv.sdk.library.VimConstant;
import com.vrv.sdk.library.bean.OptionBean;
import com.vrv.sdk.library.common.adapter.BaseRecyclerAdapter;
import com.vrv.sdk.library.common.adapter.BaseRecyclerViewHolder;
import com.vrv.sdk.library.common.listener.OnItemClickListener;
import com.vrv.sdk.library.ui.activity.ChatActivity;
import com.vrv.sdk.library.ui.activity.FileSelectActivity;
import com.vrv.sdk.library.ui.activity.LocationActivity;
import com.vrv.sdk.library.ui.activity.PhotosThumbnailActivity;
import com.vrv.sdk.library.ui.activity.SelectContactActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatOptionsPagerAdapter extends PagerAdapter {

    private final String TAG = ChatOptionsPagerAdapter.class.getSimpleName();

    private Context context;
    private final int COUNT = 8;
    private List<OptionBean> options;
    private List<RecyclerView> viewList = new ArrayList<>();
    private OptionRecyclerAdapter adapter;

    public ChatOptionsPagerAdapter(Context context) {
        this.context = context;
        options = VimConstant.getOptionList(context);
        int size = options.size() - 1;
        int pages = size / COUNT + 1;
        for (int i = 0; i < pages; i++) {
            RecyclerView recyclerView = new RecyclerView(context);
            GridLayoutManager layoutManager = new GridLayoutManager(context, 4);
            recyclerView.setLayoutManager(layoutManager);
            final List<OptionBean> adapterList = options.subList(i * COUNT, Math.min(size, (i + 1) * COUNT));
            adapter = new OptionRecyclerAdapter(context, adapterList);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void OnItemClick(int position, View view) {
                    if (listener != null) {
                        listener.onClick(position, adapterList.get(position).getType());
                    }
                    chatOptionsClick(position, adapterList.get(position).getType());
                }

                @Override
                public boolean OnItemLongClick(int position, View view) {
                    return false;
                }
            });
            viewList.add(recyclerView);
        }
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position), 0);
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    class OptionRecyclerAdapter extends BaseRecyclerAdapter<OptionRecyclerAdapter.OptionViewHolder> {

        private List<OptionBean> options = new ArrayList<>();
        private Context context;

        public OptionRecyclerAdapter(Context context, List<OptionBean> options) {
            this.context = context;
            this.options = options;
        }

        public OptionBean getItem(int position) {
            if (position < 0 || position >= options.size()) {
                return null;
            }
            return options.get(position);
        }

        @Override
        public OptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            OptionViewHolder holder = new OptionViewHolder(View.inflate(context, R.layout.vim_view_grid_item, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
            OptionViewHolder viewHolder = (OptionViewHolder) holder;
            bindOnItemClickListener(viewHolder, position);
            OptionBean option = options.get(position);
            viewHolder.imgIcon.setImageResource(option.getIcon());
            viewHolder.tvName.setText(option.getName());
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        class OptionViewHolder extends BaseRecyclerViewHolder {

            private ImageView imgIcon;
            private TextView tvName;
            private LinearLayout llOption;

            public OptionViewHolder(View itemView) {
                super(itemView);
                imgIcon = (ImageView) itemView.findViewById(R.id.img_gridItem);
                tvName = (TextView) itemView.findViewById(R.id.tv_gridItem);
                llOption = (LinearLayout) itemView.findViewById(R.id.ll_option_item);
            }
        }
    }

    /**
     * 点击执行输入部分操作选项
     *
     * @param option
     */
    private void chatOptionsClick(int position, int option) {
        switch (option) {
            case VimConstant.TYPE_PIC://发送图片
                PhotosThumbnailActivity.startForResult((ChatActivity) context, VimConstant.TYPE_PIC);
                break;
            case VimConstant.TYPE_FILE://发送文件
                FileSelectActivity.startForResult((ChatActivity) context, VimConstant.TYPE_FILE);
                break;
            case VimConstant.TYPE_POSITION://位置
                LocationActivity.startForResult((ChatActivity) context, VimConstant.TYPE_POSITION);
                break;
            case VimConstant.TYPE_CARD://名片
                SelectContactActivity.startForResult((ChatActivity) context, VimConstant.TYPE_CARD);
                break;
            case VimConstant.TYPE_BURN://阅后即焚
                OptionBean burnOut = adapter.getItem(position);
                burnOut.setIcon(R.mipmap.vim_chat_option_burn_cancle);
                burnOut.setType(VimConstant.TYPE_BURN_CANCEL);
                adapter.notifyItemChanged(position);
                break;
            case VimConstant.TYPE_SHARK://抖一抖
                break;
            case VimConstant.TYPE_BURN_CANCEL://退出阅后即焚模式
                OptionBean burn = adapter.getItem(position);
                burn.setIcon(R.mipmap.vim_chat_option_burn);
                burn.setType(VimConstant.TYPE_BURN);
                adapter.notifyItemChanged(position);
                break;
        }
    }


    private OnOptionListener listener;

    public void setClickListener(OnOptionListener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    public interface OnOptionListener {
        void onClick(int position, int type);
    }
}
