package com.vrv.sdk.library.ui.view;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.api.ConfigApi;
import com.vrv.imsdk.api.MsgFile;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.imsdk.util.SDKUtils;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.VimConstant;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.bean.FileBean;
import com.vrv.sdk.library.utils.DialogUtil;
import com.vrv.sdk.library.utils.FileUtils;
import com.vrv.sdk.library.utils.ToastUtil;

/**
 * 聊天文件界面
 */
public class ChatFileView extends ChatMsgItemView {

    private ImageView imgView;//文件图标
    private TextView tvName;//文件名称
    private TextView tvContent;//文件大小
    private ProgressBar progressBar;
    private MsgFile msgFile;
    private DownloadHandler handler;
    private String fileName;

    public ChatFileView(Context context, ChatMsg messageBean) {
        super(context, messageBean);
    }

    @Override
    protected void loadView() {
        View view = View.inflate(context, R.layout.vim_view_chat_file, this);
        imgView = (ImageView) view.findViewById(R.id.img_file);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvContent = (TextView) view.findViewById(R.id.tv_size);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_chat_loading);
    }

    @Override
    protected void handleData() {
        msgFile = ChatMsgApi.parseFileJson(msgBean.getMessage());
        if (msgFile != null) {
            encryptKey = msgFile.getEncDecKey();
        }
    }

    @Override
    protected void display() {
        if (msgFile == null) {
            tvName.setText("文件消息体异常");
        } else {
            String fileName = msgFile.getFileName();
            if (fileName.contains("/")) {
                fileName = SDKUtils.getFileNameByPath(fileName);
            }
            FileBean.setFileIcon(imgView, fileName, msgFile.getFilePath());
            tvName.setText(fileName);
            tvContent.setText(FileBean.changeBite(msgFile.getFileSize()));
        }
    }

    @Override
    protected void onClick() {
        openFile();
    }

    @Override
    protected void onLongClick() {
        if (RequestHelper.isMyself(msgBean.getSendID()))
            isMe = true;
        CharSequence[] items;
        if (isMe) {
            items = new CharSequence[]{"转发", "删除", "更多"};
        } else {
            items = new CharSequence[]{"转发", "删除", "撤回", "更多"};
        }
        MaterialDialog.ListCallback itemOperateCallBack = new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                switch (i) {
                    case 0:
                        itemDataChangeListener.onItemOperation(VimConstant.TYPE_MSG_FORWARD, msgBean);
                        break;
                    case 1:
                        itemDataChangeListener.onItemOperation(VimConstant.TYPE_MSG_DELETE, msgBean);
                        break;
                    case 2:
                        if (isMe) {
                            itemDataChangeListener.ItemDataChange(true);
                        } else {
                            itemDataChangeListener.onItemOperation(VimConstant.TYPE_MSG_WITHDRAW, msgBean);
                        }
                        break;
                    default:
                        itemDataChangeListener.ItemDataChange(true);
                        break;
                }
            }
        };
        DialogUtil.buildOperateDialog(context, items, itemOperateCallBack).show();
    }

    //下载文件并打开
    private void openFile() {
        //// TODO: 文件不存在先下载然后打开
        if (msgFile == null) {
            return;
        }
        String realName = msgFile.getFileName();
        if (openMyFile(realName)) {
            return;
        }
        if (FileUtils.isExist(msgFile.getFilePath())) {
            openEncryptFile(msgFile.getFilePath(), encryptKey);
        } else {
            download();
        }
    }

    //处理自己发送的文件
    private boolean openMyFile(String realName) {
        if (msgBean != null && RequestHelper.isMyself(msgBean.getSendID())) {
            if (FileUtils.isExist(realName)) {
                FileUtils.openFile(context, realName, fileName);
                return true;
            }
        }
        return false;
    }

    //打开加密文件
    private void openEncryptFile(String encryptPath, String encryptKey) {
        String cachePath = ConfigApi.decryptFile(encryptKey, encryptPath);
        FileUtils.openFile(context, cachePath, fileName);
    }

    //下载
    private void download() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            ToastUtil.showShort(context, "正在下载");
        } else {
            progressBar.setVisibility(VISIBLE);
            handler = new DownloadHandler();
            boolean download = RequestHelper.downloadFile(msgBean, handler);
            if (!download) {
                handler.sendEmptyMessage(RequestHandler.REQUEST_FALSE);
            }
        }
    }

    class DownloadHandler extends RequestHandler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressBar.setVisibility(GONE);
        }

        @Override
        public void handleSuccess(Message msg) {
            openEncryptFile(msg.getData().getString(KEY_DATA), encryptKey);
        }
    }
}
