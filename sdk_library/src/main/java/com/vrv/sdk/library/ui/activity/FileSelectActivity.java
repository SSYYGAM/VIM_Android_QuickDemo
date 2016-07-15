package com.vrv.sdk.library.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.vrv.imsdk.util.SDKUtils;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.bean.FileBean;
import com.vrv.sdk.library.listener.OnItemClickListener;
import com.vrv.sdk.library.ui.adapter.FileSelectAdapter;
import com.vrv.sdk.library.utils.FileUtils;
import com.vrv.sdk.library.utils.ToastUtil;
import com.vrv.sdk.library.utils.Utils;
import com.vrv.sdk.library.utils.VrvLog;

import java.io.File;
import java.util.ArrayList;

/**
 * 文件选择
 * Created by Yang on 2015/10/22 022.
 */
public class FileSelectActivity extends BaseActivity {

    private final String TAG = FileSelectActivity.class.getSimpleName();
    private ArrayList<FileBean> fileBeans = new ArrayList<>();
    private final String ROOTPATH = SDKUtils.getSDPath();

    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, FileSelectActivity.class);
        activity.startActivity(intent);
    }

    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, FileSelectActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    private RecyclerView recyclerView;
    private FileSelectAdapter adapter;

    @Override
    protected void setToolBar() {
        toolbar.setTitle("文件目录");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void loadContentLayout() {
        contentView = View.inflate(context, R.layout.vim_view_recycler, null);
        contentLayout.addView(contentView);
    }

    @Override
    protected void findViews() {
        recyclerView = (RecyclerView) contentView.findViewById(R.id.recycler);
    }

    @Override
    protected void setViews() {
        if (adapter == null) {
            adapter = new FileSelectAdapter(context, fileBeans);
        }
        recyclerView.addItemDecoration(Utils.buildDividerItemDecoration(context));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        refreshFileList(ROOTPATH);
    }

    @Override
    protected void setListener() {
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(int position, View view) {
                FileBean fileBean = adapter.getItemObject(position);
                if (fileBean == null)
                    return;
                switch (fileBean.getType()) {
                    case ROOT:
                        ToastUtil.showShort(context, "没有上一层了");
                        break;
                    case PARENT:
                        refreshFileList(fileBean.getPath());
                        break;
                    case DIR:
                        refreshFileList(fileBean.getPath());
                        break;
                    case FILE:
                        Intent data = new Intent();
                        data.putExtra("data", fileBean);
                        setResult(RESULT_OK, data);
                        finish();
                        break;
                }
            }

            @Override
            public boolean OnItemLongClick(int position, View view) {
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        FileBean parent = adapter.getItemObject(0);
        if (parent == null || parent.getType() == FileBean.FileType.ROOT) {
            finish();
        } else {
            refreshFileList(parent.getPath());
        }
    }

    // 刷新文件列表
    private void refreshFileList(String currentPath) {
        VrvLog.i(TAG, "当前目录：" + currentPath);
        if (TextUtils.isEmpty(currentPath)) {
            currentPath = ROOTPATH;
        }
        try {
            File curFile = FileUtils.openOrCreateFile(currentPath);
            if (curFile == null) {
                VrvLog.e(TAG, "refreshFile1");
                return;
            }
            File[] files = curFile.listFiles();
            if (files == null) {
                VrvLog.e(TAG, "refreshFile2");
                return;
            }
            if (fileBeans == null) {
                fileBeans = new ArrayList<>();
            }
            fileBeans.clear();
            FileBean topFileData = new FileBean(); // 根目录
            if (currentPath.equals(ROOTPATH) || ROOTPATH.replace(currentPath, "").equals(File.separator)) {
                topFileData.setName("根目录");
                topFileData.setPath(currentPath);
                topFileData.setType(FileBean.FileType.ROOT);
            } else {// 加返回上层目录项
                topFileData.setName("返回上一层");
                topFileData.setPath(curFile.getParent());
                topFileData.setType(FileBean.FileType.PARENT);
            }
            fileBeans.add(topFileData);

            for (File file : files) {
                if (file.isDirectory() && file.listFiles() != null) {
                    // 添加文件夹
                    FileBean folder = new FileBean();
                    folder.setName(file.getName());
                    folder.setPath(file.getPath());
                    folder.setTime(FileBean.formatTime(context, file.lastModified()));
                    folder.setType(FileBean.FileType.DIR);
                    fileBeans.add(folder);
                } else if (file.isFile()) {
                    // 添加文件
                    FileBean fileData = new FileBean();
                    fileData.setName(file.getName());
                    fileData.setPath(file.getAbsolutePath());
                    fileData.setSize(FileBean.changeBite(file.length()));
                    fileData.setTime(FileBean.formatTime(context, file.lastModified()));
                    fileData.setType(FileBean.FileType.FILE);
                    fileBeans.add(fileData);
                }
            }
        } catch (Exception e) {
            VrvLog.e(TAG, "refreshFile:" + e.toString());
        }
        adapter.notifyDataSetChanged();
    }
}
