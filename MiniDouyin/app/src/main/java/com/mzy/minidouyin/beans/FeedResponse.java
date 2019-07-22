package com.mzy.minidouyin.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedResponse {

    // TODO-C2 (2) Implement your FeedResponse Bean here according to the response json
    @SerializedName("feeds") private List<Feed> feeds;
    @SerializedName("success") private boolean success;

    public List<Feed> getFeeds() {
        return feeds;
    }
    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public boolean getSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "FeedResponse{" +
                "feeds='" + feeds +
                ", success=" + success +
                '}';
    }
}

