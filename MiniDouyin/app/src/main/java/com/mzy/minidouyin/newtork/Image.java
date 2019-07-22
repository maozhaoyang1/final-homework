package com.mzy.minidouyin.newtork;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class Image {
    public static void displayWebImage(String url, ImageView imageView) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }
}
