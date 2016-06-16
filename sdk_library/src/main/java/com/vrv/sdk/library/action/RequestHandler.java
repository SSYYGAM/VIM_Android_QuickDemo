package com.vrv.sdk.library.action;


import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.vrv.imsdk.api.ResponseCode;
import com.vrv.sdk.library.SDKApp;
import com.vrv.sdk.library.utils.DialogUtil;
import com.vrv.sdk.library.utils.ToastUtil;
import com.vrv.sdk.library.utils.VrvLog;


/**
 * {@link CallBackHelper} SDK请求使用
 * Created by Yang on 2015/8/30 030.
 */
public abstract class RequestHandler extends Handler {

    private static final String TAG = RequestHandler.class.getSimpleName();
    public static final String KEY_DATA = "data";
    public static final int SUCCESS = 0;
    public static final int FAILURE = -1;
    public static final int REQUEST_FALSE = -2;
    /**
     * 显示进度对话框
     */
    public static final int SHOW_PRO = 2;
    public static final int DIS_PRO = 3;

    private Dialog dialog;

    public RequestHandler() {

    }

    public RequestHandler(Context context) {
        this.dialog = DialogUtil.buildProgressDialog(context);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        switch (msg.what) {
            case SUCCESS:
                handleSuccess(msg);
                break;
            case REQUEST_FALSE:
                break;
            case FAILURE:
                handleFailure(msg.arg1, String.valueOf(msg.obj));
                break;
            case SHOW_PRO:
                if (dialog != null) {
                    dialog.show();
                }
                break;
            case DIS_PRO:
                try {
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                } catch (Exception e) {
                    VrvLog.e(TAG, "dismiss dialog Exception：" + e);
                }
                break;
        }
    }

    public abstract void handleSuccess(Message msg);

    public void handleFailure(int code, String message) {
        VrvLog.e(TAG, " 请求失败 {code=" + code + ",msg=" + message + "}");
        String toast = "Code=" + code;
        switch (code) {
            case ResponseCode.ERR_UPDATE:
                toast = "Error Update";
                break;
            case ResponseCode.ERR_CANCEL:
                toast = "Error Cancel";
                break;
            case ResponseCode.ERR_DB_NO_ACCESS:
                toast = "数据库无法访问 Error DB Not Access";
                break;
            case ResponseCode.ERR_DB_UPDATE:
                toast = "数据库更新 Error DB Update";
                break;
            case ResponseCode.ERR_OPEN_DB_FAILED:
                toast = "数据库打开失败 Error DB Open Failed";
                break;
            case ResponseCode.ERR_PARAM:
                toast = "参数错误 Error Param";
                break;
            case ResponseCode.ERR_RESOURCE_NOT_EXIST:
                toast = "资源不存在 Error Resource Not Exist";
                break;
            case ResponseCode.ERR_START:
                toast = "Error Start";
                break;
            case ResponseCode.ERR_CERTIFICATE_INVALID:
                toast = "证书失效 Error Certificate invalid";
                break;
            case ResponseCode.ERR_CERTIFICATE_LIMITED:
                toast = "证书权限不够 Error Certificate Limited";
                break;
            case ResponseCode.ERR_DB_UPDATE_FAIL:
                toast = "数据库升级失败 Error DB Update fail";
                break;
            case ResponseCode.ERR_InviteMuchOtherEntMem:
                toast = "邀请互联成员太多 Error InviteMuchOtherEntMem";
                break;
            case ResponseCode.ERR_MSG_ID_INVALID:
                toast = "设置未读消息ID大于最大消息ID Error MsgID invalid";
                break;
            case ResponseCode.ERR_REQUEST_FREQUENT:
                toast = "请求频繁 Error Request Frequent";
                break;
        }
        ToastUtil.showShort(SDKApp.getContext(), toast);
    }
}
