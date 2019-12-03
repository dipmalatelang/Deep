package com.travel.cotravel.fragment.account.profile.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.module.Upload;
import com.travel.cotravel.fragment.chat.adapter.UserAdapter;
import com.travel.cotravel.fragment.chat.module.Chatlist;
import com.travel.cotravel.fragment.chat.module.Token;
import com.travel.cotravel.fragment.trip.module.User;
import com.travel.cotravel.fragment.visitor.UserImg;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.travel.cotravel.Constants.ChatListInstance;
import static com.travel.cotravel.Constants.FavoritesInstance;
import static com.travel.cotravel.Constants.PicturesInstance;
import static com.travel.cotravel.Constants.TokensInstance;
import static com.travel.cotravel.Constants.TrashInstance;
import static com.travel.cotravel.Constants.UsersInstance;

public class TrashActivity extends BaseActivity {

    @BindView(R.id.trash_recyclerview)
    RecyclerView trashRecyclerview;
    @BindView(R.id.search_users)
    EditText searchUsers;

    FirebaseUser fuser;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.txtNoData)
    TextView txtNoData;
    @BindView(R.id.home_admob)
    AdView homeAdmob;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private UserAdapter userAdapter;
    List<UserImg> mUsers = new ArrayList<>();
    AdView mAdmobView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        ButterKnife.bind(this);
        progressBar=findViewById(R.id.progressBar);
        txtNoData = findViewById(R.id.txtNoData);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        trashRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        searchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ChatListInstance.child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Chatlist> usersList = new ArrayList<>();
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        txtNoData.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);

                        Chatlist chatlist = snapshot.getValue(Chatlist.class);
                        usersList.add(chatlist);
                    }
                } else {
                    txtNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                }

                chatList(usersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());
        initAdmob();
    }

    protected void initAdmob() {
        MobileAds.initialize(this, getString(R.string.app_id));
        mAdmobView = (AdView) findViewById(R.id.home_admob);
        if (AppSettings.ENABLE_ADMOB) {
            mAdmobView.setVisibility(View.VISIBLE);
            AdRequest.Builder builder = new AdRequest.Builder();
            AdRequest adRequest = builder.build();
            // Start loading the ad in the background.
            mAdmobView.loadAd(adRequest);
        } else {
            mAdmobView.setVisibility(View.GONE);
        }
    }

    private void searchUsers(String s) {
        ArrayList<UserImg> mUser = new ArrayList<>();

        for (UserImg userImg : mUsers) {
            if (userImg.getUser().getSearch() != null && (userImg.getUser().getSearch().contains(s))) {
                mUser.add(userImg);
            }
        }
        userAdapter = new UserAdapter(this, mUser, true, true, new UserAdapter.UserInterface() {
            @Override
            public void lastMessage(Context mContext, String userid, int position, TextView last_msg, TextView last_msg_time, ConstraintLayout chat) {
                checkForLastMsg(mContext, userid, last_msg, last_msg_time, chat);
            }

            @Override
            public void addToFav(String userid, int position) {
                setFav(fuser.getUid(), userid);
                mUsers.get(position).setFav(1);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void addToTrash(String userid, int position) {
                setTrash(fuser.getUid(), userid);
                mUsers.remove(position);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void restoreFromTrash(String userid, int position) {
                removeTrash(fuser.getUid(), userid);
                mUsers.remove(position);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void removeFromFav(String userid, int position) {
                removeFav(fuser.getUid(), userid);
                mUsers.get(position).setFav(0);
                userAdapter.notifyDataSetChanged();
            }

        });
        trashRecyclerview.setAdapter(userAdapter);

    }

    private void updateToken(String token) {
        Token token1 = new Token(token);
        TokensInstance.child(fuser.getUid()).setValue(token1);
    }

    private void chatList(List<Chatlist> usersList) {

        UsersInstance.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (Chatlist chatlist : usersList) {
                        if (Objects.requireNonNull(user).getId().equals(chatlist.getId())) {

                            TrashInstance.child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user.getId())) {

                                        UserImg userImg = new UserImg(user, "", 0);
                                        FavoritesInstance.child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                if (snapshot.hasChild(user.getId())) {
                                                    userImg.setFav(1);
                                                }


                                                PicturesInstance.child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                                            Upload upload = ds.getValue(Upload.class);

                                                            if (Objects.requireNonNull(upload).getType() == 1) {
                                                                userImg.setPictureUrl(upload.getUrl());
                                                            }
                                                        }


                                                        mUsers.add(userImg);

                                                        userAdapter = new UserAdapter(TrashActivity.this, mUsers, true, true, new UserAdapter.UserInterface() {
                                                            @Override
                                                            public void lastMessage(Context mContext, String userid, int position, TextView last_msg, TextView last_msg_time, ConstraintLayout chat) {
                                                                checkForLastMsg(mContext, userid, last_msg, last_msg_time, chat);
                                                            }

                                                            @Override
                                                            public void addToFav(String userid, int position) {
                                                                setFav(fuser.getUid(), userid);
                                                                mUsers.get(position).setFav(1);
                                                                userAdapter.notifyDataSetChanged();
                                                            }

                                                            @Override
                                                            public void addToTrash(String userid, int position) {
                                                                setTrash(fuser.getUid(), userid);
                                                                mUsers.remove(position);
                                                                userAdapter.notifyDataSetChanged();
                                                            }

                                                            @Override
                                                            public void restoreFromTrash(String userid, int position) {
                                                                removeTrash(fuser.getUid(), userid);
                                                                mUsers.remove(position);
                                                                userAdapter.notifyDataSetChanged();
                                                            }

                                                            @Override
                                                            public void removeFromFav(String userid, int position) {
                                                                removeFav(fuser.getUid(), userid);
                                                                mUsers.get(position).setFav(0);
                                                                userAdapter.notifyDataSetChanged();
                                                            }

                                                        });
                                                        trashRecyclerview.setAdapter(userAdapter);

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
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
