package com.travel.cotravel.fragment.member;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.ui.ProfileActivity;
import com.travel.cotravel.fragment.member.adapter.MembersAdapter;
import com.travel.cotravel.fragment.trip.module.TripList;
import com.travel.cotravel.fragment.trip.module.User;
import com.travel.cotravel.fragment.visitor.UserImg;
import com.travel.cotravel.fragment.account.profile.module.Upload;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Objects;

import static com.travel.cotravel.Constants.FavoritesInstance;
import static com.travel.cotravel.Constants.PicturesInstance;
import static com.travel.cotravel.Constants.UsersInstance;

public class MembersActivity extends BaseActivity {
    private RecyclerView recyclerView;

    private MembersAdapter membersAdapter;
    SharedPreferences sharedPreferences;
    String look_user;
    int ageTo, ageFrom;
    final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    String fusername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mGridLayoutManager);

        sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("Name")) {
            fusername = (sharedPreferences.getString("Name", ""));
        }

        if (sharedPreferences.contains("TravelWith")) {
            ArrayList<String> travel_with = new Gson().fromJson((sharedPreferences.getString("TravelWith", "")), new TypeToken<ArrayList<String>>() {}.getType());

            if(travel_with.size()>0)
            {
                if(travel_with.size()>1)
                {
                    look_user = "Female,Male";
                }
                else {
                    look_user=travel_with.get(0);
                }
            }
        }

        if (sharedPreferences.contains("AgeRange")) {
            ArrayList<String> ageRange = new Gson().fromJson((sharedPreferences.getString("AgeRange", "")), new TypeToken<ArrayList<String>>() {
            }.getType());

            ageFrom= Integer.parseInt(ageRange.get(0));
            ageTo= Integer.parseInt(ageRange.get(1));
        }
        tripList(fuser,look_user, ageFrom, ageTo);
    }

    public void tripList(FirebaseUser fuser, String look_user, int ageFrom, int ageTo) {

        UsersInstance.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tripList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            User user = snapshot.getValue(User.class);
                            if (!Objects.requireNonNull(user).getId().equalsIgnoreCase(fuser.getUid()) && user.getAccount_type()==1) {


                                if(look_user.contains(user.getGender())&& Integer.parseInt(user.getAge())>=ageFrom && Integer.parseInt(user.getAge())<=ageTo)
                                {

                                    UserImg userImg=new UserImg(user, "",0);
                                    FavoritesInstance.child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            if (snapshot.hasChild(user.getId()))
                                                userImg.setFav(1);

                                            PicturesInstance.child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                                                        Upload mainPhoto = snapshot1.getValue(Upload.class);
                                                        if (Objects.requireNonNull(mainPhoto).type == 1)
                                                            userImg.setPictureUrl(mainPhoto.getUrl());

                                                    }


                                                    tripList = findAllMembers(userImg);

                                                    membersAdapter = new MembersAdapter(MembersActivity.this, fuser.getUid(), tripList, new MembersAdapter.ProfileData() {
                                                        @Override
                                                        public void setData(TripList tList, int position) {
                                                            Intent mIntent = new Intent(MembersActivity.this, ProfileActivity.class);
                                                            mIntent.putExtra("MyObj", tripList.get(position));
                                                            startActivityForResult(mIntent, 1);
                                                        }

                                                        @Override
                                                        public void setProfileVisit(String uid, String id) {

                                                            setProfile(uid,id,fusername);

                                                        }

                                                    });
                                                    recyclerView.setAdapter(membersAdapter);
                                                    membersAdapter.notifyDataSetChanged();

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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

}
