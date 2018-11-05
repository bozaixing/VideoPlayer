package com.bozaixing.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bozaixing.media.widgets.VideoPlayer;

/*
 * Author:  bozaixing.
 * Date:    2018-10-03.
 * Email:   654152983@qq.com.
 * Descr:   视频播放页面
 */
public class VideoActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * UI
     */
    private FrameLayout mVideoParentContainer;
    private VideoPlayer mVideoPlayer;
    private Button mPlayVideo;
    private Button mPauseVideo;


    /**
     * 显示视频播放页面的方法
     *
     * @param context
     */
    public static void show(Context context){
        context.startActivity(new Intent(context, VideoActivity.class));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mPlayVideo = findViewById(R.id.play_video);
        mPauseVideo = findViewById(R.id.pause_video);
        mPlayVideo.setOnClickListener(this);
        mPauseVideo.setOnClickListener(this);
        mVideoParentContainer = findViewById(R.id.video_parent_container);
        mVideoPlayer = new VideoPlayer(this, mVideoParentContainer);
        mVideoPlayer.setDataSource("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4");

        mVideoParentContainer.addView(mVideoPlayer);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_video:
                if (mVideoPlayer != null){
                    mVideoPlayer.resume();
                }
                break;
            case R.id.pause_video:
                if (mVideoPlayer != null){
                    mVideoPlayer.pause();
                }
                break;
                default:
                    break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoPlayer != null){
            mVideoPlayer.destory();
        }
    }
}
