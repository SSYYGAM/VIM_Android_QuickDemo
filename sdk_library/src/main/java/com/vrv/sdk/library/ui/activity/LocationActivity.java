package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.VimConstant;
import com.vrv.sdk.library.bean.LocationBean;
import com.vrv.sdk.library.listener.OnItemClickListener;
import com.vrv.sdk.library.ui.adapter.LocationAdapter;
import com.vrv.sdk.library.ui.view.LocationMapView;
import com.vrv.sdk.library.utils.SDKLocationUtil;
import com.vrv.sdk.library.utils.ToastUtil;
import com.vrv.sdk.library.utils.Utils;
import com.vrv.sdk.library.utils.VrvLog;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends BaseActivity {

    protected static final String TAG = LocationActivity.class.getSimpleName();
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_ADDRESS = "address";

    private LocationMapView locationMapView;
    private RecyclerView locationRecyclerView;
    private SDKLocationUtil sdkLocationUtil;
    private int selectedIndex = 0; //默认被选择的是第一个
    private ArrayList<LocationBean> dataList;
    private LocationAdapter locationAdapter;
    private ProgressBar mProgressBar;
    private RelativeLayout mBottomBlock;
    private MenuItem sendMenu; // 发送地理位置的菜单按钮
    private static boolean isRequestLocation; // true表示发送位置信息前请求位置列表，false表示查看位置信息
    private boolean isFirstRequstList = true; // 是否是第一次请求地址列表
    private static LatLng orgLatLng = null; // 自己的精确位置或者查看的位置信息,用于复位操作，１、在start中初始化；２、在handler中初始化

    public static void start(Activity activity, double latitude, double longitude, String address) { //　点击消息查看时进入
        Intent intent = new Intent();
        intent.setClass(activity, LocationActivity.class);
        intent.putExtra(KEY_LATITUDE, latitude);
        intent.putExtra(KEY_LONGITUDE, longitude);
        intent.putExtra(KEY_ADDRESS, address);
        activity.startActivity(intent);
        isRequestLocation = false;
        orgLatLng = new LatLng(latitude, longitude);
    }

    public static void startForResult(Activity activity, int requestCode) { // 请求时进入
        Intent intent = new Intent();
        intent.setClass(activity, LocationActivity.class);
        activity.startActivityForResult(intent, requestCode);
        isRequestLocation = true;
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle("位置");
    }

    @Override
    protected void loadContentLayout() {
        contentView = LayoutInflater.from(context).inflate(R.layout.vim_activity_location, null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        locationRecyclerView = (RecyclerView) contentView.findViewById(R.id.lv_location_list);
        locationMapView = (LocationMapView) contentView.findViewById(R.id.map_view);
        mBottomBlock = (RelativeLayout) contentView.findViewById(R.id.bottom_block);
        if (isRequestLocation) {//　查看位置信息时全屏，　发送位置信息时分屏
            locationMapView.getmBaiduMap().setOnMapTouchListener(new MapTouchListener()); // 监听地图触摸
            // 重新设置高度占比
            int heightPixels = getResources().getDisplayMetrics().heightPixels;
            ViewGroup.LayoutParams layoutParams = mBottomBlock.getLayoutParams();
            layoutParams.height = (int) (heightPixels * 0.4);
            mBottomBlock.setLayoutParams(layoutParams);
        }
        dataList = new ArrayList<LocationBean>();
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationRecyclerView.addItemDecoration(Utils.buildDividerItemDecoration(context));
        locationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        locationAdapter = new LocationAdapter(dataList, this);
        locationAdapter.setOnItemClickListener(new MyItemClickListener()); //　添加条目点击事件
        locationRecyclerView.setAdapter(locationAdapter);
        mProgressBar = (ProgressBar) contentView.findViewById(R.id.pb_update);
    }

    @Override
    protected void setViews() {
        resetLocationView(true);
    }

    /**
     * 初始化地图与地理列表显示
     *
     * @param isFirst 　true表示第一次使用，false表示复位地图到当前定位点
     */
    private void resetLocationView(boolean isFirst) {
        if (isRequestLocation) {
            //todo:请求位置信息
            startRequestLocationList(); // todo: 不判断会报NullPointerException
        } else {
            double latitude = getIntent().getDoubleExtra(KEY_LATITUDE, -1);
            double longitude = getIntent().getDoubleExtra(KEY_LONGITUDE, -1);
            String address = getIntent().getStringExtra(KEY_ADDRESS);
            if (latitude > 0 && longitude > 0) {
                LocationBean lookLocation = new LocationBean();
                lookLocation.setAddrStr(address);
                lookLocation.setLatitude(latitude);
                lookLocation.setLongitude(longitude);
                locationMapView.showLocation(lookLocation);
            }
        }
        if (!isFirst) { // 第一次进入会在onResume中开始定位
            locationMapView.setMapViewCenterAddress(orgLatLng);
        }

    }

    @Override
    protected void setListener() {
        locationMapView.registerGeoCodeListener(new LocationMapView.GeoCodeListener() {

            @Override
            public void onGetSucceed(ReverseGeoCodeResult reverseGeoCodeResult) {
                // 转为List集合，
                dataList.clear(); // 清空集合
                LatLng location = reverseGeoCodeResult.getLocation();
                String accurateAddress = reverseGeoCodeResult.getAddress();
                LocationBean accurateLocation = new LocationBean(location.latitude, location.longitude, accurateAddress, accurateAddress);
                dataList.add(accurateLocation);// 添加精确位置信息
                List<PoiInfo> poiList = reverseGeoCodeResult.getPoiList();
                if (poiList != null) {
                    for (PoiInfo poiInfo : poiList) {
                        LocationBean locationBean = new LocationBean(poiInfo.location.latitude, poiInfo.location.longitude, poiInfo.address, poiInfo.name);
                        dataList.add(locationBean);
                    }
                }
                requestLocationListOver();
            }

            @Override
            public void onGetFailed() {
                ToastUtil.showShort(context, "重新定位失败");
            }
        });
        // 解决初始化定位时，显示的是默认地址的问题（默认获取MapView中央位置的对应的经纬度为北京）
        locationMapView.getmBaiduMap().setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                if (isRequestLocation && isFirstRequstList) {
                    startRequestLocationList();
                    isFirstRequstList = false;
                }
            }
        });

        locationMapView.getResetLocation().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showShort(context, "重置位置信息");
                resetLocationView(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vim_menu_option, menu);
        sendMenu = menu.findItem(R.id.action_done);
        sendMenu.setVisible(isRequestLocation);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            Intent data = new Intent();
            if (dataList == null || dataList.isEmpty()) {
                data.putExtra("data", VimConstant.initDefaultLocation());
            } else {
                data.putExtra("data", dataList.get(selectedIndex));
            }
            setResult(RESULT_OK, data);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRequestLocation) {
            //自己定位时才发起定位请求
            if (sdkLocationUtil == null) {
                sdkLocationUtil = new SDKLocationUtil(context, mHandler);
            }
            sdkLocationUtil.start();
        }
        if (locationMapView != null) { // 不执行会造成mapview无法滑动
            locationMapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationMapView != null) {
            locationMapView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sdkLocationUtil != null) {
            sdkLocationUtil.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (locationMapView != null) {
            locationMapView.closeLocation();
        }
        if (sdkLocationUtil != null) {
            sdkLocationUtil.stop();
            sdkLocationUtil = null;
        }
    }

    @Override
    public void onClick(View v) {

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDKLocationUtil.LOCATION_RETURN:
                    BDLocation location = sdkLocationUtil.getLocation();
                    if (location == null) {
                        VrvLog.e(TAG, "百度sdk定位结果为NULL");
                        sdkLocationUtil.pause();
                        sdkLocationUtil.start();
                    } else if (location.getLocType() == 161
                            || location.getLocType() == 61
                            || location.getLocType() == 68) { //TODO:需要的话根据不同情况分别处理
                        orgLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        locationMapView.showLocation(new LocationBean(location));
                        //初始进入时显示的是默认的地址？？？？　北京，监听mapview的显示
//                        startRequestLocationList();

                    } else {
                        VrvLog.e(TAG, "百度sdk定位 异常：" + location.getLocType());
                        sdkLocationUtil.pause();
                        sdkLocationUtil.start();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * RecyclerView的条目点击监听
     */
    private class MyItemClickListener implements OnItemClickListener {
        @Override
        public void OnItemClick(int position, View view) {
            dataList.get(selectedIndex).isSeleted = false;
            locationAdapter.notifyItemChanged(selectedIndex);
            selectedIndex = position;
            dataList.get(position).isSeleted = true;
            locationAdapter.notifyItemChanged(position);
            locationMapView.setMapViewCenterAddress(new LatLng(dataList.get(position).getLatitude(), dataList.get(position).getLongitude()));
        }

        @Override
        public boolean OnItemLongClick(int position, View view) {
            return false;
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * MapView触摸监听
     */
    private class MapTouchListener implements BaiduMap.OnMapTouchListener {

        @Override
        public void onTouch(MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                startRequestLocationList();
            }
        }
    }

    /**
     * 　请求位置信息列表时处理界面显示
     */
    private void startRequestLocationList() {
        if (locationMapView == null) {
            return;
        }
        //显示progressBar,隐藏发送位置按钮,重置选择的index
        selectedIndex = 0;
        mProgressBar.setVisibility(View.VISIBLE);
        locationRecyclerView.setClickable(false);
        if (sendMenu != null) {
            sendMenu.setVisible(false);
        }
        locationMapView.getLocationList();
    }

    /**
     * 　请求位置信息列表返回时处理界面显示
     */
    private void requestLocationListOver() {
        if (isFirstRequstList) return;// 屏蔽第一次请求
        locationAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
        locationRecyclerView.setClickable(true);
        sendMenu.setVisible(true);
        locationRecyclerView.scrollToPosition(0); // RecyclerView默认不滑动
    }
}
