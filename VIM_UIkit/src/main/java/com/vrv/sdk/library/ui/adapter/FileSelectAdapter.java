package com.vrv.sdk.library.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrv.sdk.library.R;
import com.vrv.sdk.library.bean.FileBean;
import com.vrv.sdk.library.common.adapter.BaseRecyclerAdapter;
import com.vrv.sdk.library.common.adapter.BaseRecyclerViewHolder;

import java.util.ArrayList;

/**
 * 文件选择适配
 */
public class FileSelectAdapter extends BaseRecyclerAdapter<FileSelectAdapter.FileSelectViewHolder> {

    private final String TAG = FileSelectAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<FileBean> list = new ArrayList<>();

    public FileSelectAdapter(Context context, ArrayList<FileBean> list) {
        this.context = context;
        this.list = list;
    }

    public FileBean getItemObject(int position) {
        if (position >= 0 && position < list.size()) {
            return list.get(position);
        } else {
            return null;
        }
    }

    @Override
    public FileSelectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileSelectViewHolder(View.inflate(context, R.layout.vim_view_file_select_item, null));
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        FileSelectViewHolder viewHolder = (FileSelectViewHolder) holder;
        bindOnItemClickListener(viewHolder, position);
        FileBean fileData = list.get(position);
        if (fileData.getType() == FileBean.FileType.DIR) {
            viewHolder.img.setImageResource(R.mipmap.vim_icon_dir);
        } else if (fileData.getType() == FileBean.FileType.FILE) {
            FileBean.setFileIcon(viewHolder.img, fileData.getName(), fileData.getPath());
        } else if (fileData.getType() == FileBean.FileType.PARENT) {
            viewHolder.img.setImageResource(R.mipmap.vim_icon_dir);
        } else {
            viewHolder.img.setImageResource(R.mipmap.vim_icon_dir);
            viewHolder.itemView.setClickable(false);
        }
        viewHolder.tvName.setText(fileData.getName());
        viewHolder.tvTime.setText(fileData.getTime());
        viewHolder.tvSize.setText(fileData.getSize());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class FileSelectViewHolder extends BaseRecyclerViewHolder {

        private ImageView img;
        private TextView tvName;
        private TextView tvTime;
        private TextView tvSize;

        public FileSelectViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img_file);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvSize = (TextView) itemView.findViewById(R.id.tv_size);
        }
    }
}
