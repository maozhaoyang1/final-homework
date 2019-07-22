package com.mzy.minidouyin.newtork;

import com.mzy.minidouyin.beans.FeedResponse;
import com.mzy.minidouyin.beans.PostVideoResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface IMiniDouyinService {
    String HOST = "http://test.androidcamp.bytedance.com/";

    @Multipart

    @POST("/mini_douyin/invoke/video")
    Call<PostVideoResponse> createVideo(
            @Query("student_id") String studentId,
            @Query("user_name") String userName,
            @Part MultipartBody.Part image, @Part MultipartBody.Part vedio);

    @GET("/mini_douyin/invoke/video") Call<FeedResponse> fetchFeed();

}
