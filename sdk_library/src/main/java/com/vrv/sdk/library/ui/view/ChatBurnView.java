package com.vrv.sdk.library.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.vrv.imsdk.SDKClient;
import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.api.ConfigApi;
import com.vrv.imsdk.api.MsgAudio;
import com.vrv.imsdk.api.MsgFile;
import com.vrv.imsdk.api.MsgImage;
import com.vrv.imsdk.api.MsgPosition;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.imsdk.model.ResultCallBack;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.bean.FileBean;
import com.vrv.sdk.library.bean.OptionBean;
import com.vrv.sdk.library.ui.activity.ChatPhotosActivity;
import com.vrv.sdk.library.utils.DialogUtil;
import com.vrv.sdk.library.utils.FileUtils;
import com.vrv.sdk.library.utils.ToastUtil;

import java.util.ArrayList;

/**
 * Created by zxj on 2016/6/20.
 */
public class ChatBurnView extends ChatMsgItemView {

    private LinearLayout llBurn;
    private int newType;
    private Context mContext;
    private ChatMsg mChatMsg;
    private static final int TIME = 15;
    // 以下是解析后的消息
    private String msg;
    private MsgImage imgBean;
    private MsgFile msgFile;
    private MsgPosition msgPosition;
    private MsgAudio msgAudio;
    private AlertDialog.Builder dialogBuilder;
    private ViewGroup rootView; // 消息显示的根视图
    private ViewGroup fillViewGroup; // 消息的填充位置
    private ProgressBar numberPrg; // 倒计时时间
    private Button btnBurn;
    private TextView timeTxt;
    private String burnTime;
    private CountDownTimer countDownTimer;
    private ProgressBar fileProgressBar; // 文件下载进度
    private AnimationDrawable animationDrawable;
    private View audioImg;
    private ImageView imgView;
    private Handler mHandler;
    private boolean isDownload;
    private boolean isDownloading;

    public ChatBurnView(Context context, ChatMsg msgBean) {
        super(context, msgBean);
        mContext = context;
        mChatMsg = msgBean;
    }

    @Override
    protected void loadView() {
        View view = View.inflate(context, R.layout.vim_view_chat_burn, this);
        llBurn = (LinearLayout) view.findViewById(R.id.ll_chat_burn);
    }

    @Override
    protected void handleData() {
        if (msgBean == null)
            return;
        if (!RequestHelper.isMyself(msgBean.getSendID())) {
            newType = ChatMsgApi.reCalculateMsgType(msgBean.getMessageType());
            switch (newType) {
                case ChatMsgApi.TYPE_TEXT:
                    msg = ChatMsgApi.parseTxtJson(msgBean.getMessage());
                    break;
                case ChatMsgApi.TYPE_IMAGE:
                    imgBean = ChatMsgApi.parseImgJson(msgBean.getMessage());
                    break;
                case ChatMsgApi.TYPE_FILE:
                    msgFile = ChatMsgApi.parseFileJson(msgBean.getMessage());
                    break;
                case ChatMsgApi.TYPE_POSITION:
                    msgPosition = ChatMsgApi.parsePositionJson(msgBean.getMessage());
                    break;
                case ChatMsgApi.TYPE_VOICE:
                    msgAudio = ChatMsgApi.parseAudioJson(msgBean.getMessage());
                    break;
            }
        }
    }

    @Override
    protected void display() {
    }

    @Override
    protected void onClick() {

        dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        rootView = (ViewGroup) inflater.inflate(R.layout.vim_dialog_burn, null);
        fillViewGroup = (ViewGroup) rootView.findViewById(R.id.fill_content);
        numberPrg = (ProgressBar) rootView.findViewById(R.id.nprg_time);
        numberPrg.setMax(TIME); // 设置为5s的倒计时
        timeTxt = (TextView) rootView.findViewById(R.id.tv_time);
        btnBurn = (Button) rootView.findViewById(R.id.btn_burn);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                instantBurnMsg();
                countDownTimer.cancel();
            }
        });
        switch (newType) {
            case ChatMsgApi.TYPE_TEXT:
                setTxtviView();
                break;
            case ChatMsgApi.TYPE_IMAGE:
                setImgView();
                break;
            case ChatMsgApi.TYPE_FILE:

                break;
            case ChatMsgApi.TYPE_POSITION:

                break;
            case ChatMsgApi.TYPE_VOICE:

                break;
        }
        dialogBuilder.setView(rootView);
        burnTime = mContext.getResources().getString(R.string.vim_burn_time);
        timeTxt.setText(String.format(burnTime, TIME));
        final AlertDialog dialog = dialogBuilder.show();
        btnBurn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler = null;
                dialog.dismiss();
                instantBurnMsg();
                countDownTimer.cancel();
            }
        });
        // 倒计时
        countDownTimer = new CountDownTimer(TIME * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                int remainingTime = (int) (millisUntilFinished / 1000);
                numberPrg.setProgress(remainingTime);
                timeTxt.setText(String.format(burnTime, remainingTime));
            }

            @Override
            public void onFinish() {
                timeTxt.setText(String.format(burnTime, 0));
                numberPrg.setProgress(0);
                dialog.dismiss();
                instantBurnMsg();
            }
        };


        // todo：等待下载
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    countDownTimer.start();
                }
            }
        };
        if (newType == ChatMsgApi.TYPE_TEXT) {
            Message message = Message.obtain();
            message.what = 1;
            mHandler.handleMessage(message);
        }
    }

    private void instantBurnMsg() {
        ArrayList<Long> arrayList = new ArrayList<Long>();
        arrayList.add(mChatMsg.getMessageID());
        RequestHelper.deleteMsgByID(mChatMsg.getTargetID(), arrayList);
        msg = null;
        imgBean = null;
        msgFile = null;
        msgPosition = null;
        msgAudio = null;
        dialogBuilder = null;
        refreshData();
    }

    @Override
    protected void onLongClick() {
        CharSequence[] items = new CharSequence[]{"删除"};
        final MaterialDialog.ListCallback itemOperateCallBack = new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                switch (i) {
                    case 0:
                        itemDataChangeListener.onItemOperation(OptionBean.TYPE_OPTION_MSG_DELETE, msgBean);
                        break;

                }
            }
        };
        DialogUtil.buildOperateDialog(context, items, itemOperateCallBack).show();
    }

    private void setTxtviView() {
        TextView contentTxt = new TextView(mContext);
        contentTxt.setText(msg);
        contentTxt.setGravity(Gravity.CENTER_HORIZONTAL);
        fillViewGroup.addView(contentTxt);
    }

    private void setImgView() {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.vim_view_chat_img, null);
        imgView = (ImageView) dialogView.findViewById(R.id.img_chat_image);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        imgView.setLayoutParams(layoutParams);
        fileProgressBar = (ProgressBar) dialogView.findViewById(R.id.progress_chat_loading);
        fileProgressBar.setVisibility(View.VISIBLE);
        RequestHelper.downloadThumbImg(mChatMsg, new ImgDownLoadHandler());
        imgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDownloading) {
                    ToastUtil.showShort(mContext, "正在下载．．．");
                    return;
                }
                if (isDownload) {
                    ToastUtil.showShort(mContext, "放大图片");
                    ChatPhotosActivity.start(context, msgBean, null);
                } else {
                    RequestHelper.downloadThumbImg(mChatMsg, new ImgDownLoadHandler());
                }
            }
        });
        fillViewGroup.addView(dialogView);
    }


    /**
     * 刷新界面数据
     */
    private void refreshData() {
        itemDataChangeListener.onItemOperation(OptionBean.TYPE_OPTION_MSG_DELETE, msgBean);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && dialogBuilder != null) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class ImgDownLoadHandler extends RequestHandler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            fileProgressBar.setVisibility(GONE);
        }

        @Override
        public void handleFailure(int code, String message) {
            super.handleFailure(code, message);
            imgView.setImageResource(R.mipmap.load_pic_lost);
            isDownload = false;
        }

        @Override
        public void handleSuccess(Message msg) {
            try {//关闭之后可能崩溃
                //                String filePath = msg.getData().getString(KEY_DATA);
                //                ImageUtil.loadViewLocalWithEncrypt(context, encryptKey, filePath, imgView, R.mipmap.load_pic_lost);
                String decryptFile = ConfigApi.decryptFile(imgBean.getEncDecKey(), imgBean.getThumbShowPath());
                imgView.setImageBitmap(BitmapFactory.decodeFile(decryptFile));
                sendDownloadSuccess();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendDownloadSuccess() {
        isDownloading = false;
        if (mHandler != null) {
            Message message = Message.obtain();
            message.what = 1;
            mHandler.handleMessage(message);
        }
        isDownload = true;
    }

}
