package com.vrv.sdk.library.common;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.vrv.imsdk.VIMClient;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.utils.DateTimeUtils;
import com.vrv.sdk.library.utils.ImageUtil;

/**
 * 用户信息配置
 * 例如获取性别图标，返回性别，电话号码添加去除国家区域码...
 */
public class UserInfoConfig {

    private static final String TAG = UserInfoConfig.class.getSimpleName();

    //性别：保密,也有可能未设置
    public static final byte UNKNOWN = 0;
    //性别：男
    public static final byte MALE = 1;
    //性别：女
    public static final byte FEMALE = 2;

    /**
     * 获取性别头像
     *
     * @param gender
     * @return
     */
    public static int getGenderHead(byte gender) {
        if (MALE == gender) {
            return R.mipmap.vim_icon_default_user;
        } else if (FEMALE == gender) {
            return R.mipmap.vim_icon_default_user;
        }
        return R.mipmap.vim_icon_default_user;
    }

    /**
     * 根据性别加载头像处理
     *
     * @param context
     * @param path
     * @param headView
     * @param gender
     */
    public static void loadHeadByGender(Context context, String path, ImageView headView, byte gender) {
        int def = getGenderHead(gender);
        loadHead(context, path, headView, def);
    }

    /**
     * 加载头像，
     *
     * @param context
     * @param path
     * @param headView
     */
    public static void loadHead(Context context, String path, ImageView headView, int def) {
        if (TextUtils.isEmpty(path) || !path.contains("/")) {
            ImageUtil.loadDefaultHead(context.getApplicationContext(), headView, def);
            return;
        }
        ImageUtil.loadViewLocalHead(context.getApplicationContext(), path, headView, def);
    }

    /**
     * 获取性别
     *
     * @param gender gender
     * @return string id
     */
    public static int getGender(byte gender) {
        return getGender(gender, false);
    }

    /**
     * 获取性别
     *
     * @param gender   性别
     * @param isMyInfo 是否为自己信息（未知？保密）
     * @return 性别
     */
    public static int getGender(byte gender, boolean isMyInfo) {
        if (MALE == gender)
            return R.string.vim_male;
        else if (FEMALE == gender)
            return R.string.vim_female;
        //显示自己性别未知，别人性别为保密
        return R.string.vim_unKnow;
    }

    /**
     * 显示手机号，不显示国家码
     *
     * @param orgPhoneNo 带区号手机
     * @return 不带区号手机号
     */
    public static String showPhone(String orgPhoneNo) {
        if (TextUtils.isEmpty(orgPhoneNo)) {
            return "";
        }
        if (orgPhoneNo.length() <= 4) {
            return orgPhoneNo;
        }
        return orgPhoneNo.substring(4);
    }

    /**
     * 显示生日
     *
     * @param when long
     * @return 年-月-日
     */
    public static String showBirth(long when) {
        String birthday = DateTimeUtils.longParseDate("yyyy-MM-dd", when);
        if (TextUtils.isEmpty(birthday)) {
            return VIMClient.getContext().getString(R.string.vim_unKnow);
        }
        return birthday;
    }

    public static String getName(String name, String remark) {
        if (TextUtils.isEmpty(remark)) {
            return TextUtils.isEmpty(name) ? "" : name;
        } else {
            return remark;
        }
    }

    /**
     * 最近联系人时间显示
     * @param when
     * @return
     */
    public static String formatConversationTime(long when) {
        return DateTimeUtils.formatTimeNew(when, false, false);
    }

    /**
     * 聊天消息时间显示
     * @param when
     * @return
     */
    public static String formatMessageTime(long when) {
        return DateTimeUtils.formatTimeNew(when, false, true);
    }
}
