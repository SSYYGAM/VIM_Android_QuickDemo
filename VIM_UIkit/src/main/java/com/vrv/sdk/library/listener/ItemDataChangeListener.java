package com.vrv.sdk.library.listener;

import com.vrv.imsdk.model.ChatMsg;

/**
 * Created by zxj on 16-7-29.
 */
public interface ItemDataChangeListener {
    void ItemDataChange(boolean isShowCheckbox);

    void onItemOperation(int type, ChatMsg msg);
}
