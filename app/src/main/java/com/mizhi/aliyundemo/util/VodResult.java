/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.mizhi.aliyundemo.util;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/8.
 */

public class VodResult implements Serializable {
    private String requestId;
    private String uploadAddress;
    private String uploadAuth;
    private String videoId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUploadAddress() {
        return uploadAddress;
    }

    public void setUploadAddress(String uploadAddress) {
        this.uploadAddress = uploadAddress;
    }

    public String getUploadAuth() {
        return uploadAuth;
    }

    public void setUploadAuth(String uploadAuth) {
        this.uploadAuth = uploadAuth;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
