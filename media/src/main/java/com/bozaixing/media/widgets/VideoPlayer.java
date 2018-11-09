package com.bozaixing.media.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bozaixing.media.R;
import com.bozaixing.media.constant.VideoConstant;
import com.bozaixing.media.util.VideoUtil;

/*
 * Author:  bozaixing.
 * Date:    2018-10-02.
 * Email:   654152983@qq.com.
 * Descr:   自定义视频播放器，封装实现基本功能
 */
public class VideoPlayer extends RelativeLayout implements View.OnClickListener,
        TextureView.SurfaceTextureListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener {

    /**
     * TAG
     */
    private static final String TAG = "VideoPlayer";


    /**
     * CONSTANT
     */
    private static final int TIME_MSG = 0x01;
    private static final int TIME_INTERVAL = 1000;
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSING = 2;
    private static final int LOAD_TOTAL_COUNT = 3;


    /**
     * UI
     */
    private ViewGroup mParentContainer;
    private RelativeLayout mPlayerView;
    private TextureView mTextureView;
    private ImageView mThumbView;
    private LinearLayout mLoadingControllerLayout;
    private ProgressBar mLoadingView;
    private TextView mLoadingTextView;
    private LinearLayout mTopControllerLayout;
    private ImageView mBackView;
    private TextView mTitleView;
    private LinearLayout mBatteryAndTimeLayout;
    private ImageView mBatteryView;
    private TextView mTimeView;
    private LinearLayout mBottomControllerLayout;
    private ImageView mPlayAndPauseView;
    private TextView mPositionView;
    private TextView mDurationView;
    private SeekBar mSeekBarView;
    private ImageView mFullView;
    private TextView mLengthView;
    private ImageView mCenterStartView;


    /**
     * DATA
     */
    private String mVideoUrl;
    private int mScreenWidth;
    private int mScreenHeight;


    /**
     * STATUS
     */
    private boolean mIsCanPlay = true;
    private boolean mIsMute;
    private boolean mIsRealPause;
    private boolean mIsComplete;
    private int mCurrentLoadCount;
    // 播放器的默认状态为空闲状态
    private int mCurrentPlayerState = STATE_IDLE;
    // 视频缓冲的百分比
    private int mBufferingPercent;

    /**
     * OBJECT
     */
    private Surface mSurface;
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private LockScreenEventReceiver mLockScreenEventReceiver;
    private VideoPlayerListener mVideoPlayerListener;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIME_MSG:
                    // 如果处于播放状态中
                    if (isPlaying()) {
                        if (mVideoPlayerListener != null) {
                            mVideoPlayerListener.onBufferingUpdate(getCurrentPosition());
                        }
                        // 更新播放状态
                        updatePlayProgress();

                        sendEmptyMessageDelayed(TIME_MSG, TIME_INTERVAL);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 构造方法，初始化数据
     *
     * @param context
     * @param parentContainer
     */
    public VideoPlayer(@NonNull Context context, ViewGroup parentContainer) {
        super(context);
        // 初始化显示控件
        init(context, parentContainer);

        // 注册广播接收器
        registerBroadcastReceiver();
    }


    /**
     * 初始化显示控件
     *
     * @param context
     */
    private void init(Context context, ViewGroup parentContainer) {
        // 初始化音频管理器
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // 计算控件显示的宽度和高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        // 宽高比为16:9
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = (int) (mScreenWidth * VideoConstant.VIDEO_HEIGHT_PERCENT);

        mParentContainer = parentContainer;
        mPlayerView = (RelativeLayout) inflate(context, R.layout.media_video_player_layout, this);
        mTextureView = findViewById(R.id.texture_view);
        mThumbView = findViewById(R.id.thumb_view);
        mLoadingControllerLayout = findViewById(R.id.loading_controller_layout);
        mLoadingView = findViewById(R.id.loading_view);
        mLoadingTextView = findViewById(R.id.loading_text_view);
        mTopControllerLayout = findViewById(R.id.top_controller_layout);
        mBackView = findViewById(R.id.back_view);
        mTitleView = findViewById(R.id.title_view);
        mBatteryAndTimeLayout = findViewById(R.id.battery_and_time_layout);
        mBatteryView = findViewById(R.id.battery_view);
        mTimeView = findViewById(R.id.time_view);
        mBottomControllerLayout = findViewById(R.id.bottom_controller_layout);
        mPlayAndPauseView = findViewById(R.id.play_and_pause_view);
        mPositionView = findViewById(R.id.position_view);
        mDurationView = findViewById(R.id.duration_view);
        mSeekBarView = findViewById(R.id.seek_bar_view);
        mFullView = findViewById(R.id.full_view);
        mLengthView = findViewById(R.id.length_view);
        mCenterStartView = findViewById(R.id.center_start_view);

        // 设置播放器的宽度和高度
        LayoutParams params = new LayoutParams(mScreenWidth, mScreenHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayerView.setLayoutParams(params);

        // 设置保持屏幕常亮
        mTextureView.setKeepScreenOn(true);
        // 注册点击事件
        mTextureView.setOnClickListener(this);
        // 注册缓冲的监听事件
        mTextureView.setSurfaceTextureListener(this);

        mCenterStartView.setOnClickListener(this);
        mPlayAndPauseView.setOnClickListener(this);
        mFullView.setOnClickListener(this);
        mBackView.setOnClickListener(this);

    }


    /**
     * 空闲状态的界面显示的View
     */
    private void showIdleView(){
        // 隐藏加载控制器
        mLoadingControllerLayout.setVisibility(GONE);
        // 隐藏底部控制器
        mBottomControllerLayout.setVisibility(GONE);
        // 显示缩略图
        mThumbView.setVisibility(VISIBLE);
        // 显示总时长显示控件
        mLengthView.setVisibility(VISIBLE);
        // 显示中间的启动播放按钮
        mCenterStartView.setVisibility(VISIBLE);
    }


    /**
     * 加载状态时界面显示的View
     */
    private void showLoadingView(){
        // 隐藏缩略图
        mThumbView.setVisibility(GONE);
        // 隐藏底部控制器
        mBottomControllerLayout.setVisibility(GONE);
        // 隐藏总时长显示控件
        mLengthView.setVisibility(GONE);
        // 隐藏中间的启动播放按钮
        mCenterStartView.setVisibility(GONE);
        // 显示加载控制器
        mLoadingControllerLayout.setVisibility(VISIBLE);
    }

    /**
     * 播放状态时界面显示的View
     */
    private void showPlayView(){
        // 隐藏缩略图
        mThumbView.setVisibility(GONE);
        // 隐藏加载控制器
        mLoadingControllerLayout.setVisibility(GONE);
        // 隐藏总时长显示控件
        mLengthView.setVisibility(GONE);
        // 隐藏中间的启动播放按钮
        mCenterStartView.setVisibility(GONE);
        // 显示底部控制器
        mBottomControllerLayout.setVisibility(VISIBLE);
        // 设置播放暂停按钮为播放状态
        mPlayAndPauseView.setImageResource(R.drawable.ic_player_pause);
    }


    /**
     * 暂停状态时界面显示的View
     */
    private void showPauseView(){
        // 隐藏缩略图
        mThumbView.setVisibility(GONE);
        // 隐藏加载控制器
        mLoadingControllerLayout.setVisibility(GONE);
        // 隐藏总时长显示控件
        mLengthView.setVisibility(GONE);
        // 隐藏中间的启动按钮
        mCenterStartView.setVisibility(GONE);
        // 显示底部控制器
        mBottomControllerLayout.setVisibility(VISIBLE);
        // 设置播放暂停按钮为暂停状态
        mPlayAndPauseView.setImageResource(R.drawable.ic_player_start);
    }


    /**
     * 更新播放进度
     */
    private void updatePlayProgress(){
        int currentPosition = getCurrentPosition();
        int duration = getDuration();
        int progress = currentPosition / duration * 100;
        int bufferingPercent = getBufferingPercent();
        mPositionView.setText(VideoUtil.formatTime(currentPosition));
        mDurationView.setText(VideoUtil.formatTime(duration));
        mSeekBarView.setSecondaryProgress(bufferingPercent);
        mSeekBarView.setProgress(progress);
    }



    /**
     * 是指视频播放源
     *
     * @param videoUrl
     */
    public void setDataSource(String videoUrl) {
        mVideoUrl = videoUrl;
    }


    /**
     * 设置是否能进行播放视频
     *
     * @param isCanPlay isCanPlay为true时表示能播放视频
     */
    public void setIsCanPlay(boolean isCanPlay) {
        mIsCanPlay = isCanPlay;
    }


    /**
     * 返回值为true时表示能进行播放视频
     *
     * @return
     */
    public boolean isCanPlay() {
        return mIsCanPlay;
    }


    /**
     * 设置是否静音
     *
     * @param isMute isMute为true时表示当前视频为静音状态
     */
    public void setIsMute(boolean isMute) {
        mIsMute = isMute;
    }


    /**
     * 返回值为true时表示静音
     *
     * @return
     */
    public boolean isMute() {
        return mIsMute;
    }


    /**
     * 设置当前视频是否真正进入暂停状态
     *
     * @param isRealPause isRealPause为true时表示视频真正的进入暂停状态
     */
    public void setIsRealPause(boolean isRealPause) {
        mIsRealPause = isRealPause;
    }


    /**
     * 返回值为true时表示真正的暂停
     *
     * @return
     */
    public boolean isRealPause() {
        return mIsRealPause;
    }


    /**
     * 设置当前视频是否播放完成
     *
     * @param isComplete isComplete为true时表示视频播放完成
     */
    public void setIsComplete(boolean isComplete) {
        this.mIsComplete = isComplete;
    }


    /**
     * 返回值为true时表示视频播放完成
     *
     * @return
     */
    public boolean isComplete() {
        return mIsComplete;
    }


    /**
     * 返回当前播放器的状态
     *
     * @return
     */
    public int getCurrentPlayerState() {
        return mCurrentPlayerState;
    }


    /**
     * 设置当前播放器的状态
     *
     * @param currentPlayerState
     */
    public void setCurrentPlayerState(int currentPlayerState) {
        mCurrentPlayerState = currentPlayerState;
    }

    /**
     * 设置视频播放器的音量
     */
    private void setVolume() {
        if (mMediaPlayer != null) {
            // 如果为静音状态
            if (isMute()) {
                mMediaPlayer.setVolume(0.0f, 0.0f);
            } else {
                if (mAudioManager != null) {
                    int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.setVolume(streamVolume, streamVolume);
                } else {
                    mMediaPlayer.setVolume(0.0f, 0.0f);
                }
            }
        }
    }


    /**
     * 判断当前视频是否处于播放状态
     *
     * @return 返回true表示处于播放状态中
     */
    public boolean isPlaying() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }


    /**
     * 返回视频当前的播放位置
     *
     * @return
     */
    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }


    /**
     * 返回当前播放视频的总时长
     *
     * @return
     */
    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }


    /**
     * 返回视频缓冲的百分比
     *
     * @return
     */
    private int getBufferingPercent(){

        return mBufferingPercent;
    }


    /**
     * 进入播放状态时的状态更新
     */
    private void entryResumeUpdateState() {
        setCurrentPlayerState(STATE_PLAYING);
        setIsCanPlay(true);
        setIsRealPause(false);
        setIsComplete(false);
    }





    /**
     * 检查并初始化一个MediaPlayer对象
     */
    private synchronized void checkMediaPlayer() {
        if (mMediaPlayer == null) {
            // 每次都创建一个新的播放器对象
            mMediaPlayer = createMediaPlayer();
        }
    }




    /**
     * 初始化MediaPlayer对象
     *
     * @return
     */
    private MediaPlayer createMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        // 重置状态
        mMediaPlayer.reset();
        // 设置音频媒体流的属性
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        // 注册一个回调监听，在有警告或错误信息时调用。例如：开始缓冲、缓冲结束、下载速度变化
        mMediaPlayer.setOnInfoListener(this);
        if (mSurface != null && mSurface.isValid()) {
            mMediaPlayer.setSurface(mSurface);
        } else {
            stop();
        }
        return mMediaPlayer;
    }



    /**
     * 初始化加载视频
     */
    public void load() {
        // 如果当前播放器的状态不为空闲状态则直接返回
        // 因为只有在空闲状态下才会进行视频的加载
        if (getCurrentPlayerState() != STATE_IDLE) {
            return;
        }
        showLoadingView();
        try {
            // 开始进行视频的加载
            setCurrentPlayerState(STATE_IDLE);
            checkMediaPlayer();
            mMediaPlayer.setDataSource(mVideoUrl);
            // 异步加载视频
            mMediaPlayer.prepareAsync();
            // 设置音量
            setVolume();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            // 视频加载出现错误后重新加载视频
            stop();
        }

    }


    /**
     * 恢复视频的播放
     */
    public void resume() {
        // 如果当前状态不为暂停状态则直接返回
        if (getCurrentPlayerState() != STATE_PAUSING) {
            return;
        }
        // 只有当前视频不处于播放状态才恢复视频的播放
        if (!isPlaying()) {
            // 更新状态
            entryResumeUpdateState();
            // 移除调用seek()方法后触发的监听器
            mMediaPlayer.setOnSeekCompleteListener(null);
            // 启动播放
            mMediaPlayer.start();
            // 发送消息
            mHandler.sendEmptyMessage(TIME_MSG);
            // 显示播放状态的界面
            showPlayView();
        }else {
            // 显示暂停状态的界面
            showPauseView();
        }

    }


    /**
     * 跳到指定点播放视频
     *
     * @param position
     */
    public void seekAndResume(int position) {
        if (mMediaPlayer != null) {
            // 更新状态
            entryResumeUpdateState();
            mMediaPlayer.seekTo(position);
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    // 启动播放
                    mMediaPlayer.start();
                    // 发送消息
                    mHandler.sendEmptyMessage(TIME_MSG);
                }
            });
        }
    }


    /**
     * 暂停视频的播放
     */
    public void pause() {
        // 如果当前状态不为播放状态则直接返回
        if (getCurrentPlayerState() != STATE_PLAYING) {
            return;
        }
        // 更新状态
        setCurrentPlayerState(STATE_PAUSING);
        if (isPlaying()) {
            mMediaPlayer.pause();
            if (!isCanPlay()) {
                mMediaPlayer.seekTo(0);
            }
        }
        showPauseView();
        // 移除消息
        mHandler.removeCallbacksAndMessages(null);
    }


    /**
     * 跳到指定点暂停视频
     *
     * @param position
     */
    public void seekAndPause(int position) {
        // 如果当前状态不为播放状态直接返回
        if (getCurrentPlayerState() != STATE_PLAYING) {
            return;
        }
        // 更新状态
        setCurrentPlayerState(STATE_PAUSING);
        if (isPlaying()) {
            mMediaPlayer.seekTo(position);
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    // 暂停视频的播放
                    mMediaPlayer.pause();
                    // 移除消息
                    mHandler.removeCallbacksAndMessages(null);
                }
            });
        }

    }


    /**
     * 视频播放完成后回到初始状态
     */
    public void playBack() {
        // 更新状态
        setCurrentPlayerState(STATE_PAUSING);
        if (mMediaPlayer != null) {
            // 移除调用seek()方法后触发的监听器
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.seekTo(0);
            // 暂停视频的播放
            mMediaPlayer.pause();
        }
        // 移除消息
        mHandler.removeCallbacksAndMessages(null);

    }


    /**
     * 停止状态
     */
    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        // 移除消息
        mHandler.removeCallbacksAndMessages(null);
        // 改变状态
        setCurrentPlayerState(STATE_IDLE);
        // 满足条件则进行视频的重新加载
        if (mCurrentLoadCount < LOAD_TOTAL_COUNT) {
            mCurrentLoadCount++;
            load();
        }else {
            showPauseView();
        }
    }


    /**
     * 视频播放器加载视频准备就绪状态下的回调方法
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer = mp;
        if (mMediaPlayer != null) {
            // 设置视频缓冲的监听器
            mMediaPlayer.setOnBufferingUpdateListener(this);
            // 重置视频的加载次数
            mCurrentLoadCount = 0;
            // 回调加载视频成功的方法
            if (mVideoPlayerListener != null) {
                mVideoPlayerListener.onVideoLoadSuccess();
            }
            // 播放视频
            setCurrentPlayerState(STATE_PAUSING);
            resume();
        }
    }


    /**
     * 视频播放器缓冲视频过程的回调方法
     *
     * @param mp
     * @param percent
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }


    /**
     * 视频播放器加载视频或者播放视频失败时的回调方法
     *
     * @param mp
     * @param what
     * @param extra
     * @return 返回true表示由我们自己进行处理错误，不提交给MediaPlayer对象处理
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        setCurrentPlayerState(STATE_ERROR);
        mMediaPlayer = mp;
        // 重置视频播放器
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
        if (mCurrentLoadCount >= LOAD_TOTAL_COUNT) {
            showPauseView();
            // 回调加载视频失败的方法
            if (mVideoPlayerListener != null) {
                mVideoPlayerListener.onVideoLoadFailed();
            }
        }
        // 尝试重新加载视频
        stop();

        return true;
    }


    /**
     * 在有警告或错误信息时调用。例如：开始缓冲、缓冲结束、下载速度变化
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return true;
    }

    /**
     * 视频播放器视频播放完成时的回调方法
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        // 回调视频播放完成的方法
        if (mVideoPlayerListener != null) {
            mVideoPlayerListener.onVideoLoadComplete();
        }
        // 设置播放器回到初始状态
        playBack();


    }


    /**
     * 点击事件的回调方法
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v == mCenterStartView){   // 中间的播放按钮
            // 如果为空闲状态
            if (getCurrentPlayerState() == STATE_PAUSING){
                resume();
            }else {
                load();
            }
        }else if (v == mPlayAndPauseView){      // 播放暂停按钮
            // 如果为播放状态
            if (getCurrentPlayerState() == STATE_PLAYING){
                pause();
            }else {
                resume();
            }
        }else if (v == mFullView){      // 全屏播放按钮
            if (mVideoPlayerListener != null){
                mVideoPlayerListener.onClickFullScreen();
            }
        }else if (v == mBackView){      // 返回按钮
            if (mVideoPlayerListener != null){
                mVideoPlayerListener.onClickBack();
            }
        }
    }

    /**
     * 播放器控件显示状态发生改变的回调方法
     *
     * @param changedView
     * @param visibility
     */
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && getCurrentPlayerState() == STATE_PAUSING) {
            if (isRealPause() || isComplete()) {
                pause();
            } else {
                resume();
            }
        } else {
            pause();
        }
    }


    /**
     * TextureView控件准备就绪时的回调方法
     *
     * @param surfaceTexture
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (mSurface == null){
            mSurface = new Surface(surfaceTexture);
        }
        // 检查并创建MediaPlayer
        checkMediaPlayer();

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    /**
     * 回收资源
     */
    public void destory() {
        if (mMediaPlayer != null) {
            // 移除调用seek()方法后触发的监听器
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        // 重新视频加载次数变量
        mCurrentLoadCount = 0;
        // 重置播放状态
        setCurrentPlayerState(STATE_IDLE);
        setIsRealPause(false);
        setIsComplete(false);
        setIsCanPlay(false);

        // 移除广播接收器的监听
        unregisterBroadcastReceiver();

        // 移除消息
        mHandler.removeCallbacksAndMessages(null);
    }


    /**
     * 注册一个锁屏事件的广播接收器
     */
    private void registerBroadcastReceiver() {
        if (mLockScreenEventReceiver == null) {
            mLockScreenEventReceiver = new LockScreenEventReceiver();
            IntentFilter filter = new IntentFilter();
            // 添加一个用户锁屏事件的过滤Action
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            // 添加一个用户解锁屏幕事件的过滤Action
            filter.addAction(Intent.ACTION_USER_PRESENT);
            // 注册广播接收器
            if (getContext() != null) {
                getContext().registerReceiver(mLockScreenEventReceiver, filter);
            }

        }
    }


    /**
     * 移除锁屏事件的广播接收器
     */
    private void unregisterBroadcastReceiver() {
        if (mLockScreenEventReceiver != null) {
            if (getContext() != null) {
                getContext().unregisterReceiver(mLockScreenEventReceiver);
            }
        }
    }


    /**
     * 初始化视频播放器的监听器
     *
     * @param listener
     */
    public void setVideoPlayerListener(VideoPlayerListener listener) {
        mVideoPlayerListener = listener;
    }


    /**
     * 定义内部的锁屏广播的处理类
     */
    private class LockScreenEventReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_OFF:      // 锁屏
                    // 如果处于播放状态中则暂停视频的播放
                    if (getCurrentPlayerState() == STATE_PLAYING) {
                        pause();
                    }
                    break;
                case Intent.ACTION_USER_PRESENT:    // 解锁屏幕
                    // 如果当前状态为暂停状态
                    if (getCurrentPlayerState() == STATE_PAUSING) {
                        if (isRealPause()) {
                            // 手动点的暂停回来还是设置暂停
                            pause();
                        } else {
                            // 否则播放视频
                            resume();
                        }
                    }
                    break;
                default:
                    break;
            }

        }
    }


    /**
     * 定义视频播放器的监听器
     */
    public interface VideoPlayerListener {

        /**
         * 返回当前视频的缓冲进度(视频播放到了第几秒)
         *
         * @param time
         */
        void onBufferingUpdate(int time);


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


        /**
         * 视频全屏播放的回调方法
         */
        void onClickFullScreen();


        /**
         * 全屏播放点击返回时的回调方法
         */
        void onClickBack();

    }


}
