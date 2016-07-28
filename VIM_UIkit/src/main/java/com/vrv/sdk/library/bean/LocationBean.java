package com.vrv.sdk.library.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.location.BDLocation;

public class LocationBean implements Parcelable {

    private double latitude;
    private double longitude;
    private String address;
    private String name;

    public LocationBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationBean(double latitude, double longitude, String address, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.name = name;
    }

    public LocationBean(BDLocation bdLocation) {
        this.latitude = bdLocation.getLatitude();
        this.longitude = bdLocation.getLongitude();
        this.address = bdLocation.getAddrStr();
        this.name = "";// todo:
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.address);
        dest.writeString(this.name);
    }

    protected LocationBean(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.address = in.readString();
        this.name = in.readString();
    }

    public static final Creator<LocationBean> CREATOR = new Creator<LocationBean>() {
        @Override
        public LocationBean createFromParcel(Parcel source) {
            return new LocationBean(source);
        }

        @Override
        public LocationBean[] newArray(int size) {
            return new LocationBean[size];
        }
    };

    @Override
    public String toString() {
        return "LocationBean{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
