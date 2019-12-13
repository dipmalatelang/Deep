package com.travel.cotravel.fragment.account.profile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.ui.FacebookImageActivity;

import java.util.ArrayList;

public class DetailFBAdapter extends RecyclerView.Adapter<DetailFBAdapter.DetailFBHolder> {

    Context context;
    String gender;
    ArrayList<FacebookImageActivity.FbImage> urlImages;
    public DetailFBAdapter(Context context, ArrayList<FacebookImageActivity.FbImage> urlImages, String gender, DetailFbInterface detailFbInterface)
    {
        this.gender=gender;
        this.context=context;
        this.urlImages=urlImages;
        this.detailFbInterface=detailFbInterface;
    }
    @NonNull
    @Override
    public DetailFBHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_fb_images,parent,false);
        return new DetailFBHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailFBHolder holder, int position) {

        if(urlImages.get(position).getStatus()==1)
        {
            holder.ivTitle.setVisibility(View.VISIBLE);
            holder.ivadd.setVisibility(View.INVISIBLE);
        }
        else{
            holder.ivTitle.setVisibility(View.INVISIBLE);
            holder.ivadd.setVisibility(View.VISIBLE);
        }


        holder.rl_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(urlImages.get(position).getStatus()!=1)
                {
                    detailFbInterface.fetchFbImage(urlImages.get(position).getUrl());
                    urlImages.get(position).setStatus(1);
                    holder.ivTitle.setVisibility(View.VISIBLE);
                    holder.ivadd.setVisibility(View.INVISIBLE);
                }
            }
        });

        if(gender.equalsIgnoreCase("Female"))
        {
            Glide.with(context).asBitmap().load(urlImages.get(position).getUrl()).placeholder(R.drawable.no_photo_female)
                    .centerCrop()
                    .override(450,600)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.imageView.setImageBitmap(resource);
                        }
                    });

        }
        else {
            Glide.with(context).asBitmap().load(urlImages.get(position).getUrl()).placeholder(R.drawable.no_photo_male)
                    .centerCrop()
                    .override(450, 600)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.imageView.setImageBitmap(resource);
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return urlImages.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class DetailFBHolder extends RecyclerView.ViewHolder {
        ImageView imageView, ivTitle, ivadd;
        RelativeLayout rl_image;
        ProgressBar progressBar;

        public DetailFBHolder(@NonNull View itemView) {
            super(itemView);

            progressBar=itemView.findViewById(R.id.progressBar);
            imageView=itemView.findViewById(R.id.imageView);
            rl_image=itemView.findViewById(R.id.rl_image);
            ivTitle=itemView.findViewById(R.id.ivTitle);
            ivadd=itemView.findViewById(R.id.ivadd);

        }
    }

    DetailFbInterface detailFbInterface;

    public interface DetailFbInterface{
        void fetchFbImage(String imgUrl);
    }
}
