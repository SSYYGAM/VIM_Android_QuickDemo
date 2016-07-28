package com.vrv.sdk.library.bean;

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
