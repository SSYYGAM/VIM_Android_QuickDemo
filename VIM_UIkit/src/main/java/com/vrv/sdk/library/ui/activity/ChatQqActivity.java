package com.vrv.sdk.library.ui.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sj.emoji.EmojiBean;
import com.vrv.reclib_vrv.VrvExpressions;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.VimConstant;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.chat.common.ExpressionType;
import com.vrv.sdk.library.chat.common.SimpleCommonUtils;
import com.vrv.sdk.library.chat.qq.QqEmoticonsKeyBoard;
import com.vrv.sdk.library.chat.qq.SimpleQqGridView;
import com.vrv.sdk.library.utils.DialogUtil;

import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.widget.EmoticonsEditText;

/**
 * Created by zxj on 16-7-28.
 */
public class ChatQqActivity extends ChatBaseActivity {
    private QqEmoticonsKeyBoard ekBar;

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context, R.layout.vim_activity_chat_qq, null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        super.findViews();
        ekBar = (QqEmoticonsKeyBoard) contentView.findViewById(R.id.ek_bar);
    }

    @Override
    protected void setViews() {
        super.setViews();
        initEmoticonsKeyBoardBar();
    }

    @Override
    protected void setListener() {
        super.setListener();
        messageListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ekBar.reset();
                return false;
            }
        });
        ekBar.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTxt(ekBar.getEtChat());
            }
        });
        inputEtListener(ekBar.getEtChat(), null, null);
    }


    private void initEmoticonsKeyBoardBar() {
        SimpleCommonUtils utils = new SimpleCommonUtils();
        utils.initEmoticonsEditText(ekBar.getEtChat());
        ekBar.setAdapter(utils.getCommonAdapter(this, emojiCLickListener));
        ekBar.addOnFuncKeyBoardListener(this);
        ekBar.setKeyBoardInterface(new QqEmoticonsKeyBoard.KeyBoardClickListener() {
            @Override
            public void OnKeyBoardClickListener(int position) {
                switch (position) {
                    case QqEmoticonsKeyBoard.FUNC_TYPE_CARD:
                        //                        SelectBuddyActivity.startForResult((ChatQqActivity) context, OptionBean.TYPE_CARD);
                        break;
                    case QqEmoticonsKeyBoard.FUNC_TYPE_BURN:
                        break;
                    case QqEmoticonsKeyBoard.FUNC_TYPE_POSITION:
                        LocationActivity.startForResult((ChatQqActivity) context, VimConstant.TYPE_POSITION);
                        break;
                    case QqEmoticonsKeyBoard.FUNC_TYPE_VOICE:

                        break;
                    case QqEmoticonsKeyBoard.MORE_FORWARD:

                        break;
                    case QqEmoticonsKeyBoard.MORE_DELETE:

                        break;
                }
            }
        });
        SimpleQqGridView simpleQqGridView = new SimpleQqGridView(this);
        ekBar.addFuncView(QqEmoticonsKeyBoard.FUNC_TYPE_PLUG, simpleQqGridView);
        //        ekBar.addFuncView(QqEmoticonsKeyBoard.FUNC_TYPE_IMAGE, new ChatSelectImageView(this));
        //        ekBar.addFuncView(QqEmoticonsKeyBoard.FUNC_TYPE_VOICE, new ChatRecordVoiceView(this));
        //处理更多中的点击事件
        simpleQqGridView.getQqGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    default:
                        break;
                }
            }
        });

        ekBar.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                scrollToBottom();
            }
        });

    }

    EmoticonClickListener emojiCLickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {
            if (isDelBtn) {
                SimpleCommonUtils.delClick(ekBar.getEtChat());
            } else {
                if (o == null) {
                    return;
                }
                String content = null;
                if (o instanceof EmojiBean) {
                    content = ((EmojiBean) o).emoji;
                } else if (o instanceof EmoticonEntity) {
                    content = ((EmoticonEntity) o).getIconUri();
                }
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                if (actionType == ExpressionType.EMOTICON_CLICK_TEXT) {
                    int index = ekBar.getEtChat().getSelectionStart();
                    Editable editable = ekBar.getEtChat().getText();
                    editable.insert(index, content);
                } else if (actionType == ExpressionType.EMOTICON_CLICK_BIGIMAGE) {
                    RequestHelper.sendDynamic(ChatQqActivity.getChatID(), content, requestHandler);
                } else if (actionType == ExpressionType.EMOTICON_CLICK_TASK) {
                    if (content.equals(VrvExpressions.ORDER_DELETE)) {
                        DialogUtil.buildOperateDialog(context, new String[]{"删除对象当天消息", "删除对象所有消息"}, new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                if (which == 0) {
                                    RequestHelper.sendTxt(getChatID(), VrvExpressions.ORDER_DELETE_TODAY, requestHandler);
                                } else if (which == 1) {
                                    RequestHelper.sendTxt(getChatID(), VrvExpressions.ORDER_DELETE_ALL, requestHandler);
                                }
                            }
                        }).show();
                    } else {
                        RequestHelper.sendTxt(getChatID(), content, requestHandler);
                    }
                }
            }
        }
    };

}
