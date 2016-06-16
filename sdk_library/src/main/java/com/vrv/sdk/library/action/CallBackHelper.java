package com.vrv.sdk.library.action;

import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;

import com.vrv.imsdk.api.ResponseCode;
import com.vrv.imsdk.bean.BaseBean;
import com.vrv.imsdk.bean.ResponseResult;
import com.vrv.imsdk.model.BaseModel;
import com.vrv.imsdk.model.ResultCallBack;

import java.util.ArrayList;


/**
 * SDK回掉方法处理类
 * Created by Yang on 2015/8/30 030.
 */
public class CallBackHelper {

    private static final String TAG = CallBackHelper.class.getSimpleName();

    public static <T> ResultCallBack buildCallBack(final RequestHandler handler) {

        return new ResultCallBack<T>() {
            @Override
            public void onSuccess(T result) {
                handleResult(RequestHandler.SUCCESS, ResponseCode.RSP_SUCCESS, "", result, handler);
            }

            @Override
            public void onError(int code, String message) {
                handleResult(RequestHandler.FAILURE, code, message, null, handler);
            }
        };
    }

    private static <T> void handleResult(int what, int arg1, String obj, T t, RequestHandler handler) {
        final Message msg = new Message();
        msg.what = what;
        if (arg1 == ResponseCode.RSP_SUCCESS) {
            if (t != null) {
                Bundle bundle = new Bundle();
                if (t instanceof BaseModel) {
                    bundle.putParcelable(RequestHandler.KEY_DATA, (BaseModel) t);
                } else if (t instanceof String) {
                    bundle.putString(RequestHandler.KEY_DATA, (String) t);
                } else if (t instanceof Byte) {
                    bundle.putByte(RequestHandler.KEY_DATA, (Byte) t);
                } else if (t instanceof ArrayList) {
                    if (((ArrayList) t).size() > 0 && ((ArrayList) t).get(0).getClass().getSimpleName().equals("Long")) {
                        bundle.putSerializable(RequestHandler.KEY_DATA, (ArrayList<Long>) t);
                    } else {
                        bundle.putParcelableArrayList(RequestHandler.KEY_DATA, (ArrayList<? extends Parcelable>) t);
                    }
                    //                    bundle.putParcelableArrayList(RequestHandler.KEY_DATA, (ArrayList<? extends Parcelable>) t);
                } else if (t instanceof Long) {
                    bundle.putLong(RequestHandler.KEY_DATA, (Long) t);
                } else if (t instanceof Integer) {
                    bundle.putInt(RequestHandler.KEY_DATA, (Integer) t);
                } else if (t instanceof ResponseResult) {
                    bundle.putParcelable(RequestHandler.KEY_DATA, (ResponseResult) t);
                } else if (t instanceof BaseBean) {
                    bundle.putParcelable(RequestHandler.KEY_DATA, (Parcelable) t);
                } else if (t instanceof Parcelable) {
                    bundle.putParcelable(RequestHandler.KEY_DATA, (Parcelable) t);
                }
                msg.setData(bundle);
            }
        } else {
            msg.arg1 = arg1;
            msg.obj = obj;
        }
        if (handler != null) {
            handler.sendMessage(msg);
        }
    }
}
