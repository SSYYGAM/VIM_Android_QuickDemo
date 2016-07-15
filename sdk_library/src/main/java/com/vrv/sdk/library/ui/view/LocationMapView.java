package com.vrv.sdk.library.ui.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.bean.LocationBean;

/**
 * @description 地图定位显示
 * 组合控件，提供访问内部控件的功能，由使用者在外部设置一系列的监听事件（例如：BaiduMap的触摸监听、BaiduMap的状态改变的监听）
 * <p/>
 * 请求地址位置分为两种：
 * １、地图的定位，即地图中心点的位置（精确位置）
 * ２、请求周边地理位置列表的定位（同过反地址编码提供选择的地址列表）
 * 或者依赖于前者，当当前位置确定或者地图中央位置改变后，开始请求新地址列表
 * <p/>
 * 提供了获取MapView中央位置对应的经纬度获取地理列表的功能及其监听；
 * 提供重新设置地图中央位置地理位置的功能
 * <p/>
 * －－－重置按钮的功能以后以后改为放在本控件中－－－
 */
public class LocationMapView extends RelativeLayout {

    private final String TAG = LocationMapView.class.getSimpleName();

    private Context context;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private boolean isFirstLoc = true;
    private ImageView fixPointer; // mapView中央固定的图片
    private ImageView resetLocation; // 重置按钮

    public LocationMapView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public LocationMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public LocationMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.vim_view_location, this);
        findViews();
    }

    private void findViews() {
        mMapView = (MapView) findViewById(R.id.mapView);
        //重置中央标杆的位置为底部在中央位置 1、代码中设置paddingBottom　２、代码设置
        fixPointer = (ImageView) findViewById(R.id.fix_pointer);
        resetLocation = (ImageView) findViewById(R.id.iv_reset);
        setMap();
    }

    private void setMap() {
        if (mBaiduMap == null) {
            mBaiduMap = mMapView.getMap();
        }
        // 普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 缩放等级
        // mBaiduMap.setMaxAndMinZoomLevel(19, 10);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17));
//		mBaiduMap.setOnMapTouchListener(new BaiduMapTouchListener()); //触摸监听转移到使用者
    }

    /**
     * 地图展示当前位置
     *
     * @param location
     */
    public void showLocation(LocationBean location) {

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(0)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).longitude(location.getLongitude())
                .latitude(location.getLatitude()).build();

        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);

        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            // MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 17);
            mBaiduMap.animateMapStatus(u);
        }
        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        // BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
        // .fromResource(R.drawable.ic_launcher);
        // LocationMode mCurrentMode = LocationMode.NORMAL;
        // MyLocationConfigeration config = new MyLocationConfigeration(
        // mCurrentMode, true, mCurrentMarker);
        // mBaiduMap.setMyLocationConfigeration(config);
    }

    /**
     * 不使用之后，关闭图层、销毁view
     */
    public void closeLocation() {
        // 当不需要定位图层时关闭定位图层
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationEnabled(false);
        }
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
    }

    public void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    // 获取mapView中央位置对应经纬度的地址列表
    public void getLocationList() {
        // １＼获取mapView的物理位置
        //２＼将屏幕坐标转换为地理坐标
//		Point centerPoint = new Point((int)( fixPointer.getX() + fixPointer.getWidth() * 0.5), (int) (fixPointer.getY() + fixPointer.getHeight() * 0.5));
        Point targetScreen = mBaiduMap.getMapStatus().targetScreen;
        if (mBaiduMap == null) {
            mBaiduMap = mMapView.getMap();
        }
        if (mBaiduMap != null) {//3、反地理编码
            Projection projection = mBaiduMap.getProjection();
            if (projection == null) return;
            com.baidu.mapapi.model.LatLng centetLatLng = projection.fromScreenLocation(targetScreen);
            getPoisByGeoCode(centetLatLng.latitude, centetLatLng.longitude);
        }
    }

    private GeoCoder mGeoCoder;
    public static GeoCodeListener mGeoCodeListener = null;

    /**
     * 反地理编码监听器
     */
    public interface GeoCodeListener {
        void onGetSucceed(ReverseGeoCodeResult reverseGeoCodeResult);

        void onGetFailed();
    }

    public void registerGeoCodeListener(GeoCodeListener listener) {
        mGeoCodeListener = listener;
    }


    /**
     * 根据经纬度获取周边热点列表
     *
     * @param lat
     * @param lon
     * @return void
     * @Title: getPoiByGeoCode
     */
    public void getPoisByGeoCode(double lat, double lon) {

        if (mGeoCoder == null) {
            mGeoCoder = GeoCoder.newInstance();
        }
        mGeoCoder.setOnGetGeoCodeResultListener(new MyGetGeoCoderResultListener());
        // 反Geo搜索
        mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption()
                .location(new LatLng(lat, lon)));
    }

    private class MyGetGeoCoderResultListener implements OnGetGeoCoderResultListener {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            String province = reverseGeoCodeResult.getAddressDetail().province;
//			reverseGeoCodeResult.getAddressDetail().city);
//			reverseGeoCodeResult.getAddressDetail().district);
//			reverseGeoCodeResult.getAddressDetail().street);
//			reverseGeoCodeResult.getAddressDetail().street);
//			reverseGeoCodeResult.getAddressDetail().streetNumber);
//			reverseGeoCodeResult.getLocation().latitude);
//			reverseGeoCodeResult.getLocation().longitude);
            // 查看位置信息或者长时间不间断滑动地图reverseGeoCodeResult会报ＮullPointerException
            if (reverseGeoCodeResult == null) return;
            destroyGeoCode();
            if (mGeoCodeListener != null) {
                mGeoCodeListener.onGetSucceed(reverseGeoCodeResult);
            }
        }
    }

    public void destroyGeoCode() {
        if (mGeoCoder != null) {
            mGeoCoder.destroy();
            mGeoCoder = null;
        }
//		mGeoCodeListener = null;
//		mGeoCodePoiListener = null;
    }

    public BaiduMap getmBaiduMap() {
        if (mBaiduMap == null) {
            this.mBaiduMap = mMapView.getMap();
        }
        return this.mBaiduMap;
    }

    public ImageView getResetLocation() {
        return resetLocation;
    }

    /**
     * 设置地图的中央为指定的经纬度
     */
    public void setMapViewCenterAddress(LatLng latLng) {
        if (latLng == null) return;
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(latLng)
                .zoom(17).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        this.getmBaiduMap().setMapStatus(mMapStatusUpdate);
    }
}
