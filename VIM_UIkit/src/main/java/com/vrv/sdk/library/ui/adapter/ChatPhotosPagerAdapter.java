package com.vrv.sdk.library.ui.adapter;

import android.content.Context;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.api.MsgImage;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.imsdk.util.SDKUtils;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.action.RequestHandler;
import com.vrv.sdk.library.action.RequestHelper;
import com.vrv.sdk.library.ui.activity.ChatPhotosActivity;
import com.vrv.sdk.library.utils.FileUtils;
import com.vrv.sdk.library.utils.ImageUtil;

import java.util.ArrayList;
import java.util.HashMap;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ChatPhotosPagerAdapter extends PagerAdapter {

    private final String TAG = ChatPhotosPagerAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<ChatMsg> imgPathList;
    private HashMap<Integer, View> viewMap;
    //文件名保存下载对应位置，更新
    private HashMap<String, Integer> downloadMap = new HashMap<>();

    public ChatPhotosPagerAdapter(Context context, ArrayList<ChatMsg> imgPathList) {
        this.context = context;
        this.imgPathList = imgPathList;
        if (this.imgPathList == null) {
            this.imgPathList = new ArrayList<>();
        }
        viewMap = new HashMap<>();
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
        if (!viewMap.containsKey(position)) {
            viewMap.put(position, view);
        }
        handleView(position);
        container.addView(view, 0);
        return view;
    }

    private void handleView(int position) {
        if (viewMap == null || !viewMap.containsKey(position)) {
            return;
        }
        View view = viewMap.get(position);
//        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.img_proBar);
        PhotoView imageView = (PhotoView) view.findViewById(R.id.img_preview);
        imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                ((ChatPhotosActivity) context).finish();
            }
        });
        displayOrDownload(position, imgPathList.get(position), imageView);
    }

    private void displayOrDownload(int position, ChatMsg messageBean, PhotoView imageView) {
        MsgImage msgImage = ChatMsgApi.parseImgJson(messageBean.getMessage());
        if (msgImage == null) {
            imageView.setImageResource(R.mipmap.vim_load_pic_lost);
            return;
        }
        String thumbPath = msgImage.getThumbShowPath();
        String orgPath = msgImage.getOrgShowPath();
        String encryptKey = msgImage.getEncDecKey();
        if (TextUtils.isEmpty(orgPath)) {
            displayThumb(thumbPath, imageView, encryptKey);
            return;
        }
        if (FileUtils.isExist(orgPath)) {
            ImageUtil.loadViewLocalWithEncrypt(context, encryptKey, orgPath, imageView, R.mipmap.vim_load_pic_lost);
        } else {
            displayThumb(thumbPath, imageView, encryptKey);
            String fileName = SDKUtils.getFileNameByPath(orgPath);
            if (!downloadMap.containsKey(fileName)) {//不存在也没有下载
                downloadMap.put(fileName, position);
                DownLoadHandler handler = new DownLoadHandler();
                boolean down = RequestHelper.downloadOrgImg(messageBean, new DownLoadHandler());
                if (!down) {
                    handler.sendEmptyMessage(RequestHandler.REQUEST_FALSE);
                }
            }
        }
    }

    private void displayThumb(String thumbPath, ImageView imageView, String encryptKey) {
        if (TextUtils.isEmpty(thumbPath)) {
            imageView.setImageResource(R.mipmap.vim_load_pic_lost);
        } else {
            ImageUtil.loadViewLocalWithEncrypt(context, encryptKey, thumbPath, imageView, R.mipmap.vim_load_pic_lost);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (viewMap.containsKey(position)) {
            container.removeView(viewMap.get(position));
            viewMap.remove(position);
        }
    }

    class DownLoadHandler extends RequestHandler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }

        @Override
        public void handleSuccess(Message msg) {
            try {//关闭之后可能崩溃
                String filePath = msg.getData().getString(KEY_DATA);
                String fileName = SDKUtils.getFileNameByPath(filePath);
                if (downloadMap.containsKey(fileName)) {
                    handleView(downloadMap.get(fileName));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
