package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.edmodo.cropper.CropImageView;
import com.vrv.imsdk.api.ConfigApi;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.utils.ImageUtil;


/**
 * 图片裁剪
 * Created by Yang on 2015/11/24 024.
 */
public class PhotosCropActivity extends BaseActivity {

    private static final String KEY_PATH = "path";
    private CropImageView cropImageView;
    //    private PhotosCropView cropView;
    private String srcPath;

    public static void start(Activity activity, String path) {
        activity.startActivity(startIntent(activity, path));
    }

    public static void startForResult(Activity activity, String path, int requestCode) {
        activity.startActivityForResult(startIntent(activity, path), requestCode);
    }

    private static Intent startIntent(Activity activity, String path) {
        Intent intent = new Intent();
        intent.setClass(activity, PhotosCropActivity.class);
        intent.putExtra(KEY_PATH, path);
        return intent;
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle(R.string.vim_title_photos_preview);
        srcPath = getIntent().getStringExtra(KEY_PATH);
    }

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context, R.layout.vim_activity_photos_crop, null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        cropImageView = (CropImageView) contentView.findViewById(R.id.cropImageView);
//        cropView = (PhotosCropView) contentView.findViewById(R.id.rl_crop);
    }

    @Override
    protected void setViews() {
        if (TextUtils.isEmpty(srcPath)) {
            return;
        }
//        Glide.with(activity).load(new File(srcPath)).asBitmap().into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
//                cropImageView.setImageBitmap(bitmap);
//            }
//        });
        // FIXME xa zxj 2016/1/6 加载图片前先压缩
        Bitmap mBitmap= BitmapFactory.decodeFile(srcPath);
        int width=context.getResources().getDisplayMetrics().widthPixels;
        float factor = width / (float) mBitmap.getWidth();
        mBitmap = Bitmap.createScaledBitmap(mBitmap, width, (int) (mBitmap.getHeight() * factor), true);
        cropImageView.setImageBitmap(mBitmap);

        //        ImageUtil.loadViewLocal(context, srcPath, cropView.getZoomImageView());
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vim_menu_option, menu);
        menu.findItem(R.id.action_done).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            Bitmap cropBitmap = cropImageView.getCroppedImage();
//            Bitmap cropBitmap = cropView.crop();
            String fileName = ConfigApi.getCachePath() + System.currentTimeMillis() + ".jpg";
            boolean save = ImageUtil.saveBitmapToFile(cropBitmap, fileName);
            if (save) {
                Intent data = new Intent();
                data.putExtra("data", fileName);
                setResult(RESULT_OK, data);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
