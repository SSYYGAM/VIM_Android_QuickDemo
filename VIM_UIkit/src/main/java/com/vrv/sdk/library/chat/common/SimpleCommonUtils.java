package com.vrv.sdk.library.chat.common;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sj.emoji.DefEmoticons;
import com.sj.emoji.EmojiBean;
import com.vrv.reclib_vrv.VrvExpressions;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.chat.common.adapter.emoticonadapter.BigEmoticonsAdapter;
import com.vrv.sdk.library.chat.common.adapter.emoticonadapter.BigEmoticonsAndTitleAdapter;
import com.vrv.sdk.library.chat.common.filter.EmojiFilter;
import com.vrv.sdk.library.chat.common.filter.QqFilter;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;

import sj.keyboard.adpater.EmoticonsAdapter;
import sj.keyboard.adpater.PageSetAdapter;
import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.data.EmoticonPageEntity;
import sj.keyboard.data.EmoticonPageSetEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.interfaces.EmoticonDisplayListener;
import sj.keyboard.interfaces.PageViewInstantiateListener;
import sj.keyboard.widget.EmoticonPageView;
import sj.keyboard.widget.EmoticonsEditText;

public class SimpleCommonUtils {

    public void initEmoticonsEditText(EmoticonsEditText etContent) {
        etContent.addEmoticonFilter(new EmojiFilter());
        etContent.addEmoticonFilter(new QqFilter());
    }

    public EmoticonClickListener getCommonEmoticonClickListener(final EditText editText) {
        return new EmoticonClickListener() {
            @Override
            public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {
                if (isDelBtn) {
                    SimpleCommonUtils.delClick(editText);
                } else {
                    if (o == null) {
                        return;
                    }
                    if (actionType == ExpressionType.EMOTICON_CLICK_TEXT) {
                        String content = null;
                        if (o instanceof EmojiBean) {
                            content = ((EmojiBean) o).emoji;
                        } else if (o instanceof EmoticonEntity) {
                            content = ((EmoticonEntity) o).getContent();
                        }

                        if (TextUtils.isEmpty(content)) {
                            return;
                        }
                        int index = editText.getSelectionStart();
                        Editable editable = editText.getText();
                        editable.insert(index, content);
                    } else {

                    }
                }
            }
        };
    }

    public PageSetAdapter sCommonPageSetAdapter;

    public PageSetAdapter getCommonAdapter(Context context, EmoticonClickListener emoticonClickListener) {

        if (sCommonPageSetAdapter != null) {
            return sCommonPageSetAdapter;
        }
        PageSetAdapter sCommonPageSetAdapter = new PageSetAdapter();

        addEmojiPageSetEntity(sCommonPageSetAdapter, context, emoticonClickListener);

        addCustomPageSetEntity(sCommonPageSetAdapter, context, emoticonClickListener);

        addDynamicPageSetEntity(sCommonPageSetAdapter, context, emoticonClickListener);
        addInstructionPageSetEntity(sCommonPageSetAdapter, context, emoticonClickListener);

        return sCommonPageSetAdapter;
    }

    /**
     * 插入emoji表情集
     *
     * @param pageSetAdapter
     * @param context
     * @param emoticonClickListener
     */
    public void addEmojiPageSetEntity(PageSetAdapter pageSetAdapter, Context context, final EmoticonClickListener emoticonClickListener) {
        ArrayList<EmojiBean> emojiArray = new ArrayList<>();
        Collections.addAll(emojiArray, DefEmoticons.getDefEmojiArray());
        EmoticonPageSetEntity emojiPageSetEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(3)
                .setRow(7)
                .setEmoticonList(emojiArray)
                .setIPageViewInstantiateItem(getDefaultEmoticonPageViewInstantiateItem(new EmoticonDisplayListener<Object>() {
                    @Override
                    public void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, Object object, final boolean isDelBtn) {
                        final EmojiBean emojiBean = (EmojiBean) object;
                        if (emojiBean == null && !isDelBtn) {
                            return;
                        }
                        viewHolder.ly_root.setBackgroundResource(R.drawable.bg_emoticon);
                        if (isDelBtn) {
                            viewHolder.iv_emoticon.setImageResource(R.mipmap.icon_del);
                        } else {
                            viewHolder.iv_emoticon.setImageResource(emojiBean.icon);
                        }
                        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (emoticonClickListener != null) {
                                    emoticonClickListener.onEmoticonClick(emojiBean, ExpressionType.EMOTICON_CLICK_TEXT, isDelBtn);
                                }
                            }
                        });
                    }
                }))
                .setShowDelBtn(EmoticonPageEntity.DelBtnStatus.LAST)
                .setIconUri(emojiArray.get(0).icon)
                .build();
        pageSetAdapter.add(emojiPageSetEntity);
    }


    /**
     * 插入自定义表情集
     *
     * @param pageSetAdapter
     * @param context
     * @param emoticonClickListener
     */
    public void addCustomPageSetEntity(PageSetAdapter pageSetAdapter, Context context, EmoticonClickListener emoticonClickListener) {
        EmoticonPageSetEntity cunstomEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(3)
                .setRow(7)
                .setEmoticonList(VrvExpressions.getCustomList())
                .setIPageViewInstantiateItem(getDefaultEmoticonPageViewInstantiateItem(getCommonEmoticonDisplayListener(emoticonClickListener, ExpressionType.EMOTICON_CLICK_TEXT)))
                .setShowDelBtn(EmoticonPageEntity.DelBtnStatus.LAST)
                .setIconUri(VrvExpressions.getCustomList().get(0).icon)
                .build();
        pageSetAdapter.add(cunstomEntity);
    }

    public void addDynamicPageSetEntity(PageSetAdapter pageSetAdapter, Context context, final EmoticonClickListener emoticonClickListener) {
        EmoticonPageSetEntity dynamicEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(2)
                .setRow(4)
                .setEmoticonList(VrvExpressions.getDynamicList())
                .setIPageViewInstantiateItem(getEmoticonPageViewInstantiateItem(BigEmoticonsAdapter.class, emoticonClickListener))
                .setIconUri(VrvExpressions.getDynamicList().get(0).icon)
                .build();
        pageSetAdapter.add(dynamicEntity);
    }

    public void addInstructionPageSetEntity(PageSetAdapter pageSetAdapter, Context context, final EmoticonClickListener emoticonClickListener) {
        EmoticonPageSetEntity instructionEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(3)
                .setRow(4)
                .setEmoticonList(VrvExpressions.getInstructionList())
                .setIPageViewInstantiateItem(getEmoticonPageViewInstantiateItem(BigEmoticonsAndTitleAdapter.class, emoticonClickListener))
                .setIconUri((int) VrvExpressions.getInstructionList().get(0).getEventType())
                .build();
        pageSetAdapter.add(instructionEntity);
    }


    @SuppressWarnings("unchecked")
    public static Object newInstance(Class _Class, Object... args) throws Exception {
        return newInstance(_Class, 0, args);
    }

    @SuppressWarnings("unchecked")
    public static Object newInstance(Class _Class, int constructorIndex, Object... args) throws Exception {
        Constructor cons = _Class.getConstructors()[constructorIndex];
        return cons.newInstance(args);
    }

    public static PageViewInstantiateListener<EmoticonPageEntity> getDefaultEmoticonPageViewInstantiateItem(final EmoticonDisplayListener<Object> emoticonDisplayListener) {
        return getEmoticonPageViewInstantiateItem(EmoticonsAdapter.class, null, emoticonDisplayListener);
    }

    public static PageViewInstantiateListener<EmoticonPageEntity> getEmoticonPageViewInstantiateItem(final Class _class, EmoticonClickListener onEmoticonClickListener) {
        return getEmoticonPageViewInstantiateItem(_class, onEmoticonClickListener, null);
    }

    public static PageViewInstantiateListener<EmoticonPageEntity> getEmoticonPageViewInstantiateItem(final Class _class, final EmoticonClickListener onEmoticonClickListener, final EmoticonDisplayListener<Object> emoticonDisplayListener) {
        return new PageViewInstantiateListener<EmoticonPageEntity>() {
            @Override
            public View instantiateItem(ViewGroup container, int position, EmoticonPageEntity pageEntity) {
                if (pageEntity.getRootView() == null) {
                    EmoticonPageView pageView = new EmoticonPageView(container.getContext());
                    pageView.setNumColumns(pageEntity.getRow());
                    pageEntity.setRootView(pageView);
                    try {
                        EmoticonsAdapter adapter = (EmoticonsAdapter) newInstance(_class, container.getContext(), pageEntity, onEmoticonClickListener);
                        if (emoticonDisplayListener != null) {
                            adapter.setOnDisPlayListener(emoticonDisplayListener);
                        }
                        pageView.getEmoticonsGridView().setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return pageEntity.getRootView();
            }
        };
    }

    public static EmoticonDisplayListener<Object> getCommonEmoticonDisplayListener(final EmoticonClickListener onEmoticonClickListener, final int type) {
        return new EmoticonDisplayListener<Object>() {
            @Override
            public void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, Object object, final boolean isDelBtn) {

                final EmojiBean emoji = (EmojiBean) object;
                if (emoji == null && !isDelBtn) {
                    return;
                }
                viewHolder.ly_root.setBackgroundResource(R.drawable.bg_emoticon);
                viewHolder.iv_emoticon.setScaleType(ImageView.ScaleType.FIT_CENTER);
                if (isDelBtn) {
                    viewHolder.iv_emoticon.setImageResource(R.mipmap.icon_del);
                } else {
                    Glide.with(viewHolder.iv_emoticon.getContext()).load(emoji.icon).into(viewHolder.iv_emoticon);
                }

                viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onEmoticonClickListener != null) {
                            onEmoticonClickListener.onEmoticonClick(emoji, type, isDelBtn);
                        }
                    }
                });
            }
        };
    }


    public static void delClick(EditText editText) {
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        editText.onKeyDown(KeyEvent.KEYCODE_DEL, event);
    }

    public static void spannableEmoticonFilter(TextView tv_content, String content) {
        //        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
        //
        //        Spannable spannable = EmojiDisplay.spannableFilter(tv_content.getContext(),
        //                spannableStringBuilder,
        //                content,
        //                EmoticonsKeyboardUtils.getFontHeight(tv_content));
        //
        //        spannable = XhsFilter.spannableFilter(tv_content.getContext(),
        //                spannable,
        //                content,
        //                EmoticonsKeyboardUtils.getFontHeight(tv_content),
        //                null);
        //        tv_content.setText(spannable);
    }

}
