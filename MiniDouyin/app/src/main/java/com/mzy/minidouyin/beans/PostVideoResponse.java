package com.mzy.minidouyin.beans;

import com.google.gson.annotations.SerializedName;

public class PostVideoResponse {

    // TODO-C2 (3) Implement your PostVideoResponse Bean here according to the response json
    @SerializedName("success") private boolean success;
    @SerializedName("item") private Feed item;

    public Feed getItem() {
        return item;
    }
    public void setItem(Feed item) {
        this.item = item;
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
                "success='" + success +
                ", item=" + item +
                '}';
    }
}
