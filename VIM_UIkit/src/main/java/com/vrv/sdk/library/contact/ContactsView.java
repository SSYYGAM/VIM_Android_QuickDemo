package com.vrv.sdk.library.contact;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.vrv.imsdk.model.Contact;
import com.vrv.sdk.library.common.view.BaseRecyclerView;
import com.vrv.sdk.library.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 通讯录列表
 */
public class ContactsView extends BaseRecyclerView {
    private List<Contact> contacts;

    public ContactsView(Context context) {
        super(context);
        init();
    }

    public ContactsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ContactsView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        addItemDecoration(Utils.buildDividerItemDecoration(context));
        contacts = new ArrayList<>();
        adapter = new ContactsAdapter(context, contacts);
        setAdapter(adapter);
        setLayoutManager(new LinearLayoutManager(context));
    }

    public List<Contact> getData(){
        return this.contacts;
    }
}
