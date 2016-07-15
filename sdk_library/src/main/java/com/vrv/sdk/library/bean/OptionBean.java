package com.vrv.sdk.library.bean;

/**
 * 资源配置说明
 * 1.聊天输入部分点击 +出现的操作选项
 * 2.好友聊天：不会出现私聊按钮
 * 3.群聊天：不会出现抖一抖按钮
 * 4.阅后即焚不支持发送名片
 */
public class OptionBean {

    public OptionBean() {
    }

    public OptionBean(int icon, String name) {
        this.name = name;
        this.icon = icon;
    }

    private int type;
    private int icon;
    private String name;
    private String intent;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    @Override
    public String toString() {
        return "OptionBean{" +
                "type=" + type +
                ", icon=" + icon +
                ", name='" + name + '\'' +
                ", intent='" + intent + '\'' +
                '}';
    }
}
