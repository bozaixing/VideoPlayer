package com.bozaixing.media.core;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bozaixing.media.constant.VideoConstant;
import com.bozaixing.media.dialog.VideoFullDialog;
import com.bozaixing.media.util.VideoUtil;
import com.bozaixing.media.widgets.VideoPlayer;

/**
 * Author:  bozaixing
 * Email:   654152983@qq.com
 * Date:    2018/11/5
 * Descr:   视频业务逻辑的处理类
 */
public class VideoSlot implements VideoPlayer.VideoPlayerListener {

    /**
     * 定义上下文对象
     */
    private Context mContext;


    /**
     * UI
     */
    private VideoPlayer mVideoPlayer;
    // 视频播放器控件VideoPlayer需要添加到的父容器
    private ViewGroup mParentContainer;


    /**
     * DATA
     */
    private String mVideoUrl;


    /**
     * 构造方法，初始化参数
     *
     * @param videoUrl
     * @param parentContainer
     */
    public VideoSlot(String videoUrl, ViewGroup parentContainer){
        mVideoUrl = videoUrl;
        mParentContainer = parentContainer;
        mContext = mParentContainer.getContext();
        // 初始化视频播放器对象
        initVideoPlayer();
    }


    /**
     * 初始化视频播放器控件
     */
    private void initVideoPlayer(){
        mVideoPlayer = new VideoPlayer(mContext);
        if (!TextUtils.isEmpty(mVideoUrl)){
            mVideoPlayer.setDataSource(mVideoUrl);
            mVideoPlayer.setVideoPlayerListener(this);
        }
        // 将播放器控件添加到父容器中
        mParentContainer.addView(mVideoPlayer);
    }

    @Override
    public void onClickVideo() {

    }


    /**
     * 视频全屏播放的方法
     */
    @Override
    public void onClickFullScreen() {
        // 获取属性值
        Bundle bundle = VideoUtil.getViewProperty(mParentContainer);
        // 第一步将视频播放器控件从父容器中移除
        mParentContainer.removeView(mVideoPlayer);
        // 第二步创建dialog
        VideoFullDialog videoFullDialog = new VideoFullDialog(mContext, mVideoPlayer, mVideoPlayer.getCurrentPosition());
        videoFullDialog.setFullToSmallListener(new VideoFullDialog.FullToSmallListener() {

            /**
             * 全屏播放时点击返回按钮回调的方法
             * @param position
             */
            @Override
            public void getCurrentPlayPosition(int position) {
                // 调用全屏模式返回小屏模式的方法
                backToSmallMode(position);
            }

            @Override
            public void onPlayComplete() {

            }
        });
        // 第三步显示dialog
        videoFullDialog.show();
    }


    /**
     * 从全屏模式返回小屏模式的方法
     *
     * @param position
     */
    private void backToSmallMode(int position){
        // 如果当前播放器控件不为空
        if (mVideoPlayer != null){
            // 如果当前播放器没有父容器
            if (mVideoPlayer.getParent() == null){
                // 将播放器控件添加到父容器中
                mParentContainer.addView(mVideoPlayer);
            }
            // 重新设置监听
            mVideoPlayer.setVideoPlayerListener(this);
            // 跳转到指定位置播放
            mVideoPlayer.seekAndResume(position);
        }
    }


    /**
     * 全屏播放完成返回执行的逻辑
     */
    private void fullScreenPlayComplete(){
        if (mVideoPlayer != null){
            if (mVideoPlayer.getParent() == null){
                mParentContainer.addView(mVideoPlayer);
            }
            // 重新设置监听
            mVideoPlayer.setVideoPlayerListener(this);
            mVideoPlayer.seekAndPause(0);
        }
    }



    @Override
    public void onClickBack() {

    }



    @Override
    public void onBufferingUpdate(int time) {

    }

    /**
     * 视频加载成功的回调方法
     */
    @Override
    public void onVideoLoadSuccess() {
    }


    /**
     * 视频加载失败的回调方法
     */
    @Override
    public void onVideoLoadFailed() {

    }


    /**
     * 视频播放完成的回调方法
     */
    @Override
    public void onVideoLoadComplete() {

    }


    /**
     * 返回视频当前的播放位置
     *
     * @return
     */
    public int getCurrentPosition(){
        if (mVideoPlayer != null){
            return mVideoPlayer.getCurrentPosition() / VideoConstant.MILLION_UNIT;
        }
        return 0;
    }


    /**
     * 返回当前播放视频的总时长
     *
     * @return
     */
    public int getDuration(){
        if (mVideoPlayer != null){
            return mVideoPlayer.getDuration() / VideoConstant.MILLION_UNIT;
        }
        return 0;
    }


    /**
     * 暂停播放
     */
    public void pause(){
        if (mVideoPlayer != null){
            mVideoPlayer.pause();
        }
    }


    /**
     * 恢复视频的播放
     */
    public void resume(){
        if (mVideoPlayer != null){
            mVideoPlayer.resume();
        }
    }


    /**
     * 销毁资源
     */
    public void destory(){
        if (mVideoPlayer != null){
            mVideoPlayer.destory();
        }
        mVideoPlayer = null;
        mParentContainer.removeAllViews();
        mParentContainer = null;
        mContext = null;
    }




    /**
     * 定义提供给外部调用业务逻辑的接口
     */
    public interface VideoSlotListener {

        ViewGroup getParentContainer();

        /**
         * 视频加载成功的回调方法
         */
        void onVideoLoadSuccess();


        /**
         * 视频加载失败的回调方法
         */
        void onVideoLoadFailed();


        /**
         * 视频加载完成的回调方法
         */
        void onVideoLoadComplete();


        /**
         * 点击视频界面的回调方法
         */
        void onClickVideo();



    }






}


