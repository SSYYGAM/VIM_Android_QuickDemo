package com.vrv.sdk.library.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrv.sdk.library.R;
import com.vrv.sdk.library.bean.LocationBean;
import com.vrv.sdk.library.ui.activity.LocationActivity;

import java.util.List;

public class LocationAdapter extends BaseRecyclerAdapter<LocationAdapter.LocationHolder> {

    private List<LocationBean> dataList;
    private static LocationActivity mActivity;

    public LocationAdapter(List<LocationBean> list, LocationActivity activity) {
        dataList = list;
        mActivity = activity;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vim_location_item, null);
        LocationHolder listHodler = new LocationHolder(view);
        return listHodler;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        LocationHolder viewHolder = (LocationHolder) holder;
        bindOnItemClickListener(viewHolder, position);
        LocationBean locationBean = dataList.get(position);
        viewHolder.mName.setText(locationBean.getName());
        viewHolder.mAddress.setText(locationBean.getAddrStr());

        if (position == 0) {
            viewHolder.mAddress.setVisibility(View.GONE); // 第一个条目为精确的地址信息，默认不显示详细地址
        } else {
            viewHolder.mAddress.setVisibility(View.VISIBLE);
        }
        if (mActivity.getSelectedIndex() == position) {
            dataList.get(position).isSeleted = true;
            viewHolder.selectedIV.setVisibility(View.VISIBLE);
        } else {
            dataList.get(position).isSeleted = false;
            viewHolder.selectedIV.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class LocationHolder extends BaseRecyclerViewHolder {
        public TextView mName;
        public TextView mAddress;
        public ImageView selectedIV;

        public LocationHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.address_name);
            mAddress = (TextView) itemView.findViewById(R.id.detail_address);
            selectedIV = (ImageView) itemView.findViewById(R.id.cb_select_location);
        }
    }
}
