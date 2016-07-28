package com.vrv.sdk.library.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vrv.imsdk.SDKClient;
import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.model.Contact;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.bean.BaseInfoBean;
import com.vrv.sdk.library.utils.Utils;
import com.vrv.sdk.library.utils.VrvLog;

public class ContactDetailActivity extends BaseActivity {

    private final String TAG = ContactDetailActivity.class.getSimpleName();
    private ImageView imgIcon;//头像
    private TextView txAccount;//账户昵称
    private TextView txID;//id
    private TextView tvPhone;
    private TextView tvMail;
    private TextView tvSign;
    private TextView tvSendMsg;//发消息
    private TextView tvAdd;//添加为好友
    private LinearLayout llAdd;

    private long userID;
    private Contact contact;

    public static void start(Context context, long userID) {
        Intent intent = new Intent();
        intent.setClass(context, ContactDetailActivity.class);
        intent.putExtra("userID", userID);
        context.startActivity(intent);
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle(R.string.vim_title_contact_detail);
        userID = getIntent().getLongExtra("userID", 0);
    }

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context, R.layout.vim_activity_contact_detail, null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        imgIcon = (ImageView) findViewById(R.id.img_item_icon);
        txAccount = (TextView) findViewById(R.id.tv_item_name);
        txID = (TextView) findViewById(R.id.tv_item_content);
        tvSendMsg = (TextView) findViewById(R.id.tv_personal_sendMsg);
        tvAdd = (TextView) findViewById(R.id.tv_personal_addBuddy);
        tvPhone = (TextView) findViewById(R.id.tv_personal_phone);
        tvMail = (TextView) findViewById(R.id.tv_personal_mail);
        tvSign = (TextView) findViewById(R.id.tv_personal_sign);
        llAdd = (LinearLayout) findViewById(R.id.ll_personal_add);
    }

    @Override
    protected void setViews() {
        if (RequestHelper.isMyself(userID)) {
            contact = RequestHelper.getMyInfo();
            setViewData(contact);
        } else if (ChatMsgApi.isUser(userID)) {
            llAdd.setVisibility(View.VISIBLE);
            requestHandler = new GetInfoHandler(GetInfoHandler.GET_USER, context);
            requestHandler.sendEmptyMessage(RequestHandler.SHOW_PRO);
            boolean get = RequestHelper.getUserInfo(userID, requestHandler);
            if (!get) {
                requestHandler.sendEmptyMessage(RequestHandler.REQUEST_FALSE);
            }
        } else {
            contact = SDKClient.instance().getContactService().findItemByID(userID);
            setViewData(contact);
        }
    }

    private void setViewData(Contact contact) {
        if (contact == null) {
            return;
        }
        VrvLog.i(TAG, "好友信息：" + contact.toString());
        Utils.loadHead(context, contact.getAvatar(), imgIcon, R.mipmap.vim_icon_default_user);
        txAccount.setText(contact.getName());
        txID.setText(contact.getNickID());
        if (contact.getPhones() != null && contact.getPhones().size() > 0) {
            tvPhone.setText(contact.getPhones().get(0));
        }

        if (contact.getEmails() != null && contact.getEmails().size() > 0) {
            tvMail.setText(contact.getEmails().get(0));
        }
        tvSign.setText(contact.getSign());
        //设置添加好友按钮是否显示
        if (SDKClient.instance().getContactService().isFriend(userID)) {
            llAdd.setVisibility(View.INVISIBLE);
        } else {
            llAdd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setListener() {
        tvSendMsg.setOnClickListener(this);
        tvAdd.setOnClickListener(this);
        imgIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_personal_sendMsg) {
            if (contact == null) {
                contact = new Contact();
                contact.setId(userID);
            }
            ChatActivity.start(context, BaseInfoBean.contact2BaseInfo(contact));
        }
    }

    class GetInfoHandler extends RequestHandler {

        public static final int GET_USER = 2;
        private int type;

        public GetInfoHandler(int type, Context context) {
            super(context);
            this.type = type;
        }

        @Override
        public void handleSuccess(Message msg) {
            if (type == GET_USER) {
                VrvLog.i(TAG, "陌生人信息成功");
                contact = msg.getData().getParcelable(KEY_DATA);
                setViewData(contact);
            }
        }
    }
}
