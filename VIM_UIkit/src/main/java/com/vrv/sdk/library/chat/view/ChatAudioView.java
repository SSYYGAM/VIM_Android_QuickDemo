package com.vrv.sdk.library.chat.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.api.ConfigApi;
import com.vrv.imsdk.api.MsgAudio;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.VimConstant;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.utils.AudioUtils;
import com.vrv.sdk.library.utils.DialogUtil;
import com.vrv.sdk.library.utils.FileUtils;
import com.vrv.sdk.library.utils.ToastUtil;

/**
 * 聊天音频
 * Created by Yang on 2015/11/10 010.
 */
public class ChatAudioView extends ChatMsgItemView {

    private View imgView;
    private TextView tvTime;
    private MsgAudio msgAudio;
    private boolean from = true;
    private AnimationDrawable animationDrawable;
    private int minItemWidth;
    private int maxItemWidth;
    private RelativeLayout rlAudioRoot;
    View view;
    private static long playingMsgID;//正在播放的消息id

    public ChatAudioView(Context context, ChatMsg msgBean) {
        super(context, msgBean);
    }

    @Override
    protected void loadView() {
        from = !RequestHelper.isMyself(msgBean.getSendID());
        view = inflate(context, R.layout.vim_view_chat_audio, this);
        tvTime = (TextView) view.findViewById(R.id.tv_audio_time);
        rlAudioRoot = (RelativeLayout) view.findViewById(R.id.rl_audio_root);
        if (from) {
            imgView = view.findViewById(R.id.img_audio_from);
            imgView.setBackgroundResource(R.mipmap.vim_chat_audio_from3);
            tvTime.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            tvTime.setTextColor(getResources().getColor(R.color.vim_tx_gray));
            view.findViewById(R.id.img_audio_to).setVisibility(GONE);
        } else {
            imgView = view.findViewById(R.id.img_audio_to);
            imgView.setBackgroundResource(R.mipmap.vim_chat_audio_to3);
            tvTime.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            tvTime.setTextColor(getResources().getColor(android.R.color.white));
            view.findViewById(R.id.img_audio_from).setVisibility(GONE);
        }

        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        maxItemWidth = (int) (widthPixels * 0.35f);
        minItemWidth = (int) (widthPixels * 0.15f);
    }


    @Override
    protected void handleData() {
        msgAudio = ChatMsgApi.parseAudioJson(msgBean.getMessage());
        if (msgAudio == null) {
            return;
        }
        encryptKey = msgAudio.getEncDecKey();
        if (!FileUtils.isExist(msgAudio.getFilePath())) {
            RequestHelper.downloadFile(msgBean, new RequestHandler() {
                @Override
                public void handleSuccess(Message msg) {

                }
            });
        }
    }

    @Override
    protected void display() {
        if (msgAudio == null) {
            return;
        }
        tvTime.setText(msgAudio.getTime());
        ViewGroup.LayoutParams lp = rlAudioRoot.getLayoutParams();
        long duration = msgAudio.getMediaTime() / 1000;
        if (duration > 60) {
            duration = 60;
        }
        lp.width = (int) (minItemWidth + maxItemWidth / 60f * duration);
        //        ViewGroup.LayoutParams lp = getLayoutParams();
        //        lp.width = (lp.width + (msgAudio.getMediaTime() / 1000)* 2);
        if (from) {
            imgView.setBackgroundResource(R.drawable.vim_chat_audio_from);
        } else {
            imgView.setBackgroundResource(R.drawable.vim_chat_audio_to);
        }
        animationDrawable = (AnimationDrawable) imgView.getBackground();
        if (AudioUtils.isPlaying && playingMsgID == msgBean.getMessageID()) {
            animationDrawable.start();
        } else {
            animationDrawable.stop();
            animationDrawable.selectDrawable(0);
        }
    }

    @Override
    protected void onClick() {
        play();
    }


    private void play() {
        from = !RequestHelper.isMyself(msgBean.getSendID());
        if (msgAudio == null) {
            ToastUtil.showShort(context, "播放失败");
            return;
        }
        if (!FileUtils.isExist(msgAudio.getFilePath())) {
            ToastUtil.showShort(context, "播放失败");
        } else {
            if (animationDrawable.isRunning()) {
                animationDrawable.stop();
                playingMsgID = 0;
            }
            String cachePath = ConfigApi.decryptFile(encryptKey, msgAudio.getFilePath());
            AudioUtils.play(context, cachePath, new AudioHandler());
        }
    }


    @Override
    protected void onLongClick() {
        if (RequestHelper.isMyself(msgBean.getSendID()))
            isMe = true;
        CharSequence[] items;
        if (isMe) {
            items = new CharSequence[]{"转发", "收藏", "删除", "更多"};
        } else {
            items = new CharSequence[]{"转发", "收藏", "删除", "撤回", "更多"};
        }
        final MaterialDialog.ListCallback itemOperateCallBack = new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                switch (i) {
                    case 0:
                        itemDataChangeListener.onItemOperation(VimConstant.TYPE_MSG_FORWARD, msgBean);
                        break;
                    case 1:
                        itemDataChangeListener.onItemOperation(VimConstant.TYPE_MSG_COLLECTION, msgBean);
                        break;
                    case 2:
                        itemDataChangeListener.onItemOperation(VimConstant.TYPE_MSG_DELETE, msgBean);
                        break;
                    case 3:
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

    class AudioHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AudioUtils.START:
                    animationDrawable.start();
                    playingMsgID = msgBean.getMessageID();
                    break;
                case AudioUtils.END:
                    if (animationDrawable != null) {
                        animationDrawable.stop();
                        animationDrawable.selectDrawable(0);
                    }
                    playingMsgID = 0;
                    break;
            }
        }
    }
}
