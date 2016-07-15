package com.vrv.sdk.library.utils;

import android.content.Context;
import android.os.Handler;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

/**
 * @description 百度地图sdk定位
 */
public class SDKLocationUtil {

	/**
	 * @deprecated 定位成功
	 */
	/* public */static final int LOCATION_SUCCESS = 0x110;
	/**
	 * @deprecated 定位失败
	 */
	/* public */static final int LOCATION_FAILURE = 0x120;

	/**
	 * 定位结果
	 */
	public static final int LOCATION_RETURN = 0x130;

	/**
	 * 百度基站定位错误返回码
	 */
	// 61 ： GPS定位结果
	// 62 ： 扫描整合定位依据失败。此时定位结果无效。
	// 63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。
	// 65 ： 定位缓存的结果。
	// 66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
	// 67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果
	// 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
	// 161： 表示网络定位结果
	// 162~167： 服务端定位失败
	// 502：KEY参数错误
	// 505：KEY不存在或者非法
	// 601：KEY服务被开发者自己禁用
	// 602: KEY Mcode不匹配,意思就是您的ak配置过程中安全码设置有问题，请确保：
	// sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名
	// 501-700：KEY验证失败

	/***/
	private final String TAG = SDKLocationUtil.class.getSimpleName();

	// private final String KEY = "7acb748d9a955c42805ecb062f54f9f7";

	public LocationClient mLocationClient = null;
	private Handler handler;
	private Context context;
	private String geo;
	private BDLocation bdLocation;

	public SDKLocationUtil(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
		init();
	}

	/**
	 * 百度sdk开始请求坐标定位
	 */
	public void start() {
		mLocationClient.start();

		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.requestLocation();
		}
	}

	/**
	 * 获取坐标信息，格式：经度，纬度
	 * 
	 * @deprecated 用 {@link #getLocation()}代替
	 * @see #getLocation()
	 * @return
	 */
	public String getGeo() {
		VrvLog.i(TAG, "geo:" + geo);
		return geo;
	}

	/**
	 * 获取Location
	 * 
	 * @return
	 */
	public BDLocation getLocation() {
		return bdLocation;
	}

	/**
	 * 初始化
	 */
	private void init() {
		mLocationClient = new LocationClient(context); // 声明LocationClient类
		// mLocationClient.setAccessKey(KEY);
		mLocationClient.registerLocationListener(new MyLocationListener()); // 注册监听函数
		setLocationOption();
	}

	/**
	 * 暂停
	 */
	public void pause() {
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
	}

	/**
	 * 停止请求定位
	 */
	public void stop() {
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();
			mLocationClient = null;
		}
	}

	/**
	 * 设置相关参数
	 */
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(30 * 1000);// 设置发起定位请求的间隔时间为30*1000ms
		option.setNeedDeviceDirect(false);
		mLocationClient.setLocOption(option);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			VrvLog.i(TAG, "--->BaiduSDK onReceiveLocation()");

			if (location == null) {
				geo = "0,0";
			} else {
				StringBuffer sb = new StringBuffer(256);
				sb.append("time : ");
				sb.append(location.getTime());
				sb.append("\nerror code : ");
				sb.append(location.getLocType());
				sb.append("\nlontitude,latitude : ");
				sb.append(location.getLongitude());
				sb.append(",");
				sb.append(location.getLatitude());
				sb.append("\nradius : ");
				sb.append(location.getRadius());
				if (location.getLocType() == BDLocation.TypeGpsLocation) {
					sb.append("\nspeed : ");
					sb.append(location.getSpeed());
					sb.append("\nsatellite : ");
					sb.append(location.getSatelliteNumber());
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
					sb.append("\naddr : ");
					sb.append(location.getAddrStr());
				}
				geo = location.getLongitude() + "," + location.getLatitude();
				VrvLog.d(TAG, "location:" + sb.toString());
				if (location.getLocType() == 161) {
					handler.sendEmptyMessage(LOCATION_SUCCESS);
				} else {
					handler.sendEmptyMessage(LOCATION_FAILURE);
				}
			}

			bdLocation = location;
			handler.sendEmptyMessage(LOCATION_RETURN);
		}
	}
}
