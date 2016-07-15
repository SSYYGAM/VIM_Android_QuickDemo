package com.vrv.sdk.library.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.vrv.sdk.library.R;
import com.vrv.sdk.library.ui.activity.fragment.ContactFragment;
import com.vrv.sdk.library.ui.activity.fragment.ConversationFragment;

public class MainActivity extends BaseActivity {

    private ViewPager viewPager;
    private RadioGroup radioGroup;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ConversationFragment conversationFragment;
    private ContactFragment contactFragment;

    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle(R.string.vim_conversation);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void loadContentLayout() {
        //不显示返回按钮需要在setToolBar方法完成之后添加
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        contentView = View.inflate(context, R.layout.vim_activity_main, null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        radioGroup = (RadioGroup) contentView.findViewById(R.id.radioGroup);
    }

    @Override
    protected void setViews() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mSectionsPagerAdapter);
        radioGroup.check(R.id.rb_conversation);
        viewPager.setCurrentItem(0);
    }

    @Override
    protected void setListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_contact) {
                    viewPager.setCurrentItem(1);
                } else if (checkedId == R.id.rb_conversation) {
                    viewPager.setCurrentItem(0);
                }
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    radioGroup.check(R.id.rb_conversation);
                    toolbar.setTitle(R.string.vim_conversation);
                } else if (position == 1) {
                    radioGroup.check(R.id.rb_contact);
                    toolbar.setTitle(R.string.vim_contact);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vim_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            SearchActivity.start(activity);
            return true;
        }
        return false;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (conversationFragment == null) {
                        conversationFragment = new ConversationFragment();
                    }
                    return conversationFragment;
                case 1:
                    if (contactFragment == null) {
                        contactFragment = new ContactFragment();
                    }
                    return contactFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }
}
