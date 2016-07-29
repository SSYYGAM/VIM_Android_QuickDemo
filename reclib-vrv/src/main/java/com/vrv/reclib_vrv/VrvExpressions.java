package com.vrv.reclib_vrv;

import com.sj.emoji.EmojiBean;

import java.util.ArrayList;
import java.util.List;

import sj.keyboard.data.EmoticonEntity;


/**
 * 所有表情(Emoji,自定义字符，动态表情)
 * Created by Yang on 2015/8/20 020.
 */
public class VrvExpressions {
    public static final String ORDER_DELETE = "&:delete";
    public static final String ORDER_DELETE_TODAY = "&:delete|today";
    public static final String ORDER_DELETE_ALL = "&:delete|all";
    public static final String ORDER_FLASH = "&:flash";

    /**
     * 字符表情id
     */
    private static int[] EXPRESSION_ICON = new int[]{
            R.drawable.e40a, R.drawable.e402, R.drawable.e417,
            R.drawable.e105, R.drawable.e409, R.drawable.e411,
            R.drawable.e404, R.drawable.e405, R.drawable.e40e,
            R.drawable.e403, R.drawable.e407, R.drawable.e418,
            R.drawable.e416, R.drawable.e401, R.drawable.e40b,
            R.drawable.e40f, R.drawable.e107, R.drawable.e410,
            R.drawable.e40d, R.drawable.e40c, R.drawable.e22e,
            R.drawable.e22f, R.drawable.e230, R.drawable.e231,
            R.drawable.e14c, R.drawable.e00d, R.drawable.e420,
            R.drawable.e00e, R.drawable.e421, R.drawable.e41f,
            R.drawable.e41d, R.drawable.e41b, R.drawable.e41a,
            R.drawable.e41c, R.drawable.e00f, R.drawable.e011,
            R.drawable.e346, R.drawable.e345, R.drawable.e348,
            R.drawable.e347, R.drawable.e33e, R.drawable.e340,
            R.drawable.e33f, R.drawable.e339, R.drawable.e33b,
            R.drawable.e344, R.drawable.e43f, R.drawable.e419,
            R.drawable.e30e, R.drawable.e047, R.drawable.e30c,
            R.drawable.e30f, R.drawable.e034, R.drawable.e437,
            R.drawable.e307, R.drawable.e032, R.drawable.e120,
            R.drawable.e342, R.drawable.e147, R.drawable.e044,
            R.drawable.e314, R.drawable.e34b, R.drawable.e033,
            R.drawable.e310, R.drawable.e312, R.drawable.e10e,
            R.drawable.e536, R.drawable.e325, R.drawable.e045,
            R.drawable.e11d, R.drawable.e13d, R.drawable.e32e,
            R.drawable.e131, R.drawable.e311, R.drawable.e05a,
            R.drawable.e12f, R.drawable.e101, R.drawable.e135,
            R.drawable.e136, R.drawable.e159, R.drawable.e134,
            R.drawable.e335, R.drawable.e109, R.drawable.e001,
            R.drawable.e002, R.drawable.e004, R.drawable.e005,
            R.drawable.e13b, R.drawable.e03f, R.drawable.e144,
            R.drawable.e10d, R.drawable.e435, R.drawable.e01b,
            R.drawable.e01c, R.drawable.e123, R.drawable.e01d,
            R.drawable.e053, R.drawable.e52b, R.drawable.e050,
            R.drawable.e11b, R.drawable.e04e, R.drawable.e52e,
            R.drawable.e10a, R.drawable.e522, R.drawable.e523,
            R.drawable.e055, R.drawable.e527, R.drawable.e054,
            R.drawable.e052, R.drawable.e10b, R.drawable.e531,
            R.drawable.e318, R.drawable.e319, R.drawable.e11c,
            R.drawable.e31c, R.drawable.e13c, R.drawable.e331,
            R.drawable.e330, R.drawable.e441, R.drawable.e525,
            R.drawable.e03c, R.drawable.e03d, R.drawable.e133,
            R.drawable.e041, R.drawable.e036, R.drawable.e155,
            R.drawable.e14d, R.drawable.e154, R.drawable.e158,
            R.drawable.e156, R.drawable.e037, R.drawable.e12d,
            R.drawable.e00c, R.drawable.e126, R.drawable.e14e,
            R.drawable.e137, R.drawable.e140, R.drawable.e13f,
            R.drawable.e113, R.drawable.e320, R.drawable.e327,
            R.drawable.e009, R.drawable.e12c, R.drawable.e20f,
            R.drawable.e00b, R.drawable.e00a, R.drawable.e008,
            R.drawable.e138, R.drawable.e139, R.drawable.e323,
            R.drawable.e007, R.drawable.e43c, R.drawable.e04c,
            R.drawable.e006, R.drawable.e322, R.drawable.e13e,
            R.drawable.e31b, R.drawable.e048, R.drawable.e332,
            R.drawable.e333, R.drawable.e04a, R.drawable.e049,
            R.drawable.e04b, R.drawable.e336, R.drawable.e337};

    /**
     * 字符表情名称
     */
    private static String[] EXPRESSION_NAME = new String[]{
            "1f60c", "1f60f", "1f61a", "1f61c",
            "1f61d", "1f62d", "1f601", "1f609",
            "1f612", "1f614", "1f616", "1f618",
            "1f621", "1f625", "1f628", "1f630",
            "1f631", "1f632", "1f633", "1f637",
            "1f446", "1f447", "1f448", "1f449",
            "1f4aa", "1f44a", "1f44c", "1f44d",
            "1f44e", "1f44f", "1f64f", "1f442",
            "1f443", "1f444", "261d", "270c",
            "1f34a", "1f34e", "1f349", "1f353",
            "1f35a", "1f35c", "1f35d", "1f35e",
            "1f35f", "1f363", "1f367", "1f440",
            "1f6ac", "1f37a", "1f37b", "1f48a",
            "1f48d", "1f49d", "1f334", "1f339",
            "1f354", "1f359", "1f373", "1f378",
            "1f380", "1f382", "1f384", "1f388",
            "1f389", "1f451", "1f463", "1f514",
            "2615", "1f525", "26a1", "2728",
            "1f3c6", "1f4a3", "1f4a9", "1f4b0",
            "1f4eb", "1f6a4", "1f6b2", "1f68c",
            "1f40e", "1f31f", "1f435", "1f466",
            "1f467", "1f468", "1f469", "1f489",
            "1f511", "1f512", "1f680", "1f684",
            "1f697", "26f5", "2668", "2708",
            "1f42d", "1f42e", "1f42f", "1f47b",
            "1f47c", "1f414", "1f419", "1f420",
            "1f424", "1f427", "1f428", "1f433",
            "1f436", "1f437", "1f438", "1f452",
            "1f457", "1f480", "1f484", "1f4a4",
            "1f4a6", "1f4a8", "1f41a", "1f41b",
            "1f3a4", "1f3a5", "1f3b0", "1f3b8",
            "1f3e0", "1f3e5", "1f3e6", "1f3e7",
            "1f3e8", "1f3ea", "26ea", "1f004",
            "1f4bb", "1f4bf", "1f6a5", "1f6a7",
            "1f6bd", "1f6c0", "1f52b", "1f488",
            "1f493", "260e", "303d", "2663",
            "1f4e0", "1f4f1", "1f4f7", "1f6b9",
            "1f6ba", "1f45c", "1f45f", "1f302",
            "1f319", "1f455", "1f459", "1f460",
            "1f462", "26c4", "2b55", "274c",
            "2600", "2601", "2614", "2754",
            "2755"};

    /**
     * 自定义表情id
     */
    private static int[] CUSTOM_ICON = new int[]{
            R.drawable.c_weixiao, R.drawable.c_se, R.drawable.c_deyi, R.drawable.c_liulei, R.drawable.c_haixiu, R.drawable.c_bizui, R.drawable.c_shui, R.drawable.c_ganga,
            R.drawable.c_fanu, R.drawable.c_tiaopi, R.drawable.c_ciya, R.drawable.c_jingya, R.drawable.c_zhuakuang, R.drawable.c_touxiao, R.drawable.c_baiyan, R.drawable.c_koubi,
            R.drawable.c_liuhan, R.drawable.c_yiwen, R.drawable.c_bugaoxing, R.drawable.c_yun, R.drawable.c_fengle, R.drawable.c_qiaoda, R.drawable.c_shuai, R.drawable.c_zaijian,
            R.drawable.c_guzhang, R.drawable.c_zuohengheng, R.drawable.c_youhengheng, R.drawable.c_bishi, R.drawable.c_weiqu, R.drawable.c_yinxian, R.drawable.c_qinqin, R.drawable.c_kelian,
            R.drawable.c_aida, R.drawable.c_aoman, R.drawable.c_buxie, R.drawable.c_daxiao, R.drawable.c_heng, R.drawable.c_huachi, R.drawable.c_ku, R.drawable.c_kun,
            R.drawable.c_lenghan, R.drawable.c_nu, R.drawable.c_qiexiao, R.drawable.c_xia, R.drawable.c_zhemo};

    /**
     * 自定义表情名称
     */
    private static String[] CUSTOM_NAME = new String[]{
            "[微笑]", "[色]", "[得意]", "[流泪]", "[害羞]", "[闭嘴]", "[睡]", "[尴尬]",
            "[发怒]", "[调皮]", "[呲牙]", "[惊讶]", "[抓狂]", "[偷笑]", "[白眼]", "[抠鼻]",
            "[流汗]", "[疑问]", "[不高兴]", "[晕]", "[疯了]", "[敲打]", "[衰]", "[再见]",
            "[鼓掌]", "[左哼哼]", "[右哼哼]", "[鄙视]", "[委屈]", "[阴险]", "[亲亲]", "[可怜]",
            "[挨打]", "[傲慢]", "[不屑]", "[大笑]", "[哼]", "[花痴]", "[哭]", "[困]",
            "[冷汗]", "[怒]", "[窃笑]", "[吓]", "[折磨]"
    };

    private static int[] DYNAMIC_GIF = new int[]{
            R.drawable.dynamic_expression_01,
            R.drawable.dynamic_expression_02,
            R.drawable.dynamic_expression_03,
            R.drawable.dynamic_expression_04,
            R.drawable.dynamic_expression_05,
            R.drawable.dynamic_expression_06,
            R.drawable.dynamic_expression_07,
            R.drawable.dynamic_expression_08,
            R.drawable.dynamic_expression_09,
            R.drawable.dynamic_expression_10,
            R.drawable.dynamic_expression_11,
            R.drawable.dynamic_expression_12,
            R.drawable.dynamic_expression_13,
            R.drawable.dynamic_expression_14,
            R.drawable.dynamic_expression_15,
            R.drawable.dynamic_expression_16,
            R.drawable.dynamic_expression_17,
            R.drawable.dynamic_expression_18,
            R.drawable.dynamic_expression_19,
            R.drawable.dynamic_expression_20,
            R.drawable.dynamic_expression_21,
            R.drawable.dynamic_expression_22,
            R.drawable.dynamic_expression_23,
            R.drawable.dynamic_expression_24,
            R.drawable.dynamic_expression_25,
            R.drawable.dynamic_expression_26,
            R.drawable.dynamic_expression_27,
            R.drawable.dynamic_expression_28,
            R.drawable.dynamic_expression_29
    };

    private static String[] DYNAMIC_NAME = new String[]{
            "Dynamic_Expression_01.gif",
            "Dynamic_Expression_02.gif",
            "Dynamic_Expression_03.gif",
            "Dynamic_Expression_04.gif",
            "Dynamic_Expression_05.gif",
            "Dynamic_Expression_06.gif",
            "Dynamic_Expression_07.gif",
            "Dynamic_Expression_08.gif",
            "Dynamic_Expression_09.gif",
            "Dynamic_Expression_10.gif",
            "Dynamic_Expression_11.gif",
            "Dynamic_Expression_12.gif",
            "Dynamic_Expression_13.gif",
            "Dynamic_Expression_14.gif",
            "Dynamic_Expression_15.gif",
            "Dynamic_Expression_16.gif",
            "Dynamic_Expression_17.gif",
            "Dynamic_Expression_18.gif",
            "Dynamic_Expression_19.gif",
            "Dynamic_Expression_20.gif",
            "Dynamic_Expression_21.gif",
            "Dynamic_Expression_22.gif",
            "Dynamic_Expression_23.gif",
            "Dynamic_Expression_24.gif",
            "Dynamic_Expression_25.gif",
            "Dynamic_Expression_26.gif",
            "Dynamic_Expression_27.gif",
            "Dynamic_Expression_28.gif",
            "Dynamic_Expression_29.gif"
    };

    private static int[] INSTRUCTION_ICON = new int[]{
            R.drawable.cmd_receipt,
            R.drawable.cmd_task,
            R.drawable.cmd_delay,
            R.drawable.cmd_eraser,
            R.drawable.cmd_shake,
            R.drawable.cmd_help
    };
    private static String[] INSTRUCTION_NAME = new String[]{
            "阅后回执", "任务", "延时消息", ORDER_DELETE, ORDER_FLASH, "帮助"
    };
    private static String[] INSTRUCTION_DESCRIBE = new String[]{
            "阅后回执", "任务", "延时消息", "橡皮擦", "抖一抖", "帮助"
    };


    //emoji表情列表
    private static ArrayList<EmojiBean> emoList;
    //自定义表情列表
    private static ArrayList<EmojiBean> customList;
    //动态表情列表
    private static ArrayList<EmojiBean> dynamicList;
    // 指令表情
    private static ArrayList<EmoticonEntity> instructionList;

    public static ArrayList<EmojiBean> getEmoList() {
        if (emoList == null || emoList.size() < 0) {
            emoList = init(EXPRESSION_ICON, EXPRESSION_NAME);
        }
        return emoList;
    }

    public static ArrayList<EmojiBean> getCustomList() {
        if (customList == null || customList.size() < 0) {
            customList = init(CUSTOM_ICON, CUSTOM_NAME);
        }
        return customList;
    }

    public static ArrayList<EmojiBean> getDynamicList() {
        if (dynamicList == null || dynamicList.size() < 0) {
            dynamicList = init(DYNAMIC_GIF, DYNAMIC_NAME);
        }
        return dynamicList;
    }

    public static ArrayList<EmoticonEntity> getInstructionList() {
        if (instructionList == null || instructionList.size() < 0) {
            instructionList = init(INSTRUCTION_ICON, INSTRUCTION_NAME, INSTRUCTION_DESCRIBE);
        }
        return instructionList;
    }

    private static ArrayList<EmojiBean> init(int[] icons, String[] names) {
        ArrayList<EmojiBean> list = new ArrayList<>();
        if (icons == null || names == null) {
            return list;
        }
        int size = Math.min(icons.length, names.length);
        for (int i = 0; i < size; i++) {
            EmojiBean option = new EmojiBean(icons[i], names[i]);
            list.add(option);
        }
        return list;
    }


    /**
     * @param icons    资源id
     * @param names    资源对于的字符串
     * @param describe 表情显示文字
     * @return
     */
    private static ArrayList<EmoticonEntity> init(int[] icons, String[] names, String[] describe) {
        ArrayList<EmoticonEntity> list = new ArrayList<>();
        if (icons == null || names == null || describe == null) {
            return list;
        }
        int size = Math.min(icons.length, names.length);
        size = Math.min(size, describe.length);
        for (int i = 0; i < size; i++) {
            EmoticonEntity option = new EmoticonEntity(icons[i], names[i], describe[i]);
            list.add(option);
        }
        return list;
    }

    /**
     * 通过表情名获取表情id
     *
     * @param name
     * @return
     */
    public static int getResIdByName(String name) {
        for (int i = 0; i < DYNAMIC_NAME.length; i++) {
            if (name.trim().equals(DYNAMIC_NAME[i])) {
                return DYNAMIC_GIF[i];
            }
        }
        return 0;
    }

    public static int getResIdByName(String name, List<EmojiBean> lists) {
        for (EmojiBean bean : lists) {
            if (bean.emoji.equals(name)) {
                return bean.icon;
            }
        }
        return 0;
    }
}
