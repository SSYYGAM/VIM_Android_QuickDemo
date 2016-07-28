package com.vrv.sdk.library.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.vrv.sdk.library.R;
import com.vrv.sdk.library.ui.activity.PhotosPreviewActivity;
import com.vrv.sdk.library.utils.ImageUtil;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoPreviewPagerAdapter extends PagerAdapter {

    private final String TAG = PhotoPreviewPagerAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<String> imgPathList;
    private ArrayList<View> viewList;

    public PhotoPreviewPagerAdapter(Context context, ArrayList<String> imgPathList) {
        this.context = context;
        this.imgPathList = imgPathList;
        if (this.imgPathList == null) {
            this.imgPathList = new ArrayList<>();
        }
        viewList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return imgPathList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.vim_view_photo_preview_item, null);
        PhotoView imgView = (PhotoView) view.findViewById(R.id.img_preview);
        ImageUtil.loadViewLocal(context, imgPathList.get(position), imgView);
        viewList.add(position, view);
        container.addView(view, 0);
        imgView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                ((PhotosPreviewActivity)context).finish();
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }
}
