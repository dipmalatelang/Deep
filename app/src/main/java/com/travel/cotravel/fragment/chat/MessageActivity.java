package com.travel.cotravel.fragment.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.MainActivity;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.module.Upload;
import com.travel.cotravel.fragment.account.profile.ui.ProfileActivity;
import com.travel.cotravel.fragment.chat.adapter.MessageAdapter;
import com.travel.cotravel.fragment.chat.module.Chat;
import com.travel.cotravel.fragment.chat.notification.GMailSender;
import com.travel.cotravel.fragment.trip.module.User;
import com.travel.cotravel.fragment.visitor.UserImg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.travel.cotravel.Constants.ChatListInstance;
import static com.travel.cotravel.Constants.ChatsInstance;
import static com.travel.cotravel.Constants.FavoritesInstance;
import static com.travel.cotravel.Constants.PicturesInstance;
import static com.travel.cotravel.Constants.UsersInstance;


public class MessageActivity extends BaseActivity {

    ImageView profile_image;
    TextView username;
    FirebaseUser fuser;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListener;

    String userid,email;
    private List<UserImg> msgArray = new ArrayList<>();

    boolean notify = false;
    RelativeLayout message_realtivelayout;

    SharedPreferences sharedPreferences;
    String fusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        message_realtivelayout = findViewById(R.id.message_realtivelayout);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        email=intent.getStringExtra("email");

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Name")) {
            fusername = (sharedPreferences.getString("Name", ""));
        }

        UsersInstance.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(Objects.requireNonNull(user).getUsername());

                UserImg userImg=new UserImg(user, "", 0);
                PicturesInstance.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        msgArray.clear();
                        for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                            Upload mainPhoto = snapshot1.getValue(Upload.class);
                            if (Objects.requireNonNull(mainPhoto).type == 1)
                               userImg.setPictureUrl(mainPhoto.getUrl());

                        }

                        if(user.getGender().equalsIgnoreCase("Female"))
                        {
                            Glide.with(getApplicationContext()).asBitmap().load(userImg.getPictureUrl()).placeholder(R.drawable.no_photo_female)
                                    .fitCenter()
                                    .override(450,600)
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                            profile_image.setImageBitmap(resource);
                                        }
                                    });
                        }
                        else {
                            Glide.with(getApplicationContext()).asBitmap().load(userImg.getPictureUrl()).placeholder(R.drawable.no_photo_male)
                                    .centerCrop()
                                    .override(450,600)
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            profile_image.setImageBitmap(resource);
                                        }
                                    });
                        }
                        FavoritesInstance.child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.hasChild(user.getId())) {
                                    userImg.setFav(1);
                                }
                        msgArray.add(userImg);
                        readMesagges(fuser.getUid(), userid, userImg.getPictureUrl(), user.getGender());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);
    }

    private void sendMessage() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("support@cotravel.in",
                            "Jiyakiyan@9");
                    sender.sendMail("Cotravel", "New Messasge from" +username,
                            "support@cotravel.in",email );
                    Log.d(TAG, "SendMail22: "+email+""+username);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        }).start();
    }


    private void seenMessage(final String userid) {

        seenListener = ChatsInstance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (Objects.requireNonNull(chat).getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message, String str_date, String str_time) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("msg_date",str_date);
        hashMap.put("msg_time",str_time);
        hashMap.put("isseen", false);

        ChatsInstance.push().setValue(hashMap);


        ChatListInstance.child(fuser.getUid()).child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    ChatListInstance.child(fuser.getUid()).child(userid).child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ChatListInstance.child(userid).child(fuser.getUid()).child("id").setValue(fuser.getUid());

        final String msg = message;

        UsersInstance.child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendMsgNotifiaction(fuser.getUid(),userid,receiver, Objects.requireNonNull(user).getUsername(), msg, "Message");
                    sendMessage();
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void readMesagges(final String myid, final String userid, final String imageurl, String gender) {
        mchat = new ArrayList<>();

        ChatsInstance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (Objects.requireNonNull(chat).getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl,gender);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ChatsInstance.removeEventListener(seenListener);
        currentUser("none");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// todo: goto back activity from here

            Intent intent = new Intent(MessageActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.toolbar, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar:
                setProfile(fuser.getUid(),msgArray.get(0).getUser().getId(),fusername);
                Intent mIntent = new Intent(this, ProfileActivity.class);
                mIntent.putExtra("MyUserObj", msgArray.get(0));
                startActivityForResult(mIntent,1);

                break;
            case R.id.btn_send:

                notify = true;
                String msg = text_send.getText().toString();

                if (!msg.equals("")) {
                    DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                    String str_time = dateFormat.format(new Date());

                    DateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy");
                    String str_date = dateFormat2.format(new Date());


                    sendMessage(fuser.getUid(), userid, msg, str_date, str_time);
                } else {
                    snackBar(message_realtivelayout, "You can't send empty message");
                }

                text_send.getText().clear();

                break;
        }
    }
}
