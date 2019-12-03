package com.travel.cotravel.fragment.account.profile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.ui.FacebookImageActivity;

import java.util.ArrayList;
import java.util.List;


public class FB_Adapter extends RecyclerView.Adapter<FB_Adapter.ImageViewHolder> {
    private Context mcontext;
    private List<FacebookImageActivity.Images> mUploads;
    private String TAG = "AdapterClass";
    int count=0;
    String uid;
    String gender;


    public FB_Adapter(Context context, String uid, String gender, List<FacebookImageActivity.Images> uploads, FbInterface fbInterface) {
        this.uid=uid;
        mcontext =context;
        this.gender=gender;
        mUploads =uploads;
        this.fbInterface=fbInterface;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.layout_album, parent,false);
        return new ImageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        count=0;


        for(int j=0;j<mUploads.get(position).getImage_Url().size();j++)
        {
            if(mUploads.get(position).getImage_Url().get(j).getStatus()==1)
            {
                count++;
            }
        }

        holder.imageView.setAdjustViewBounds(true);
        holder.imageView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));

        holder.txt_title.setText(mUploads.get(position).getName());
        String body=count+" out of "+mUploads.get(position).getImage_Url().size()+" added";
        holder.txt_body.setText(body);

        holder.cl_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbInterface.proceed(mUploads.get(position).getImage_Url());
            }
        });

     /*   if(gender.equalsIgnoreCase("Female"))
        {

            Glide.with(mcontext).load(mUploads.get(position).getImage_Url().get(0).getUrl())
                    .centerCrop()
                    .override(450,600)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.imageView.setImageResource(R.drawable.no_photo_female);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.imageView);

        }
        else {
            Glide.with(mcontext).load(mUploads.get(position).getImage_Url().get(0).getUrl())
                    .centerCrop()
                    .override(450, 600)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.imageView.setImageResource(R.drawable.no_photo_male);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                    })
                    .into(holder.imageView);
        }*/

    }


    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        ConstraintLayout cl_image;
        ProgressBar progressBar;
        TextView txt_title, txt_body;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            progressBar=itemView.findViewById(R.id.progressBar);
            imageView = itemView.findViewById(R.id.imageView);
            txt_title=itemView.findViewById(R.id.txt_title);
            txt_body=itemView.findViewById(R.id.txt_body);
            cl_image=itemView.findViewById(R.id.cl_image);

        }
    }

    FbInterface fbInterface;

    public interface FbInterface{
        void proceed(ArrayList<FacebookImageActivity.FbImage> image_url);
    }

}