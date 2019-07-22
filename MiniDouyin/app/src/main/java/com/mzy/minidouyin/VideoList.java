package com.mzy.minidouyin;

import android.app.Activity;
import android.graphics.Rect;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mzy.minidouyin.beans.Feed;
import com.mzy.minidouyin.beans.FeedResponse;
import com.mzy.minidouyin.beans.PostVideoResponse;
import com.mzy.minidouyin.newtork.IMiniDouyinService;
import com.mzy.minidouyin.newtork.Image;
import com.mzy.minidouyin.utils.ResourceUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class VideoList extends Activity {

    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final int GRANT_PERMISSION = 3;
    private RecyclerView mRv;
    private List<Feed> mFeeds = new ArrayList<>();
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    public Button mBtn;
    private List<Call> mCallList = new ArrayList<>();

    private static final int REQUEST_EXTERNAL_STORAGE = 101;
    private String[] mPermissionsArrays = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_list);
        initRecyclerView();
        initBtns();
        fetchFeed();
        findViewById(R.id.camera).setOnClickListener(v -> {
            //todo 在这里申请相机、麦克风、存储的权限
            if (ContextCompat.checkSelfPermission(VideoList.this,
                    WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(VideoList.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED|| ContextCompat.checkSelfPermission(VideoList.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(VideoList.this,mPermissionsArrays,REQUEST_EXTERNAL_STORAGE);
            } else {
                startActivity(new Intent(VideoList.this, CameraActivity.class));
            }

        });
    }

    private void initBtns() {
        mBtn = findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String s = mBtn.getText().toString();
                if (getString(R.string.select_an_image).equals(s)) {
                        chooseImage();
                } else if (getString(R.string.select_a_video).equals(s)) {
                        chooseVideo();
                } else if (getString(R.string.post_it).equals(s)) {
                    if (mSelectedVideo != null && mSelectedImage != null) {
                        postVideo();
                    } else {
                        throw new IllegalArgumentException("error data uri, mSelectedVideo = " + mSelectedVideo + ", mSelectedImage = " + mSelectedImage);
                    }
                } else{
                    fetchFeed();
                    mBtn.setText(R.string.select_an_image);
                }
            }
        });
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
        public TextView usernameTv;
        public TextView studentIdTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            usernameTv = itemView.findViewById(R.id.username_text);
            studentIdTv = itemView.findViewById(R.id.studentID_text);
        }

        public void bind(final Activity activity, final Feed video) {
            Image.displayWebImage(video.getImage_url(), img);
            studentIdTv.setText(video.getUsername());
            usernameTv.setText(video.getStudent_id());
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoPlay.launch(activity, video);
                }
            });
        }
    }

    private void initRecyclerView() {
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRv.addItemDecoration(new MyDecoration());
        mRv.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new MyViewHolder(LayoutInflater.from(VideoList.this)
                        .inflate(R.layout.video_item, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
                final Feed video = mFeeds.get(i);
                viewHolder.bind(VideoList.this, video);
            }

            @Override public int getItemCount() {
                return mFeeds.size();
            }
        });
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "选择封面图"), PICK_IMAGE);
    }

    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "选择视频"), PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {

            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                mBtn.setText(R.string.select_a_video);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                mBtn.setText(R.string.post_it);
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(VideoList.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }
//上传
    private void postVideo() {
        mBtn.setText("上传中");
        mBtn.setEnabled(false);

        // TODO-C2 (6) Send Request to post a video with its cover image
        // if success, make a text Toast and show
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://test.androidcamp.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Call<PostVideoResponse> PostVideoResponseCall = retrofit.create(IMiniDouyinService.class).createVideo("harpoon1","hepeng",getMultipartFromUri("cover_image",mSelectedImage),getMultipartFromUri("video",mSelectedVideo));
        mCallList.add(PostVideoResponseCall);
        PostVideoResponseCall.enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                Toast toast=Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT);
                toast.show();
                mCallList.remove(call);
                mBtn.setText(R.string.select_an_image);
                mBtn.setEnabled(true);
            }
            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                Toast toast=Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT);
                toast.show();
                mCallList.remove(call);
                mBtn.setText(R.string.select_an_image);
                mBtn.setEnabled(true);
            }
        });


    }
//刷新
    public void fetchFeed() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://test.androidcamp.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Call<FeedResponse> FeedResponseCall = retrofit.create(IMiniDouyinService.class).fetchFeed();
        mCallList.add(FeedResponseCall);
        FeedResponseCall.enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                mFeeds.addAll(response.body().getFeeds());
//                resetRefreshBtn();
                mRv.getAdapter().notifyDataSetChanged();
                mCallList.remove(call);
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                mCallList.remove(call);
                Toast.makeText(VideoList.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    class MyDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            super.getItemOffsets(outRect, view, parent, state);
            int distance = getResources().getDimensionPixelOffset(R.dimen.pirtureDistance);
            outRect.set(distance, distance, distance, distance);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                //todo 判断权限是否已经授予
                if (requestCode == REQUEST_EXTERNAL_STORAGE) {
                    startActivity(new Intent(VideoList.this, CameraActivity.class));
                }
                break;
            }
        }
    }



}

