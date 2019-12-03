package com.travel.cotravel.fragment.favourite;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.travel.cotravel.BaseFragment;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.module.Upload;
import com.travel.cotravel.fragment.account.profile.ui.AppSettings;
import com.travel.cotravel.fragment.account.profile.ui.ProfileActivity;
import com.travel.cotravel.fragment.favourite.adapter.FavouriteAdapter;
import com.travel.cotravel.fragment.trip.module.PlanTrip;
import com.travel.cotravel.fragment.trip.module.TripData;
import com.travel.cotravel.fragment.trip.module.User;
import com.travel.cotravel.fragment.visitor.UserImg;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.travel.cotravel.Constants.FavoritesInstance;
import static com.travel.cotravel.Constants.PicturesInstance;
import static com.travel.cotravel.Constants.TripsInstance;
import static com.travel.cotravel.Constants.UsersInstance;

public class FavouriteFragment extends BaseFragment {


    TextView txtNoData;
    ProgressBar progressBar;
    private RecyclerView myFavRV;
    private FirebaseUser fuser;
    View view;
    private List<UserImg> myFavArray = new ArrayList<>();
SharedPreferences sharedPreferences;
String fusername;
    AdView mAdmobView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_favourite, container, false);

        progressBar=view.findViewById(R.id.progressBar);
        txtNoData=view.findViewById(R.id.txtNoData);
        myFavRV = view.findViewById(R.id.myFavRV);
        mAdmobView= view.findViewById(R.id.home_admob);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        myFavRV.setLayoutManager(mGridLayoutManager);

        showProgressDialog();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences = getActivity().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Name")) {
            fusername = (sharedPreferences.getString("Name", ""));
        }
        favList(Objects.requireNonNull(fuser));

        initAdmob();
        return view;
    }
    protected void initAdmob() {
        MobileAds.initialize(getContext(), getString(R.string.app_id));

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


    public void favList(FirebaseUser fuser) {


        FavoritesInstance.child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myFavArray.clear();
                if (snapshot.getChildrenCount() > 0) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        String userKey = dataSnapshot.getKey();
                        UsersInstance.child(Objects.requireNonNull(userKey)).addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);

                                        UserImg userImg=new UserImg(user, "", 1);
                                        PicturesInstance.child(Objects.requireNonNull(user).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                                                    Upload mainPhoto = snapshot1.getValue(Upload.class);
                                                    if (Objects.requireNonNull(mainPhoto).type == 1)
                                                        userImg.setPictureUrl(mainPhoto.getUrl());

                                                }

                                                myFavArray.add(userImg);

                                                FavouriteAdapter tripAdapter = new FavouriteAdapter(getActivity(), fuser.getUid(), myFavArray, new FavouriteAdapter.FavouriteInterface() {
                                                    @Override
                                                    public void sendFavourite(String id) {
                                                        getData(id);
                                                    }

                                                    @Override
                                                    public void setProfileVisit(String uid, String id) {

                                                      setProfile(uid,id,fusername);

                                                    }

                                                    @Override
                                                    public void setData(User tList, int position) {
                                                        if (tList.getAccount_type() == 1) {
                                                            Intent mIntent = new Intent(getActivity(), ProfileActivity.class);
                                                            mIntent.putExtra("MyUserObj", myFavArray.get(position));
                                                            startActivityForResult(mIntent, 1);
                                                        } else {
                                                            hiddenProfileDialog();
                                                        }
                                                    }

                                                });
                                                myFavRV.setAdapter(tripAdapter);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
//                                }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                    }
                    progressBar.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
//                    Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dismissProgressDialog();
    }


    private void getData(String id) {

        UsersInstance.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tripList.clear();
                        myDetail.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            final User user = snapshot.getValue(User.class);


                            if (Objects.requireNonNull(user).getId().equalsIgnoreCase(id)) {

                                UserImg userImg=new UserImg(user, "", 1);
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

                                                TripsInstance.orderByKey().equalTo(user.getId())
                                                        .addValueEventListener(new ValueEventListener() {

                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                from_to_dates.clear();
                                                                dates.clear();

                                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                    String city = "";
                                                                    String tripNote = "";
                                                                    String date = "";

                                                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                                                                        TripData tripData = snapshot1.getValue(TripData.class);

                                                                        city += tripData.getLocation();
                                                                        tripNote += tripData.getTrip_note();
                                                                        date += tripData.getFrom_date() + " - " + tripData.getTo_date();

                                                                        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                                                        try {
                                                                            Date date1 = format.parse(tripData.getFrom_date());
                                                                            dates.add(date1);
                                                                            PlanTrip planTrip = new PlanTrip(tripData.getLocation(), tripData.getFrom_date(), tripData.getTo_date());
                                                                            from_to_dates.add(planTrip);

                                                                        } catch (ParseException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }


                                                                    tripList = findClosestDate(dates, userImg);

                                                                }

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
