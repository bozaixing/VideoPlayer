package com.bozaixing.videoplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bozaixing.media.core.VideoSlot;
import com.bozaixing.media.widgets.VideoPlayer;

/*
 * Author:  bozaixing.
 * Date:    2018-10-03.
 * Email:   654152983@qq.com.
 * Descr:   视频播放页面
 */
public class VideoActivity extends AppCompatActivity {

    /**
     * UI
     */
    private VideoPlayer mVideoPlayer;
    private VideoSlot mVideoSlot;


    /**
     * 显示视频播放页面的方法
     *
     * @param context
     */
    public static void show(Context context){
        context.startActivity(new Intent(context, VideoActivity.class));
    }


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mVideoPlayer = findViewById(R.id.video_player);
        mVideoPlayer.setDataSource("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4");
//        mVideoSlot = new VideoSlot("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4",
//                mParentContainer);
        mVideoPlayer.setVideoPlayerListener(new VideoPlayer.VideoPlayerListener() {
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

            @Override
            public void onClickBack() {
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoPlayer != null){
            mVideoPlayer.destory();
        }
    }
}
