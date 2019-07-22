package com.mzy.minidouyin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.mzy.minidouyin.beans.Feed;

public class VideoPlay extends Activity {


    private static Feed video;
    VideoView videoView;
    ImageView iv_play1;
    boolean isplaying = true;

    public static void launch(Activity activity, Feed video) {
        Intent intent = new Intent(activity, VideoPlay.class);
        intent.putExtra("video",video);
        activity.startActivity(intent);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_play);
        iv_play1 = findViewById(R.id.iv_video_play1);

        video = (Feed) getIntent().getSerializableExtra("video");
        String url = video.getVideo_url();


        videoView = findViewById(R.id.video_view);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                iv_play1.setVisibility(View.INVISIBLE);
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isplaying = !isplaying;
                if (isplaying) {
                    iv_play1.setVisibility(View.INVISIBLE);
                    videoView.start();
                } else {
                    iv_play1.setVisibility(View.VISIBLE);
                    videoView.pause();
                }

                return false;
            }
        });
    }


}
