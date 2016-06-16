package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.vrv.imsdk.util.SDKUtils;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.bean.FileBean;
import com.vrv.sdk.library.listener.OnItemClickListener;
import com.vrv.sdk.library.ui.adapter.PhotosThumbAdapter;
import com.vrv.sdk.library.utils.DialogUtil;
import com.vrv.sdk.library.utils.ImageUtil;
import com.vrv.sdk.library.utils.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * 相册缩略图
 * <p/>
 * Created by Yang on 2015/8/25 025.
 */
public class PhotosThumbnailActivity extends BaseActivity {

    private final String TAG = PhotosThumbnailActivity.class.getSimpleName();
    private static final String KEY_MULTI = "multi";

    private boolean isMulti;//是否为多选
    private RecyclerView recyclerView;
    private TextView tvDir;//图片目录
    private TextView tvPages;//张数
    private TextView tvPreview;//预览
    private PhotosThumbAdapter adapter;
    private MenuItem doneMenu;//选中完成菜单

    //所有图片文件夹
    private ArrayList<FileBean> imgFolders = new ArrayList<>();
    //当前预览目录
    private String curDir;
    //当前目录下的所有图片
    private ArrayList<String> photos = new ArrayList<>();
    private ScanHandler handler;

    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, PhotosThumbnailActivity.class);
        activity.startActivity(intent);
    }

    public static void startForResult(Activity activity, int requestCode, boolean isMulti) {
        Intent intent = new Intent();
        intent.setClass(activity, PhotosThumbnailActivity.class);
        intent.putExtra(KEY_MULTI, isMulti);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startForResult(Activity activity, int requestCode) {
        startForResult(activity, requestCode, true);
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle(R.string.vim_photos);
        isMulti = getIntent().getBooleanExtra(KEY_MULTI, true);
    }

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context, R.layout.vim_activity_photos_thumbnail, null);
        contentLayout.addView(contentView);

        handler = new ScanHandler();
        new Thread(new ScanRunnable(handler)).start();
    }

    @Override
    protected void findViews() {
        recyclerView = (RecyclerView) contentView.findViewById(R.id.rc_thumb);
        tvDir = (TextView) contentView.findViewById(R.id.tv_thumb_dir);
        tvPages = (TextView) contentView.findViewById(R.id.tv_thumb_pages);
        tvPreview = (TextView) contentView.findViewById(R.id.tv_thumb_preview);
        if (!isMulti) {
            tvPreview.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListener() {
        tvDir.setOnClickListener(this);
        tvPreview.setOnClickListener(this);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(int position, View view) {
                if (position == 0) {
                    takePhotoPath = ImageUtil.takePic(activity, TAKEPHOTO_CODE);
                } else {
                    if (isMulti) {
                        adapter.updateSelectData(position);
                        doneMenu.setEnabled(adapter.getSelectList().size() > 0);
                    } else {
                        PhotosCropActivity.startForResult(activity, adapter.getItemObject(position), CROP_CODE);
                    }
                }
            }

            @Override
            public boolean OnItemLongClick(int position, View view) {
                return false;
            }
        });
    }

    @Override
    protected void setViews() {
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        adapter = new PhotosThumbAdapter(context, curDir, photos, isMulti);
        recyclerView.setAdapter(adapter);
        tvPages.setText("");
    }

    private void setSelectView(FileBean imgFolder) {
        tvDir.setText(imgFolder.getName());
        tvPages.setText(getString(R.string.vim_photoPages, imgFolder.getCount()));
    }

    //更新adapter中的图片显示
    private void updatePhotos() {
        photos.clear();
//        String files[] = filterFile(new File(curDir));
        File file = new File(curDir);
        if (file.exists()) {
            List<File> newfiles = SortFiles(file);
            for (File file1 : newfiles) {
                String filename = file1.getName();
                if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")) {
                    photos.add(filename);
                }
            }
            //        photos.addAll(Arrays.asList(files));
            adapter.updatePhoto(curDir, photos);
        }
    }

    /**
     * 文件排序 需要自己手动过滤
     *
     * @return
     */
    @NonNull
    private List<File> SortFiles(File dir) {
        List<File> newfiles = Arrays.asList(dir.listFiles());
        Collections.sort(newfiles, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                if (lhs.lastModified() > rhs.lastModified()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        return newfiles;
    }

    //检索遍历图片列表
    private String[] filterFile(File dir) {
        return dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg");
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_thumb_dir) {
            initDirPopWind(v);

        } else if (i == R.id.tv_thumb_preview) {
            if (adapter == null || adapter.getSelectList() == null || adapter.getSelectList().isEmpty()) {
                return;
            }
            PhotosPreviewActivity.startForResult(activity, PREVIEW_CODE, adapter.getSelectList());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vim_menu_option, menu);
        doneMenu = menu.findItem(R.id.action_done);
        if (isMulti) {
            doneMenu.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            if (adapter.getSelectList() == null || adapter.getSelectList().size() <= 0) {
                return false;
            }
            setActivityResult(adapter.getSelectList());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ListPopupWindow dirPop;

    private void initDirPopWind(View v) {
        if (dirPop == null) {
            dirPop = new ListPopupWindow(context);
            dirPop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            dirPop.setHeight(Utils.dip2px(context, 400));
            dirPop.setAdapter(new DirAdapter(context, imgFolders));

            dirPop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dirPop.dismiss();
                    curDir = imgFolders.get(position).getDir();
                    updatePhotos();
                    setSelectView(imgFolders.get(position));
                }
            });
        }
        dirPop.setAnchorView(v);
        dirPop.show();
    }

    @Override
    protected void onStop() {
        if (dirPop != null) {
            dirPop.dismiss();
            dirPop = null;
        }
        super.onStop();
    }

    private final int TAKEPHOTO_CODE = 1;
    private final int PREVIEW_CODE = 2;
    private final int CROP_CODE = 3;
    private String takePhotoPath = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKEPHOTO_CODE && resultCode == RESULT_OK) {
            if (isMulti) {
                ArrayList<String> paths = new ArrayList<>();
                paths.add(takePhotoPath);
                setActivityResult(paths);
            } else {
                PhotosCropActivity.startForResult(activity, takePhotoPath, CROP_CODE);
            }
        } else if (requestCode == PREVIEW_CODE && resultCode == RESULT_OK) {
            setActivityResult(adapter.getSelectList());
        } else if (requestCode == CROP_CODE && resultCode == RESULT_OK) {//裁剪图片返回裁剪路径
            ArrayList<String> paths = new ArrayList<>();
            paths.add(data.getStringExtra("data"));
            setActivityResult(paths);
        }
    }

    //调用setResult,返回之前Activity
    private void setActivityResult(ArrayList<String> paths) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("data", paths);
        setResult(RESULT_OK, intent);
        finish();
    }

    //遍历扫描图片线程
    class ScanRunnable implements Runnable {
        private ScanHandler handler;

        public ScanRunnable(ScanHandler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendEmptyMessage(SHOW_PROGRESS);
            try {
                scanPhotos();
            } catch (Exception e) {
                handler.sendEmptyMessage(ERROR);
            }
        }

        //扫描图片
        private void scanPhotos() throws Exception {
            // 临时的辅助类，用于防止同一个文件夹的多次扫描
            HashSet<String> dirHash = new HashSet<>();
            //扫描 缩略图
            ContentResolver cr = context.getContentResolver();

            String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
            // Add xa zxj 2015/12/23 增加 orderby 按 最后修改时间降序排序
            String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
            Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, orderBy);
            if (cursor == null) {
                //发送扫描失败通知
                handler.sendEmptyMessage(ERROR);
                return;
            }
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                String imgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                // 获取该图片的父路径名
                File parentFile = new File(imgPath).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                String dirPath = parentFile.getAbsolutePath();
                // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                if (dirHash.contains(dirPath)) {
                    continue;
                }
                dirHash.add(dirPath);
                String[] files = filterFile(parentFile);
                if (files == null || files.length == 0) {
                    continue;
                }
                FileBean imgFile = new FileBean();
                imgFile.setDir(dirPath);
//                imgFile.setFirstPath(files[0]);
                imgFile.setFirstPath(new File(imgPath).getName());
                imgFile.setCount(files.length);
//                VrvLog.i(TAG, "---缩略图扫描:" + imgFile.toStrings());
                imgFolders.add(imgFile);
            }
            cursor.close();
            // 扫描完成，辅助的HashSet也就可以释放内存了
            dirHash = null;
            // 通知Handler扫描图片完成
            handler.sendEmptyMessage(FINISH);
        }

        private String selectThumbById(ContentResolver cr, int imgID) {
            String[] projection = {Thumbnails.IMAGE_ID, Thumbnails.DATA};
            String selection = Thumbnails.IMAGE_ID + "=?";
            Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection, selection,
                    new String[]{String.valueOf(imgID)}, null);
            if (cursor == null) {
                return null;
            }
            String imgPath = null;
            try {
                cursor.moveToFirst();
                imgPath = cursor.getString(cursor.getColumnIndex(Thumbnails.DATA));
            } catch (Exception e) {
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
            return imgPath;
        }
    }

    private final int FINISH = 1;
    private final int ERROR = 2;
    private final int SHOW_PROGRESS = 3;
    private MaterialDialog progressDialog;

    class ScanHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            switch (msg.what) {
                case SHOW_PROGRESS:
                    if (progressDialog == null) {
                        progressDialog = DialogUtil.buildProgressDialog(context);
                    }
                    progressDialog.show();
                    break;
                case FINISH:
                    if (imgFolders != null && imgFolders.size() > 0) {
                        curDir = imgFolders.get(0).getDir();
                    }
                    updatePhotos();
                    break;
                case ERROR:
//                    updatePhotos();
                    break;
            }
        }
    }

    class DirAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<FileBean> dirs;
        private final String ROOTPATH = SDKUtils.getSDPath();

        public DirAdapter(Context context, ArrayList<FileBean> dirs) {
            this.context = context;
            this.dirs = dirs;
        }

        @Override
        public int getCount() {
            return dirs.size();
        }

        @Override
        public Object getItem(int position) {
            return dirs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.vim_view_photo_dir_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            FileBean fileBean = dirs.get(position);
            ImageUtil.loadViewLocal(context, fileBean.getDir() + File.separator + fileBean.getFirstPath(), viewHolder.img);
            viewHolder.tvCount.setText(fileBean.getCount() + "");
            if (fileBean.getDir().contains(ROOTPATH)) {
                viewHolder.tvName.setText(fileBean.getDir().replace(ROOTPATH, ""));
            } else {
                viewHolder.tvName.setText(fileBean.getDir());
            }
            if (curDir.equals(fileBean.getDir())) {
                viewHolder.imgStatus.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imgStatus.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        class ViewHolder {
            private ImageView img;
            private TextView tvName;
            private TextView tvCount;
            private ImageView imgStatus;

            public ViewHolder(View itemView) {
                img = (ImageView) itemView.findViewById(R.id.img_item_icon);
                tvName = (TextView) itemView.findViewById(R.id.tv_item_name);
                tvName.setEllipsize(TextUtils.TruncateAt.START);
                tvCount = (TextView) itemView.findViewById(R.id.tv_item_count);
                imgStatus = (ImageView) itemView.findViewById(R.id.img_item_status);
            }
        }
    }
}
