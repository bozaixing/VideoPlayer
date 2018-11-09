package com.bozaixing.media.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.bozaixing.media.R;
import com.bozaixing.media.widgets.VideoPlayer;

/**
 * Author:  bozaixing
 * Email:   654152983@qq.com
 * Date:    2018/11/9
 * Descr:   实现视频全屏播放的dialog
 */
public class VideoFullDialog extends Dialog implements VideoPlayer.VideoPlayerListener {

    /**
     * TAG
     */
    private static final String TAG = "VideoFullDialog";

    /**
     * UI
     */
    private ViewGroup mParentContainer;
    private VideoPlayer mVideoPlayer;


    /**
     * DATA
     */
    private int mPosition;
    private boolean mIsFirstCreateDialog = true;
    private FullToSmallListener mFullToSmallListener;


    /**
     * 构造方法，初始化数据
     *
     * @param context
     * @param videoPlayer
     * @param position
     */
    public VideoFullDialog(Context context, VideoPlayer videoPlayer, int position) {
        // 通过添加Dialog样式设置全屏
        super(context, R.style.dialog_full_screen);
        mVideoPlayer = videoPlayer;
        mPosition = position;
    }


    /**
     * 创建Dialog时执行的方法
     *
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置透明状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 设置隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 加载布局
        setContentView(R.layout.media_dialog_video_player_layout);
        // 初始化控件
        initWidgets();
    }

    /**
     * 初始化控件
     */
    private void initWidgets(){
        mParentContainer = findViewById(R.id.container_layout);
        // 为视频播放器设置监听
        mVideoPlayer.setVideoPlayerListener(this);
        // 添加到父容器中
        mParentContainer.addView(mVideoPlayer);
        // 为父容器添加视图树将要绘制时的监听
        mParentContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            /**
             * 视图树准备绘制时的回调方法
             *
             * @return
             */
            @Override
            public boolean onPreDraw() {
                // 第一步先移除监听
                mParentContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                // 准备动画需要的数据
                return false;
            }
        });
    }


    /**
     * 准备动画
     */
    private void prepareScene(){



    }


    @Override
    public void onBufferingUpdate(int time) {

    }

    @Override
    public void onVideoLoadSuccess() {

    }

    @Override
    public void onVideoLoadFailed() {

    }

    @Override
    public void onVideoLoadComplete() {

    }

    @Override
    public void onClickVideo() {

    }

    @Override
    public void onClickFullScreen() {

    }


    /**
     * 点击返回时的回调方法
     */
    @Override
    public void onClickBack() {
        // 第一步关闭当前的dialog
        dismiss();
        // 第二步通知逻辑层当前的播放位置
        if (mFullToSmallListener != null){
            if (mVideoPlayer != null){
                mFullToSmallListener.getCurrentPlayPosition(mVideoPlayer.getCurrentPosition());
            }
        }
    }


    /**
     * 用户点击手机的物理返回按键的回调方法
     */
    @Override
    public void onBackPressed() {
        // 执行返回的方法
        onClickBack();
        super.onBackPressed();
    }


    /**
     * dialog焦点状态发生改变时的回调方法
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus){
            // 未取得焦点，获取当前的播放位置并暂停播放
            if (mVideoPlayer != null){
                mPosition = mVideoPlayer.getCurrentPosition();
                mVideoPlayer.pause();
            }
        }else {
            // 首次创建dialog，首次取得焦点
            if (mIsFirstCreateDialog){
                // 如果是第一次创建
                if (mVideoPlayer != null){
                    mVideoPlayer.seekAndResume(mPosition);
                }
            }else {
                // 取得焦点时直接恢复播放
                if (mVideoPlayer != null){
                    mVideoPlayer.resume();
                }
            }
        }
        // 改变标识，保证只执行一次
        mIsFirstCreateDialog = false;
    }


    /**
     * dialog销毁时执行的方法
     */
    @Override
    public void dismiss() {
        // 从父容器中移除播放器控件
        mParentContainer.removeView(mVideoPlayer);
        super.dismiss();
    }




    /**
     * 初始化接口监听对象
     *
     * @param listener
     */
    public void setFullToSmallListener(FullToSmallListener listener){
        mFullToSmallListener = listener;
    }


    /**
     * 全屏切换到小屏播放的监听接口
     */
    public interface FullToSmallListener {

        /**
         * 全屏播放中点击返回按钮的回调
         */
        void getCurrentPlayPosition(int position);


        /**
         * 全屏模式是播放完成的回调
         */
        void onPlayComplete();
    }
}
