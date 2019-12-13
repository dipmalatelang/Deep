package com.travel.cotravel.fragment.member.adapter;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.trip.module.TripList;
import com.travel.cotravel.fragment.trip.module.User;
import com.travel.cotravel.fragment.visitor.UserImg;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.TripViewHolder > {
    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_THREE = 2;

    private static final int VIEW_TYPE_SMALL = 1;
    private static final int VIEW_TYPE_BIG = 2;
    private Context mContext;
    private List<TripList> mTrip;
    private String uid;
    private GridLayoutManager mLayoutManager;

    public MembersAdapter(Context mContext, String uid, List<TripList> mTrip, ProfileData listener, GridLayoutManager layoutManager) {
        this.uid=uid;
        this.mContext = mContext;
        this.mTrip = mTrip;
        this.listener = listener;
        this.mLayoutManager = layoutManager;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_BIG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_big, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_member, parent, false);
        }
        return new TripViewHolder(view, viewType);
       /*
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_member, parent, false);
        return new TripViewHolder(mView);*/
    }

    @Override
    public int getItemViewType(int position) {
        int spanCount = mLayoutManager.getSpanCount();
        if (spanCount == SPAN_COUNT_ONE) {
            return VIEW_TYPE_BIG;
        } else {
            return VIEW_TYPE_SMALL;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {

        TripList tList = mTrip.get(position);
        User userList = mTrip.get(position).getUser();
        UserImg userImg = mTrip.get(position).getUserImg();

        if(userList.getGender().equalsIgnoreCase("Female"))
        {
            Glide.with(mContext).load(R.drawable.no_photo_female).centerCrop().override(450,600).into(holder.mImage);
            holder.progressBar.setVisibility(View.GONE);

            if(userList.getAccount_type()==1)
            {
                Glide.with(mContext).asBitmap().load(userImg.getPictureUrl()).placeholder(R.drawable.no_photo_female)
                        .centerCrop()
                        .override(450,600)
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

            if (userList.getAccount_type() == 1) {
                Glide.with(mContext).asBitmap().load(userImg.getPictureUrl()).placeholder(R.drawable.no_photo_male)
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

        holder.mTitle.setText(userList.getName()+" , "+userList.getAge());

        holder.mCity.setText(userList.getLocation());


        if(userList.getStatus().equalsIgnoreCase("Online"))
        {
            holder.tvStatus.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvStatus.setVisibility(View.GONE);
        }
        if (getItemViewType(position) == VIEW_TYPE_SMALL) {
            if (position % 2 == 0)
                holder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color8));
            else if (position % 3 == 0) {
                holder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color9));
            } else if (position % 4 == 0) {
                holder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color6));
            } else if (position % 5 == 0) {
                holder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color5));
            } else {
                holder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorbrowne4));
            }
        }else {
            holder.linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.gray_bg));
        }
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.setData(tList,position);
                listener.setProfileVisit(uid,userList.getId());

            }
        });
    }


    ProfileData listener ;
    public interface ProfileData{
        void setData(TripList tList, int position);
        void setProfileVisit(String uid, String id);
    }

    @Override
    public int getItemCount() {

        return mTrip.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class TripViewHolder extends RecyclerView.ViewHolder {

        ImageView mImage;
        ProgressBar progressBar;
        TextView mTitle, mCity ,tvStatus;
        CardView mCardView;
        LinearLayout linearLayout;

        TripViewHolder(View itemView, int viewType) {

            super(itemView);
            if (viewType == VIEW_TYPE_BIG) {
                mImage = itemView.findViewById(R.id.ivImage);
                mTitle = itemView.findViewById(R.id.tvTitle);
                mCity = itemView.findViewById(R.id.tvCity);
                mCardView = itemView.findViewById(R.id.cardview);
                linearLayout = itemView.findViewById(R.id.linearLayout);
                progressBar = itemView.findViewById(R.id.progressBar);
                tvStatus=itemView.findViewById(R.id.tvStatus);
            }
            else {
                mImage = itemView.findViewById(R.id.ivImage);
                mTitle = itemView.findViewById(R.id.tvTitle);
                mCity = itemView.findViewById(R.id.tvCity);
                mCardView = itemView.findViewById(R.id.cardview);
                linearLayout = itemView.findViewById(R.id.linearLayout);
                progressBar = itemView.findViewById(R.id.progressBar);
                tvStatus=itemView.findViewById(R.id.tvStatus);
            }
        }
    }
}