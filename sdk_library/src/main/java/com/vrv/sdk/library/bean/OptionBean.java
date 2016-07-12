package com.vrv.sdk.library.bean;

/**
 * Created by zxj on 2016/4/7.
 */
public class OptionBean {
    public static final int TYPE_PIC = 0X01; // 图片
    public static final int TYPE_FILE = 0X02;//文件
    public static final int TYPE_POSITION = 0X03;// 位置
    public static final int TYPE_CARD = 0X04;//名片
    public static final int TYPE_DELAY = 0X05;
    public static final int TYPE_REMIND = 0X06;
    public static final int TYPE_BURN = 0X07;
    public static final int TYPE_SHARK = 0X08;
    public static final int TYPE_PRIVATE = 0X09;
    public static final int TYPE_THIRD = 0X0A;
    public static final int TYPE_BURN_OUT = 0X0B;
    public static final int TYPE_CARD_DISABLE = 0x0C;
    public static final int TYPE_TAKE_PIC = 0x0D;//拍照

    //消息操作
    public static final int TYPE_OPTION_MSG_FORWARD = 0x30;// 转发
    public static final int TYPE_OPTION_MSG_COLLECTION = 0x31;// 收藏
    public static final int TYPE_OPTION_MSG_DELETE = 0x32;// 删除
    public static final int TYPE_OPTION_MSG_WITHDRAW = 0x33;// 撤回

}
