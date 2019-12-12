package com.travel.cotravel.fragment.account.profile.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.module.Upload;
import com.travel.cotravel.fragment.account.profile.ui.ViewVideoActivity;


import java.util.ArrayList;
import java.util.Objects;

public class CustomAdapter extends PagerAdapter {
    private Context ctx;
    private LayoutInflater inflater;
    private ArrayList<Upload> mUploads;
    String uid, gender;
    boolean isRunning=false;


    public CustomAdapter(Context ctx, String uid, ArrayList<Upload> uploads, String gender) {
        this.ctx = ctx;
        this.mUploads= uploads;
        this.uid=uid;
        this.gender=gender;
    }

    @Override
    public int getCount() {
        return mUploads.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = Objects.requireNonNull(inflater).inflate(R.layout.swipe,container,false);
        ImageView img = v.findViewById(R.id.imageView);
        VideoView videoView=v.findViewById(R.id.videoView);
        ImageView ivPlay=v.findViewById(R.id.ivPlay);

        ProgressBar progressBar=v.findViewById(R.id.progressBar);
        if(mUploads.get(position).getName().equalsIgnoreCase("Video"))
        {
            img.setVisibility(View.GONE);
            videoView.setVideoURI(Uri.parse(mUploads.get(position).getUrl()));
            videoView.seekTo(1000);
            progressBar.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            ivPlay.setVisibility(View.VISIBLE);

        /*    videoView.setOnPreparedListener(mp -> {
                mp.setVolume(0,0);
                mp.start();
            });*/

            ivPlay.setOnClickListener(v1 -> {
                Intent intent = new Intent(ctx, ViewVideoActivity.class);
                intent.putExtra("VideoUrl", mUploads.get(position).getUrl());
                ctx.startActivity(intent);
            });
        }
        else {
            if(gender.equalsIgnoreCase("Female"))
            {
                Glide.with(ctx).asBitmap().load(mUploads.get(position).getUrl())
                        .centerCrop()
                        .override(450,600)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                img.setImageResource(R.drawable.no_photo_female);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                img.setImageBitmap(resource);
                            }
                        });
            }
            else {
                Glide.with(ctx).asBitmap().load(mUploads.get(position).getUrl())
                        .centerCrop()
                        .override(450,600)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                img.setImageResource(R.drawable.no_photo_male);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                img.setImageBitmap(resource);
                            }
                        });
            }
        }


        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
        isRunning=false;
    }
}