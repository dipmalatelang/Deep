package com.travel.cotravel.fragment.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.chat.module.Chat;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl, gender;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl, String gender){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
        this.gender=gender;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        holder.right_msg_time.setText(chat.getMsg_time());
        holder.show_message.setText(chat.getMessage());


        if(gender.equalsIgnoreCase("Female"))
        {
            Glide.with(mContext).asBitmap().load(imageurl).placeholder(R.drawable.no_photo_female)
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
            Glide.with(mContext).asBitmap().load(imageurl).placeholder(R.drawable.no_photo_male)
                    .centerCrop()
                    .override(450,600)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.profile_image.setImageBitmap(resource);
                        }
                    });
        }


        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;
        public TextView right_msg_time;

        public ViewHolder(View itemView) {
            super(itemView);

            right_msg_time=itemView.findViewById(R.id.right_msg_time);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}