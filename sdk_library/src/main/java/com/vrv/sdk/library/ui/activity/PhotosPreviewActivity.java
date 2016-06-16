package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vrv.sdk.library.R;
import com.vrv.sdk.library.ui.adapter.PhotoPreviewPagerAdapter;

import java.util.ArrayList;

/**
 * 图片预览
 */
public class PhotosPreviewActivity extends BaseActivity {

    private ViewPager viewPager;
    private PhotoPreviewPagerAdapter adapter;
    private ArrayList<String> pathList;

    public static void start(Activity activity, ArrayList<String> pathList) {
        activity.startActivity(startIntent(activity, pathList));
    }

    public static Intent startIntent(Context context,ArrayList<String> pathList){
        Intent intent = new Intent();
        intent.setClass(context, PhotosPreviewActivity.class);
        intent.putStringArrayListExtra("pathList", pathList);
        return intent;
    }

    public static void startForResult(Activity activity, int requestCode,ArrayList<String> pathList) {
        activity.startActivityForResult(startIntent(activity, pathList), requestCode);
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle(R.string.vim_title_photos_preview);
        pathList = getIntent().getStringArrayListExtra("pathList");
    }

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context, R.layout.vim_activity_photo_preview, null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        viewPager = (ViewPager) contentView.findViewById(R.id.pager);
        contentLayout.findViewById(R.id.ll_pager_index).setVisibility(View.GONE);
    }

    @Override
    protected void setViews() {
        adapter = new PhotoPreviewPagerAdapter(context, pathList);
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vim_menu_option,menu);
        menu.findItem(R.id.action_done).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_done){
            setResult(RESULT_OK);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
