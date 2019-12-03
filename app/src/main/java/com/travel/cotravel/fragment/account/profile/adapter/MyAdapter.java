package com.travel.cotravel.fragment.account.profile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

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
import com.travel.cotravel.fragment.account.profile.module.Upload;
import com.travel.cotravel.fragment.account.profile.ui.EditPhotoActivity;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ImageViewHolder> {
    private Context mcontext;
    private List<Upload> mUploads;
    String uid;
    String gender;

    public MyAdapter(Context context, String uid, String gender, List<Upload> uploads, PhotoInterface listener) {
        this.uid=uid;
        mcontext =context;
        mUploads =uploads;
        this.gender=gender;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.layout_images, parent,false);
        return new ImageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        Upload uploadCurrent = mUploads.get(position);


        if(mUploads.get(position).getName().equalsIgnoreCase("Video"))
        {
            holder.imageView.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoView.setVideoURI(Uri.parse(mUploads.get(position).getUrl()));
            holder.videoView.start();

        }
        else {
            holder.imageView.setAdjustViewBounds(true);
            holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            if(gender.equalsIgnoreCase("Female"))
            {

                Glide.with(mcontext).asBitmap().load(uploadCurrent.getUrl())
                        .centerCrop()
                        .override(450,600)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                holder.progressBar.setVisibility(View.GONE);
                                holder.imageView.setImageResource(R.drawable.no_photo_female);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                holder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                holder.imageView.setImageBitmap(resource);
                            }
                        });

            }
            else {
                Glide.with(mcontext).asBitmap().load(uploadCurrent.getUrl())
                        .centerCrop()
                        .override(450, 600)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                holder.progressBar.setVisibility(View.GONE);
                                holder.imageView.setImageResource(R.drawable.no_photo_male);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                holder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                holder.imageView.setImageBitmap(resource);
                            }
                        });
            }

        }


        if(uploadCurrent.getType()==3)
        {
            holder.pp_eye.setText("Make Public");
        }
        else if(uploadCurrent.getType()==2)
        {
            holder.pp_eye.setText("Make Private");
        }
        else if(uploadCurrent.getType()==1){
            holder.ivTitle.setVisibility(View.VISIBLE);
            holder.pp_eye.setText("Make Private");

            ((EditPhotoActivity)mcontext).appDetails("CurProfilePhoto",uploadCurrent.getId());
        }

        holder.flipView.setOnFlipListener(new EasyFlipView.OnFlipAnimationListener() {
            @Override
            public void onViewFlipCompleted(EasyFlipView flipView, EasyFlipView.FlipState newCurrentSide)
            {
                holder.set_main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        listener.setProfilePhoto(mUploads.get(position).getId(),((EditPhotoActivity)mcontext).getAppDetails("CurProfilePhoto"),position);
                        holder.ivTitle.setVisibility(View.VISIBLE);

                    }
                });

                holder.pp_eye.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.setPhotoAsPrivate(mUploads.get(position).getId());
                    }
                });

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.removePhoto(mUploads.get(position).getId());
                    }
                });
            }
        });
    }



    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView,ivTitle;
        TextView set_main, pp_eye, delete;
        EasyFlipView flipView;
        ProgressBar progressBar;
        VideoView videoView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            videoView=itemView.findViewById(R.id.videoView);
            ivTitle=itemView.findViewById(R.id.ivTitle);

            flipView=itemView.findViewById(R.id.flipView);
            set_main=itemView.findViewById(R.id.set_main);
            pp_eye=itemView.findViewById(R.id.pp_eye);
            delete=itemView.findViewById(R.id.delete);
            progressBar=itemView.findViewById(R.id.progressBar);

        }
    }

    PhotoInterface listener;
    public interface PhotoInterface{
        void setProfilePhoto(String id, String previousValue,int position);
        void removePhoto(String id);
        void setPhotoAsPrivate(String id);
    }

}