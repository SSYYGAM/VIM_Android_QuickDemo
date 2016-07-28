package com.vrv.sdk.library.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;

import com.vrv.imsdk.api.ConfigApi;

import java.io.File;
import java.util.UUID;

public class AudioUtils {

    private static final String TAG = AudioUtils.class.getSimpleName();

    public static final int START = 2001;
    public static final int END = 2002;
    public static boolean isPlaying = false;

    private static Handler playHandler;
    private static MediaPlayer mediaPlayer;

    private boolean isPrepare;
    private String mDir;
    private String mCurrentFilePath;

    //最大录音时长 1分钟
    private static final int MAX_DURATIONS = 60 * 1000;
    private static MediaRecorder mRecorder = null;
    private static boolean isRecording = false;
    private static long startTime = 0;
    private static String oldAudioUrl = "";

    public static void play(Context context, String audioUrl, Handler handler) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            stopMedia();
            if (!oldAudioUrl.equals(audioUrl)) {
                oldAudioUrl = audioUrl;
                try {
                    startMedia(context, audioUrl, handler);
                } catch (Exception e) {
                    stopMedia();
                }
            }
        } else {
            try {
                startMedia(context, audioUrl, handler);
            } catch (Exception e) {
                stopMedia();
            }
        }
    }

    private static void startMedia(Context context, String url, final Handler handler) throws Exception {
        playHandler = handler;
        Uri mediaUrl = Uri.parse(url);
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, mediaUrl);
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopMedia();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                stopMedia();
                return false;
            }
        });
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
            }
        });
        // 设置是否循环
        mediaPlayer.setLooping(false);
        //        mediaPlayer.prepare();
        mediaPlayer.start();
        sendPlayStatus(START);
        isPlaying = true;
    }

    /**
     * 停止播放
     */
    private static void stopMedia() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            // 重置MediaPlayer到初始状态
            mediaPlayer.release();
        }
        mediaPlayer = null;
        sendPlayStatus(END);
        isPlaying = false;
    }

    private static void sendPlayStatus(int status) {
        if (playHandler != null) {
            playHandler.sendEmptyMessage(status);
        }
    }


    /**
     * 开始录音
     * 设置录音保存路径
     */
    public void prepareAudio() {
        try {
            isPrepare = false;
            File dir = new File(ConfigApi.getCachePath());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = generateFileName();
            File file = new File(dir, fileName);
            mCurrentFilePath = file.getAbsolutePath();
            startRecord(file.getAbsolutePath().toString());
            // 准备结束
            isPrepare = true;
            if (mAudioStateListener != null) {
                mAudioStateListener.wellPrepared();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 开始录音
     *
     * @param path
     * @throws Exception
     */
    private void startRecord(String path) throws Exception {
        mRecorder = new MediaRecorder();
        mRecorder.setMaxDuration(MAX_DURATIONS);
        mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                stopRecord();
            }
        });

        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
            }
        });
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(path);
        mRecorder.prepare();
        mRecorder.start();
        isRecording = true;
        startTime = SystemClock.elapsedRealtime();
    }

    /**
     * 停止录音
     */
    public static long stopRecord() {
        if (isRecording) {
            try {
                mRecorder.stop();
                mRecorder.release();
            } catch (Exception e) {
            } finally {
                mRecorder = null;
                isRecording = false;
            }
            return SystemClock.elapsedRealtime() - startTime;
        } else {
            return 0;
        }
    }

    public static boolean isRecording() {
        return mRecorder != null && isRecording;
    }

    /**
     * 随机生成文件的名称
     */
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int maxlevel) {
        if (isPrepare) {
            try {
                // mMediaRecorder.getMaxAmplitude() 1~32767
                return maxlevel * mRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {
            }
        }
        return 1;
    }

    /**
     * 释放资源
     */
    public void release() {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder = null;
        isRecording = false;
    }

    /**
     * 取消录音
     */
    public void cancel() {
        release();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }

    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    /**
     * 使用接口 用于回调
     */
    public interface AudioStateListener {
        void wellPrepared();
    }

    public AudioStateListener mAudioStateListener;

    /**
     * 回调方法
     */
    public void setOnAudioStateListener(AudioStateListener listener) {
        mAudioStateListener = listener;
    }

    public interface PlayStateListener {
        void start();

        void stop();
    }

    public PlayStateListener mPlayStateListener;

    /**
     * 回调方法
     */
    public void setOnPlayStateListener(PlayStateListener listener) {
        mPlayStateListener = listener;
    }
}
