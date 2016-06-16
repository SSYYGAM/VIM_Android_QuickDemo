package com.vrv.sdk.library.ui.view;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.api.MsgImage;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.ui.activity.ChatActivity;
import com.vrv.sdk.library.ui.activity.ChatPhotosActivity;
import com.vrv.sdk.library.utils.FileUtils;
import com.vrv.sdk.library.utils.ImageUtil;
import com.vrv.sdk.library.utils.Utils;
import com.vrv.sdk.library.utils.VrvLog;

import java.util.ArrayList;

/**
 * Created by Yang on 2015/11/3 003.
 */
public class ChatImgView extends ChatMsgItemView {

    private ImageView imgView;
    private ProgressBar progressBar;
    private MsgImage msgImage;

    public ChatImgView(Context context, ChatMsg messageBean) {
        super(context, messageBean);
    }

    @Override
    protected void loadView() {
        View view = View.inflate(context, R.layout.vim_view_chat_img, this);
        imgView = (ImageView) view.findViewById(R.id.img_chat_image);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_chat_loading);
    }

    @Override
    protected void handleData() {
        msgImage = ChatMsgApi.parseImgJson(msgBean.getMessage());
        if (msgImage != null) {
            encryptKey = msgImage.getEncDecKey();
        }
    }

    @Override
    protected void display() {
        if (msgImage == null) {
            imgView.setImageResource(R.mipmap.vim_load_pic_lost);
            return;
        }
        setViewParam();
        String thumbPath = msgImage.getThumbShowPath();
        if (TextUtils.isEmpty(thumbPath)) {
            imgView.setImageResource(R.mipmap.vim_load_pic_lost);
            return;
        }
        if (msgBean != null && RequestHelper.isMyself(msgBean.getSendID())) {
            if (msgBean.getMsgStatus() == ChatMsg.STATUS_UPLOAD_FAILURE) {
                if (FileUtils.isExist(thumbPath)) {//自己发图时缩略图存在显示
                    ImageUtil.loadViewLocalWithEncrypt(context, "", thumbPath, imgView, R.mipmap.vim_load_pic_lost);
                    return;
                }
                ImageUtil.loadViewLocalWithEncrypt(context, "", msgImage.getOrgShowPath(), imgView, R.mipmap.vim_load_pic_lost);
                return;
            }
        }
        if (FileUtils.isExist(thumbPath)) {
            ImageUtil.loadViewLocalWithEncrypt(context, encryptKey, thumbPath, imgView, R.mipmap.vim_load_pic_lost);
        } else {
            progressBar.setVisibility(VISIBLE);
            //下载图片会保存到默认路径下
            boolean download = RequestHelper.downloadThumbImg(msgBean, new DownLoadHandler());
            if (!download) {
                progressBar.setVisibility(GONE);
                imgView.setImageResource(R.mipmap.vim_load_pic_lost);
            }
        }
    }

    private void setViewParam() {
        int height = msgImage.getHeight();
        int width = msgImage.getWidth();
        if (Math.min(width, height) <= 0) {
            return;
        }
        int max = Utils.dip2px(context, 120);
        if (Math.max(width, height) > max) {
            if (height > max) {
                width = width * max / height;
                height = max;
            } else {
                height = height * max / width;
                width = max;
            }
        }
        VrvLog.i(width + "X" + height);
        ViewGroup.LayoutParams params = imgView.getLayoutParams();
        params.width = width;
        params.height = height;
        imgView.setLayoutParams(params);
    }

    @Override
    protected void onClick() {
        ArrayList<ChatMsg> imgMsgList = new ArrayList<>();
        for (ChatMsg messageBean : ChatActivity.getMsgList()) {
            if (messageBean.getMessageType() == ChatMsgApi.TYPE_IMAGE) {
                imgMsgList.add(messageBean);
            }
        }
        ChatPhotosActivity.start(context, msgBean, imgMsgList);
    }

    @Override
    protected void onLongClick() {

    }

    class DownLoadHandler extends RequestHandler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressBar.setVisibility(GONE);
        }

        @Override
        public void handleFailure(int code, String message) {
            super.handleFailure(code, message);
            imgView.setImageResource(R.mipmap.vim_load_pic_lost);
        }

        @Override
        public void handleSuccess(Message msg) {
            try {//关闭之后可能崩溃
                String filePath = msg.getData().getString(KEY_DATA);
                ImageUtil.loadViewLocalWithEncrypt(context, encryptKey, filePath, imgView, R.mipmap.vim_load_pic_lost);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
