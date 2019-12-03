package com.travel.cotravel.fragment.chat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.travel.cotravel.BaseFragment;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.chat.module.Chatlist;
import com.travel.cotravel.fragment.chat.module.Token;
import com.travel.cotravel.fragment.chat.adapter.UserAdapter;
import com.travel.cotravel.fragment.member.MembersActivity;
import com.travel.cotravel.fragment.trip.module.User;
import com.travel.cotravel.fragment.account.profile.module.Upload;
import com.travel.cotravel.fragment.visitor.UserImg;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.travel.cotravel.Constants.ChatListInstance;
import static com.travel.cotravel.Constants.FavoritesInstance;
import static com.travel.cotravel.Constants.PicturesInstance;
import static com.travel.cotravel.Constants.TokensInstance;
import static com.travel.cotravel.Constants.TrashInstance;
import static com.travel.cotravel.Constants.UsersInstance;

public class ChatFragment extends BaseFragment {



    EditText search_users;
    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    ArrayList<UserImg> mUsers = new ArrayList<>();

    TextView txtNoData;
    FirebaseUser fuser;
    ProgressBar progressBar;
    FloatingActionButton floatingActionButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        setHasOptionsMenu(false);
        recyclerView = view.findViewById(R.id.recycler_view);
        search_users=view.findViewById(R.id.search_users);
        progressBar=view.findViewById(R.id.progressBar);
        txtNoData=view.findViewById(R.id.txtNoData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MembersActivity.class));
            }
        });

        ChatListInstance.child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Chatlist> usersList = new ArrayList<>();
                if(dataSnapshot.getChildrenCount()>0)
                {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Chatlist chatlist = snapshot.getValue(Chatlist.class);
                        usersList.add(chatlist);
                    }

                    chatList(usersList);
                    progressBar.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.GONE);

                }
                else {
                    progressBar.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());

        search_users.addTextChangedListener(new TextWatcher() {
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

        return view;
    }

    private void searchUsers(String s)
    {
        ArrayList<UserImg> mUser=new ArrayList<>();

        for(UserImg userImg : mUsers){
            if(userImg.getUser().getSearch() != null && (userImg.getUser().getSearch().contains(s)))
            {
                mUser.add(userImg);
            }

        }
                userAdapter = new UserAdapter(getContext(), mUser, true, new UserAdapter.UserInterface() {
                    @Override
                    public void lastMessage(Context mContext, String userid, int position, TextView last_msg, TextView last_msg_time, ConstraintLayout chat) {
                        checkForLastMsg(mContext , userid, last_msg,last_msg_time,chat);
                    }

                    @Override
                    public void addToFav(String userid, int position) {
                        setFav(fuser.getUid(), userid);
                        mUsers.get(position).setFav(1);
                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void addToTrash(String userid, int position) {
                        setTrash(fuser.getUid(),userid);
                        mUsers.remove(position);
                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void restoreFromTrash(String userid, int position) {
                        removeTrash(fuser.getUid(),userid);
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
                recyclerView.setAdapter(userAdapter);

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
                                    if (!dataSnapshot.hasChild(user.getId())) {

                                        UserImg userImg=new UserImg(user, "", 0);
                                        PicturesInstance.child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                                    Upload upload = ds.getValue(Upload.class);

                                                    if (Objects.requireNonNull(upload).getType() == 1) {
                                                        userImg.setPictureUrl(upload.getUrl());
                                                    }
                                                }

                                                FavoritesInstance.child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.hasChild(user.getId())) {
                                                            userImg.setFav(1);
                                                        }

                                                        mUsers.add(userImg);

                                                        userAdapter = new UserAdapter(getContext(), mUsers, true, new UserAdapter.UserInterface() {
                                                            @Override
                                                            public void lastMessage(Context mContext, String userid, int position, TextView last_msg, TextView last_msg_time, ConstraintLayout chat) {
                                                                checkForLastMsg(mContext, userid, last_msg, last_msg_time,chat);
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
                                                                removeTrash(fuser.getUid(),userid);
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
                                                        recyclerView.setAdapter(userAdapter);

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
