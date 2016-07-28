package com.vrv.sdk.library;

import android.content.Context;

import com.vrv.sdk.library.bean.LocationBean;
import com.vrv.sdk.library.bean.OptionBean;

import java.util.ArrayList;
import java.util.List;

public class VimConstant {

    //默认登录账号
    public static final String ACCOUNT = "008611012347002";
    //密码
    public static final String PASSWORD = "qaz12345";
    //服务器
    public static final String SERVER = "im";
    //国家码
    public static final String NATIONAL_CODE = "0086";

    // 默认添加机器人echo，添加好友
    public static final long DEFAULT_APP = 9151315548226715951L;

    public static final double DEFAULT_LATITUDE = 34.198464;
    public static final double DEFAULT_LONGITUDE = 108.884845;
    public static final String DEFAULT_ADDRESS = "中国陕西省西安市雁塔区锦业一路";

    public static LocationBean initDefaultLocation (){
        LocationBean location = new LocationBean();
        location.setName("");
        location.setAddress(DEFAULT_ADDRESS);
        location.setLatitude(DEFAULT_LATITUDE);
        location.setLongitude(DEFAULT_LONGITUDE);
        return location;
    }

    //消息操作
    public static final int TYPE_MSG_FORWARD = 0x30;   // 转发
    public static final int TYPE_MSG_COLLECTION = 0x31;// 收藏
    public static final int TYPE_MSG_DELETE = 0x32;    // 删除
    public static final int TYPE_MSG_WITHDRAW = 0x33;  // 撤回

    //------- 聊天界面输入选项操作定义  ----//
    public static final int TYPE_PIC = 0X01;
    public static final int TYPE_FILE = 0X02;
    public static final int TYPE_POSITION = 0X03;
    public static final int TYPE_CARD = 0X04;
    public static final int TYPE_BURN = 0X05;
    public static final int TYPE_BURN_CANCEL = 0X06;
    public static final int TYPE_SHARK = 0X07;
    public static final int TYPE_VIDEO = 0x08;//音频视频聊天


    // { "图片", "文件","位置","名片", "阅后即焚", "抖一抖"， "音视频" };
    private static final int[][] ITEM_RES = {
            {TYPE_PIC, R.mipmap.vim_chat_option_photo, R.string.vim_chat_photo},
            {TYPE_FILE, R.mipmap.vim_chat_option_file, R.string.vim_chat_file},
            {TYPE_CARD, R.mipmap.vim_chat_option_card, R.string.vim_chat_card},
            {TYPE_POSITION, R.mipmap.vim_chat_option_location, R.string.vim_chat_location},
            {TYPE_BURN, R.mipmap.vim_chat_option_burn, R.string.vim_chat_burn},
            {TYPE_SHARK, R.mipmap.vim_chat_option_shark, R.string.vim_chat_shark}};


    /**
     * 获取聊天页面的操作选项
     */
    public static List<OptionBean> getOptionList(Context context) {
        List<OptionBean> list = new ArrayList<>();
        int size = ITEM_RES.length;
        for (int i = 0; i < size; i++) {
            OptionBean option = new OptionBean();
            option.setType(ITEM_RES[i][0]);
            option.setIcon(ITEM_RES[i][1]);
            option.setName(context.getResources().getString(ITEM_RES[i][2]));
            list.add(option);
        }
        return list;
    }
}
