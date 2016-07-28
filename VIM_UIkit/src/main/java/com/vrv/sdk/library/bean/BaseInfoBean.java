package com.vrv.sdk.library.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.Contact;
import com.vrv.imsdk.model.Group;
import com.vrv.imsdk.model.ItemModel;

public class BaseInfoBean implements Parcelable {

    private long ID = 0;
    private String name = "";
    private String content = "";
    private String icon = "";
    private byte gender = 0;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "BaseInfoBean{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", icon='" + icon + '\'' +
                ", gender=" + gender +
                '}';
    }

    public static BaseInfoBean chat2BaseInfo(Chat chat) {
        BaseInfoBean baseInfoBean = new BaseInfoBean();
        baseInfoBean.setID(chat.getId());
        baseInfoBean.setName(chat.getName());
        baseInfoBean.setIcon(chat.getAvatar());
        baseInfoBean.setGender(chat.getGender());
        return baseInfoBean;
    }

    public static BaseInfoBean contact2BaseInfo(Contact contact) {
        BaseInfoBean baseInfoBean = new BaseInfoBean();
        baseInfoBean.setID(contact.getId());
        baseInfoBean.setName(contact.getName());
        baseInfoBean.setIcon(contact.getAvatar());
        baseInfoBean.setGender(contact.getGender());
        return baseInfoBean;
    }

    public static BaseInfoBean group2BaseInfo(Group group) {
        BaseInfoBean baseInfoBean = new BaseInfoBean();
        baseInfoBean.setID(group.getId());
        baseInfoBean.setName(group.getName());
        baseInfoBean.setIcon(group.getAvatar());
        return baseInfoBean;
    }
    
    public static BaseInfoBean itemModel2BaseInfo(ItemModel itemModel){
        BaseInfoBean baseInfoBean = new BaseInfoBean();
        baseInfoBean.setID(itemModel.getId());
        baseInfoBean.setName(itemModel.getName());
        baseInfoBean.setIcon(itemModel.getAvatar());
        return baseInfoBean;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.ID);
        dest.writeString(this.name);
        dest.writeString(this.content);
        dest.writeString(this.icon);
        dest.writeByte(this.gender);
    }

    public BaseInfoBean() {
    }

    protected BaseInfoBean(Parcel in) {
        this.ID = in.readLong();
        this.name = in.readString();
        this.content = in.readString();
        this.icon = in.readString();
        this.gender = in.readByte();
    }

    public static final Creator<BaseInfoBean> CREATOR = new Creator<BaseInfoBean>() {
        public BaseInfoBean createFromParcel(Parcel source) {
            return new BaseInfoBean(source);
        }

        public BaseInfoBean[] newArray(int size) {
            return new BaseInfoBean[size];
        }
    };
}
