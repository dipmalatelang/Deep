package com.travel.cotravel.fragment.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.chat.MessageActivity;
import com.travel.cotravel.fragment.trip.module.User;
import com.travel.cotravel.fragment.visitor.UserImg;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<UserImg> mUsers;
    private boolean ischat;
    private boolean istrash;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public UserAdapter(Context mContext, List<UserImg> mUsers, boolean ischat, UserInterface listener){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
        this.listener=listener;
    }

    public UserAdapter(Context mContext, List<UserImg> mUsers, boolean ischat, boolean istrash, UserInterface listener){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
        this.istrash=istrash;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_user_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position).getUser();

        holder.username.setText(user.getUsername());

        if(mUsers.get(position).getFav()==1)
        {
            holder.ic_action_fav_remove.setVisibility(View.VISIBLE);
            holder.tvfavourite.setVisibility(View.GONE);
        }
        else {
            holder.tvfavourite.setVisibility(View.VISIBLE);
        }

        if(user.getGender().equalsIgnoreCase("Female"))
        {
            Glide.with(mContext).asBitmap().load(mUsers.get(position).getPictureUrl()).placeholder(R.drawable.no_photo_female)
                    .fitCenter()
                    .override(450,600)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            holder.profile_image.setImageBitmap(resource);
                        }
                    });
        }
        else {
            Glide.with(mContext).asBitmap().load(mUsers.get(position).getPictureUrl()).placeholder(R.drawable.no_photo_male)
                    .centerCrop()
                    .override(450,600)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.profile_image.setImageBitmap(resource);
                        }
                    });
        }


        viewBinderHelper.bind(holder.swipe_layout_1, user.getId());



        if(istrash)
        {
            holder.tvrestore.setVisibility(View.VISIBLE);
            holder.tvdelete.setVisibility(View.GONE);
        }
        else
            {
            holder.tvrestore.setVisibility(View.GONE);
        }

        if (ischat){
            listener.lastMessage(mContext,user.getId(), position,holder.last_msg, holder.last_msg_time, holder.chat);

        } else {
            holder.last_msg.setVisibility(View.GONE);
            holder.last_msg_time.setVisibility(View.GONE);
        }



        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                intent.putExtra("email",user.getEmail());
                mContext.startActivity(intent);
            }
        });

        holder.tvrestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.swipe_layout_1.close(true);
                listener.restoreFromTrash(user.getId(),position);

            }
        });
        holder.tvdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.swipe_layout_1.close(true);
                listener.addToTrash(user.getId(), position);

            }
        });
        holder.tvfavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.swipe_layout_1.close(true);
                listener.addToFav(user.getId(),position);

            }
        });


        holder.ic_action_fav_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.ic_action_fav_remove.setVisibility(View.GONE);
                listener.removeFromFav(user.getId(),position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;

        SwipeRevealLayout swipe_layout_1;

        TextView tvdelete, tvfavourite, tvrestore;
        private TextView last_msg,last_msg_time;
                private ConstraintLayout chat;
        ImageView ic_action_fav_remove;


        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);

            last_msg = itemView.findViewById(R.id.last_msg);
            last_msg_time=itemView.findViewById(R.id.last_msg_time);
            swipe_layout_1=itemView.findViewById(R.id.swipe_layout_1);
            tvfavourite=itemView.findViewById(R.id.tvfavourite);
            tvdelete=itemView.findViewById(R.id.tvdelete);
            tvrestore=itemView.findViewById(R.id.tvrestore);
            chat =itemView.findViewById(R.id.chat);
            ic_action_fav_remove =itemView.findViewById(R.id.fev);
        }
    }

    UserInterface listener;
    public interface UserInterface
    {
        void lastMessage(Context mContext, String userid, int position, TextView last_msg, TextView last_msg_time, ConstraintLayout chat);
        void addToFav(String userid, int position);
        void addToTrash(String userid, int position);
        void restoreFromTrash(String userid, int position);
        void removeFromFav(String userid, int position);
    }


}
