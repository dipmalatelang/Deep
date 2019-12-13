package com.travel.cotravel.fragment.favourite.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.travel.cotravel.MainActivity;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.trip.module.User;
import com.travel.cotravel.fragment.visitor.UserImg;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ProfileVisitorViewHolder >
{

    private Context mContext;
    private List<UserImg> mTrip;
    private String uid;


    public FavouriteAdapter(Context mContext, String uid, List<UserImg> mTrip, FavouriteInterface listener) {
        this.uid=uid;
        this.mContext = mContext;
        this.mTrip = mTrip;
        this.listener = listener;

    }



    @NonNull
    @Override
    public ProfileVisitorViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favisit_item, parent, false);
        return new ProfileVisitorViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfileVisitorViewHolder holder, int position)
    {

        final User tList = mTrip.get(position).getUser();


        if(tList.getGender().equalsIgnoreCase("Female"))
        {
            Glide.with(mContext).load(R.drawable.no_photo_female).centerCrop().override(450,600).into(holder.mImage);
            holder.progressBar.setVisibility(View.GONE);

            if(tList.getAccount_type()==1) {
                Glide.with(mContext).asBitmap().load(mTrip.get(position).getPictureUrl()).placeholder(R.drawable.no_photo_female)
                        .centerCrop()
                        .override(450, 600)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                holder.mImage.setImageBitmap(resource);
                            }
                        });
            }
            else {
                Glide.with(mContext).load(R.drawable.hidden_photo_female_thumb).placeholder(R.drawable.hidden_photo_female_thumb)
                        .centerCrop()
                        .override(450, 600)
                       .into(holder.mImage);
            }
        }
        else {
            Glide.with(mContext).load(R.drawable.no_photo_male).centerCrop().override(450,600).into(holder.mImage);
            holder.progressBar.setVisibility(View.GONE);

            if (tList.getAccount_type() == 1) {
                Glide.with(mContext).asBitmap().load(mTrip.get(position).getPictureUrl()).placeholder(R.drawable.no_photo_male)
                        .centerCrop()
                        .override(450, 600)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                holder.mImage.setImageBitmap(resource);
                            }
                        });
            }
            else {
                Glide.with(mContext).load(R.drawable.hidden_photo_male_thumb).placeholder(R.drawable.hidden_photo_male_thumb)
                        .centerCrop()
                        .override(450, 600)
                       .into(holder.mImage);
            }
        }


        holder.mTitle.setText(tList.getName());

        holder.mCity.setText(tList.getLocation());


        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.setProfileVisit(uid,tList.getId());

                listener.sendFavourite(tList.getId());
                listener.setData(tList,position);


            }

        });
        if (position%2==0)
        {
            holder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorpurple1));
        } else if(position%3==0)
        {
            holder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorgreen2));
        } else if(position%4==0)
        {
            holder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorblue3));
        }

        else
            {
            holder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorbrowne4));
        }

        holder.ivTitle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_action_fav_remove));

        holder.ivTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).removeFav(uid, mTrip.get(position).getUser().getId());
                mTrip.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrip.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ProfileVisitorViewHolder extends RecyclerView.ViewHolder
    {

        ImageView mImage, ivTitle;
        TextView mTitle, mCity;
        CardView mCardView;
        public ConstraintLayout cllist;
        LinearLayout linearLayout;
        ProgressBar progressBar;


        ProfileVisitorViewHolder(View itemView)
        {
            super(itemView);

            mImage = itemView.findViewById(R.id.ivImage);
            ivTitle=itemView.findViewById(R.id.ivTitle);
            mTitle = itemView.findViewById(R.id.tvTitle);
            mCity = itemView.findViewById(R.id.tvCity);
            mCardView = itemView.findViewById(R.id.cardview);
            cllist =itemView.findViewById(R.id.cllist);
            linearLayout =itemView.findViewById(R.id.linearLayout);
            progressBar=itemView.findViewById(R.id.progressBar);
        }
    }

    FavouriteInterface listener;
    public interface FavouriteInterface{
        void sendFavourite(String id);
        void setProfileVisit(String uid, String id);
        void setData(User mTrip, int position);
    }
}