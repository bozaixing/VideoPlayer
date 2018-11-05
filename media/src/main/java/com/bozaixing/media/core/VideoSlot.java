package com.bozaixing.media.core;

import android.content.Context;
import android.view.ViewGroup;

import com.bozaixing.media.constant.VideoConstant;
import com.bozaixing.media.model.VideoModel;
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
    private VideoModel mVideoModel;
    private VideoSlotListener mVideoSlotListener;


    /**
     * 构造方法，初始化数据
     *
     * @param model
     * @param listener
     */
    public VideoSlot(VideoModel model, VideoSlotListener listener){
        mVideoModel = model;
        mVideoSlotListener = listener;
        mParentContainer = listener.getParentContainer();
        mContext = mParentContainer.getContext();
        // 初始化视频播放器对象
        initVideoPlayer();
    }


    /**
     * 初始化视频播放器控件
     */
    private void initVideoPlayer(){
        mVideoPlayer = new VideoPlayer(mContext, mParentContainer);
        if (mVideoModel != null){
            mVideoPlayer.setDataSource(mVideoModel.getVideoUrl());
            mVideoPlayer.setVideoPlayerListener(this);
        }
        // 将播放器控件添加到父容器中
        mParentContainer.addView(mVideoPlayer);
    }





















    @Override
    public void onClickVideo() {

    }

    @Override
    public void onBufferingUpdate(int time) {

    }

    /**
     * 视频加载成功的回调方法
     */
    @Override
    public void onVideoLoadSuccess() {
        if (mVideoSlotListener != null){
            mVideoSlotListener.onVideoLoadSuccess();
        }
    }


    /**
     * 视频加载失败的回调方法
     */
    @Override
    public void onVideoLoadFailed() {
        if (mVideoSlotListener != null){
            mVideoSlotListener.onVideoLoadFailed();
        }
    }


    /**
     * 视频播放完成的回调方法
     */
    @Override
    public void onVideoLoadComplete() {
        if (mVideoSlotListener != null){
            mVideoSlotListener.onVideoLoadComplete();
        }
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


