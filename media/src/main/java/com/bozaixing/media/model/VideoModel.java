package com.bozaixing.media.model;

import java.io.Serializable;

/**
 * Author:  bozaixing
 * Email:   654152983@qq.com
 * Date:    2018/11/5
 * Descr:   视频Model对象
 */
public class VideoModel implements Serializable {

    // 视频url地址
    private String videoUrl;

    // 视频缩略图封面的地址
    private String thumbUrl;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }
}
