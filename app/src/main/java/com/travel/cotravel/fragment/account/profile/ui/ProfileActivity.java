package com.travel.cotravel.fragment.account.profile.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.adapter.CustomAdapter;
import com.travel.cotravel.fragment.account.profile.adapter.PlanTripsAdapter;
import com.travel.cotravel.fragment.account.profile.module.Permit;
import com.travel.cotravel.fragment.account.profile.module.Upload;
import com.travel.cotravel.fragment.chat.MessageActivity;
import com.travel.cotravel.fragment.trip.EditProfileActivity;
import com.travel.cotravel.fragment.trip.module.TripData;
import com.travel.cotravel.fragment.trip.module.TripList;
import com.travel.cotravel.fragment.trip.module.User;
import com.travel.cotravel.fragment.visitor.UserImg;
import com.travel.cotravel.login.LoginActivity;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.travel.cotravel.Constants.PhotoRequestInstance;
import static com.travel.cotravel.Constants.PicturesInstance;
import static com.travel.cotravel.Constants.TripsInstance;
import static com.travel.cotravel.Constants.UsersInstance;


public class ProfileActivity extends BaseActivity {

    CustomAdapter adapter;
    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.profile_details)
    ConstraintLayout profileDetails;
    @BindView(R.id.tvCount)
    Chip tvCount;
    @BindView(R.id.textProfile)
    Chip textProfile;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.iv_edit_profile)
    ImageView ivEditProfile;
    @BindView(R.id.tv_about_me)
    TextView tvAboutMe;
    @BindView(R.id.tv_about_me_value)
    TextView tvAboutMeValue;
    @BindView(R.id.card_summary)
    CardView cardSummary;
    @BindView(R.id.tv_height)
    TextView tvHeight;
    @BindView(R.id.tv_height_value)
    TextView tvHeightValue;
    @BindView(R.id.tv_body_type)
    TextView tvBodyType;
    @BindView(R.id.tv_body_type_value)
    TextView tvBodyTypeValue;
    @BindView(R.id.tv_eye)
    TextView tvEye;
    @BindView(R.id.tv_eye_value)
    TextView tvEyeValue;
    @BindView(R.id.tv_hair)
    TextView tvHair;
    @BindView(R.id.tv_hair_value)
    TextView tvHairValue;
    @BindView(R.id.tv_trip)
    TextView tvTrip;
    @BindView(R.id.card_trip)
    CardView cardTrip;
    @BindView(R.id.floatingActionButton2)
    FloatingActionButton floatingActionButton2;
    @BindView(R.id.iv_fav_user)
    ImageView ivFavUser;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.tvCountry)
    TextView tvCountry;
    @BindView(R.id.tvUser)
    TextView tvUser;
    @BindView(R.id.tv_looking_for_value)
    TextView tvLookingForValue;
    @BindView(R.id.tv_want_to_visit_value)
    TextView tvWantToVisitValue;
    @BindView(R.id.fab_backFromProfile)
    FloatingActionButton fabBackFromProfile;
    @BindView(R.id.tv_nationality)
    TextView tvNationality;
    @BindView(R.id.card_nationality)
    CardView cardNationality;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.card_Gender)
    CardView cardGender;
    @BindView(R.id.card_Language)
    CardView cardLanguage;
    @BindView(R.id.card_height)
    CardView cardHeight;
    @BindView(R.id.card_body_type)
    CardView cardBodyType;
    @BindView(R.id.card_eye)
    CardView cardEye;
    @BindView(R.id.card_hair)
    CardView cardHair;
    @BindView(R.id.tv_looking_for)
    TextView tvLookingFor;
    @BindView(R.id.card_looking_for)
    CardView cardLookingFor;
    @BindView(R.id.tv_want_to_visit)
    TextView tvWantToVisit;
    @BindView(R.id.card_want_to_visit)
    CardView cardWantToVisit;
    @BindView(R.id.tv_nationality_values)
    TextView tvNationalityValues;
    @BindView(R.id.tv_gender_value)
    TextView tvGenderValue;
    @BindView(R.id.tv_language)
    TextView tvLanguage;
    @BindView(R.id.tv_language_value)
    TextView tvLanguageValue;
    @BindView(R.id.cl_main)
    ConstraintLayout clMain;
    @BindView(R.id.cl_language)
    ConstraintLayout clLanguage;
    @BindView(R.id.CL_lookingfor)
    ConstraintLayout CLLookingfor;
    @BindView(R.id.tv_phone_values)
    TextView tvPhoneValues;
    @BindView(R.id.card_phone)
    CardView cardPhone;
    private ArrayList<Upload> upload1 = new ArrayList<>();
    private ArrayList<Upload> upload2 = new ArrayList<>();
    private ArrayList<Upload> upload3 = new ArrayList<>();

    private ArrayList<Upload> uploads = new ArrayList<>();
    ArrayList<User> userList = new ArrayList<>();
    int account_type = 1;
    boolean notify = false;

    private FirebaseUser fuser;
    TripList tripL;
    UserImg userL;
    String profileId;

    @BindView(R.id.rv_trip_value)
    RecyclerView rvTripValue;

    ArrayList<TripData> planTripsList = new ArrayList<>();
    int privateValue = 0;

    StringBuilder str_user;
    SharedPreferences sharedPreferences;
    String fusername;
    AdView mAdmobView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        ButterKnife.bind(this);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);

        LinearLayoutManager ll_manager = new LinearLayoutManager(ProfileActivity.this);
        rvTripValue.setLayoutManager(ll_manager);

        if (sharedPreferences.contains("Name")) {
            fusername = (sharedPreferences.getString("Name", ""));
        }

        setDataToProfile();
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

    private void setDataToProfile() {
        if (getIntent().getSerializableExtra("MyObj") == null && getIntent().getSerializableExtra("MyUserObj") == null) {
            ivEditProfile.setVisibility(View.VISIBLE);
            textProfile.setVisibility(View.VISIBLE);
            ivMenu.setVisibility(View.VISIBLE);
            ivFavUser.setVisibility(View.GONE);
            floatingActionButton2.hide();
            profileId = fuser.getUid();

            if (sharedPreferences.contains("Gender")) {
                String gender = (sharedPreferences.getString("Gender", ""));
                getAllImages(profileId, gender);
            }

            getAllTrips(profileId);
            getProfileData(profileId);

        } else if (getIntent().getSerializableExtra("MyObj") != null) {
            ivEditProfile.setVisibility(View.GONE);
            textProfile.setText("Request Private photos");
            textProfile.setChipIconResource(R.drawable.ic_action_black_eye);
            ivMenu.setVisibility(View.GONE);
            ivFavUser.setVisibility(View.VISIBLE);
            floatingActionButton2.show();
            tripL = (TripList) getIntent().getSerializableExtra("MyObj");
            profileId = Objects.requireNonNull(tripL).getUser().getId();
            getAllImages(profileId, tripL.getUser().getGender());
            getAllTrips(profileId);
            if (tripL.getUserImg().getFav() == 1) {
                ivFavUser.setImageResource(R.drawable.ic_action_fav_remove);
            } else {
                ivFavUser.setImageResource(R.drawable.ic_action_fav_add);
            }

            //need to change this later
            setDetails(tripL.getUser().getName(), tripL.getUser().getGender(), tripL.getUser().getPhone(), tripL.getUser().isShow_number(),tripL.getUser().getAbout_me(), tripL.getUser().getAge(), tripL.getUser().getLooking_for(), tripL.getUser().getTravel_with(), tripL.getUser().getLocation(), tripL.getUser().getNationality(),
                    tripL.getUser().getLang(), tripL.getUser().getHeight(), tripL.getUser().getBody_type(), tripL.getUser().getEyes(), tripL.getUser().getHair(), tripL.getUser().getVisit(), tripL.getPlanLocation(), tripL.getFrom_to_date(), tripL.getUserImg().getPictureUrl());

        } else if (getIntent().getSerializableExtra("MyUserObj") != null) {
            ivEditProfile.setVisibility(View.GONE);
            textProfile.setText("Request Private photos");
            textProfile.setChipIconResource(R.drawable.ic_action_black_eye);
            ivMenu.setVisibility(View.GONE);
            ivFavUser.setVisibility(View.VISIBLE);
            floatingActionButton2.show();
            userL = (UserImg) getIntent().getSerializableExtra("MyUserObj");
            profileId = Objects.requireNonNull(userL).getUser().getId();
            getAllImages(profileId, userL.getUser().getGender());
            getAllTrips(profileId);

            setDetails(userL.getUser().getName(), userL.getUser().getGender(), userL.getUser().getPhone(), userL.getUser().isShow_number(), userL.getUser().getAbout_me(), userL.getUser().getAge(), userL.getUser().getLooking_for(), userL.getUser().getTravel_with(), "", userL.getUser().getNationality(),
                    userL.getUser().getLang(), userL.getUser().getHeight(), userL.getUser().getBody_type(), userL.getUser().getEyes(), userL.getUser().getHair(), userL.getUser().getVisit(), "", "", "");

        }
    }

    private void setDetails(String name, String gender, String phone, boolean show_number,String about_me, String age, ArrayList<String> looking_for, ArrayList<String> travel_with, String userLocation, String nationality, String lang, String height, String body_type, String eyes, String hair, String visit, String planLocation, String from_to_date, String imageUrl) {

        String str_travel_with = null;

        if(show_number)
        {
            if (phone != null && !phone.equalsIgnoreCase("")) {
                tvPhoneValues.setText(phone);
            } else {
                cardPhone.setVisibility(View.GONE);
            }
        }
        else {
            cardPhone.setVisibility(View.GONE);
        }

        if (nationality != null && !nationality.equalsIgnoreCase("")) {
            tvNationalityValues.setText(nationality);
        } else {
            cardNationality.setVisibility(View.GONE);
        }

        if (gender != null && !gender.equalsIgnoreCase("")) {
            tvGenderValue.setText(gender);
        } else {
            cardGender.setVisibility(View.GONE);
        }

        if (lang != null && !lang.equalsIgnoreCase("")) {
            tvLanguageValue.setText(lang);
        } else {
            cardLanguage.setVisibility(View.GONE);
        }

        if ((name != null && !name.equalsIgnoreCase("")) || (age != null && !age.equalsIgnoreCase(""))) {
            str_user = new StringBuilder(name);
            str_user.append(" , ");
            str_user.append(age);
            tvUser.setText(str_user);
        }


        if (about_me != null && !about_me.equalsIgnoreCase("")) {
            tvAboutMeValue.setText(about_me);
        } else {
            cardSummary.setVisibility(View.GONE);
        }


        if (travel_with != null) {
            if (travel_with.size() > 0) {
                for (int j = 0; j < travel_with.size(); j++) {
                    if (str_travel_with != null) {
                        str_travel_with += ", " + travel_with.get(j);
                    } else {
                        str_travel_with = travel_with.get(j);
                    }

                }
            } else {
                cardLookingFor.setVisibility(View.GONE);
            }


        } else {
            cardLookingFor.setVisibility(View.GONE);
        }


        if (height != null && !height.equalsIgnoreCase("")) {
            tvHeightValue.setText(height);
        } else {
            cardHeight.setVisibility(View.GONE);
        }


        if (body_type != null && !body_type.equalsIgnoreCase("") && !body_type.equalsIgnoreCase("select")) {
            tvBodyTypeValue.setText(body_type);
        } else {
            cardBodyType.setVisibility(View.GONE);
        }


        if (eyes != null && !eyes.equalsIgnoreCase("") && !eyes.equalsIgnoreCase("select")) {
            tvEyeValue.setText(eyes);
        } else {
            cardEye.setVisibility(View.GONE);
        }

        if (hair != null && !hair.equalsIgnoreCase("") && !hair.equalsIgnoreCase("select")) {
            tvHairValue.setText(hair);
        } else {
            cardHair.setVisibility(View.GONE);
        }


        if (userLocation != null && !userLocation.equalsIgnoreCase("")) {
            tvCountry.setText(userLocation);
        }

        if (visit != null && !visit.equalsIgnoreCase("")) {
            tvWantToVisitValue.setText(visit);
        } else {
            cardWantToVisit.setVisibility(View.GONE);
        }


        String strlookingFor = "";
        if (looking_for != null) {
            if (looking_for.size() > 0) {
                for (int i = 0; i < looking_for.size(); i++) {
                    strlookingFor += " "+ looking_for.get(i);
                }
                tvLookingForValue.setText(strlookingFor);
            } else {
                cardLookingFor.setVisibility(View.GONE);
            }
        } else {
            cardLookingFor.setVisibility(View.GONE);
        }

    }


    public void getProfileData(String id) {

        UsersInstance.child(id).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();

                        User user = dataSnapshot.getValue(User.class);
                        account_type = Objects.requireNonNull(user).getAccount_type();
                        setDetails(user.getName(), user.getGender(), user.getPhone(), user.isShow_number(), user.getAbout_me(), user.getAge(), user.getLooking_for(), user.getTravel_with(), user.getLocation(), user.getNationality(), user.getLang(), user.getHeight(), user.getBody_type(), user.getEyes(), user.getHair(), user.getVisit(), "", "", "default");


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void getAllTrips(String id) {
        TripsInstance.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    TripData tripData = dataSnapshot1.getValue(TripData.class);
                    planTripsList.add(tripData);
                }

                if (planTripsList.size() <= 0)
                    cardTrip.setVisibility(View.GONE);

                PlanTripsAdapter planTripsAdapter = new PlanTripsAdapter(ProfileActivity.this, planTripsList);
                rvTripValue.setAdapter(planTripsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void getAllImages(String uid, String gender) {

        PhotoRequestInstance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                upload1 = new ArrayList<>();
                upload2 = new ArrayList<>();
                upload3 = new ArrayList<>();
                uploads = new ArrayList<>();

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        Permit permit = ds.getValue(Permit.class);
                        if (permit != null)
                            if (permit.getSender().equals(fuser.getUid()) && permit.getReceiver().equals(uid)) {
                                privateValue = 1;
                                if (permit.getStatus() == 1) {
                                    PicturesInstance.child(uid).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                Upload upload = postSnapshot.getValue(Upload.class);
                                                if (Objects.requireNonNull(upload).getType() == 1) {
                                                    upload1.add(upload);
                                                } else if (upload.getType() == 2) {
                                                    upload2.add(upload);
                                                } else if (upload.getType() == 3) {
                                                    upload3.add(upload);
                                                }
                                            }

                                            if (upload1.size() > 0) {
                                                uploads.addAll(upload1);
                                            }

                                            if (upload2.size() > 0) {
                                                uploads.addAll(upload2);
                                            }

                                            if (upload3.size() > 0) {
                                                uploads.addAll(upload3);
                                            }

//                                        privateValue = 1;

                                            Log.i(TAG, "onDataChange: " + uploads.size());
                                            if (uploads.size() > 0) {
                                                adapter = new CustomAdapter(ProfileActivity.this, uid, uploads, gender);
                                                viewPager.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();

                                                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                                    @Override
                                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                                        int i = position + 1;
                                                        tvCount.setText(i + " / " + uploads.size());
                                                        tvGenderValue.setText(gender);
                                                    }

                                                    @Override
                                                    public void onPageSelected(int position) {

                                                    }

                                                    @Override
                                                    public void onPageScrollStateChanged(int state) {

                                                    }
                                                });
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    PicturesInstance.child(uid).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            upload1 = new ArrayList<>();
                                            upload2 = new ArrayList<>();
                                            uploads = new ArrayList<>();

                                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                Upload upload = postSnapshot.getValue(Upload.class);
                                                if (Objects.requireNonNull(upload).getType() == 1) {
                                                    upload1.add(upload);
                                                } else if (upload.getType() == 2) {
                                                    upload2.add(upload);
                                                }
                                            }

                                            if (upload1.size() > 0) {
                                                uploads.addAll(upload1);
                                            }

                                            if (upload2.size() > 0) {
                                                uploads.addAll(upload2);
                                            }

//                                        privateValue = 0;

                                            Log.i(TAG, "onDataChange: " + uploads.size());
                                            if (uploads.size() > 0) {
                                                adapter = new CustomAdapter(ProfileActivity.this, uid, uploads, gender);
                                                viewPager.setAdapter(adapter);

                                                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                                    @Override
                                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                                        int i = position + 1;
                                                        tvCount.setText(i + " / " + uploads.size());
                                                    }

                                                    @Override
                                                    public void onPageSelected(int position) {

                                                    }

                                                    @Override
                                                    public void onPageScrollStateChanged(int state) {

                                                    }
                                                });
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            } else {
                                PicturesInstance.child(uid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        upload1 = new ArrayList<>();
                                        upload2 = new ArrayList<>();
                                        uploads = new ArrayList<>();

                                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                            Upload upload = postSnapshot.getValue(Upload.class);
                                            if (Objects.requireNonNull(upload).getType() == 1) {
                                                upload1.add(upload);
                                            } else if (upload.getType() == 2) {
                                                upload2.add(upload);
                                            }
                                        }

                                        if (upload1.size() > 0) {
                                            uploads.addAll(upload1);
                                        }

                                        if (upload2.size() > 0) {
                                            uploads.addAll(upload2);
                                        }

//                                        privateValue = 0;

                                        Log.i(TAG, "onDataChange: " + uploads.size());
                                        if (uploads.size() > 0) {
                                            adapter = new CustomAdapter(ProfileActivity.this, uid, uploads, gender);
                                            viewPager.setAdapter(adapter);

                                            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                                @Override
                                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                                    int i = position + 1;
                                                    tvCount.setText(i + " / " + uploads.size());
                                                }

                                                @Override
                                                public void onPageSelected(int position) {

                                                }

                                                @Override
                                                public void onPageScrollStateChanged(int state) {

                                                }
                                            });
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        if (permit.getReceiver().equals(uid)) {
                            break;
                        }
                    }

                } else {
                    PicturesInstance.child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            upload1 = new ArrayList<>();
                            upload2 = new ArrayList<>();
                            uploads = new ArrayList<>();

                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Upload upload = postSnapshot.getValue(Upload.class);
                                if (Objects.requireNonNull(upload).getType() == 1) {
                                    upload1.add(upload);
                                } else if (upload.getType() == 2) {
                                    upload2.add(upload);
                                }
                            }

                            if (upload1.size() > 0) {
                                uploads.addAll(upload1);
                            }

                            if (upload2.size() > 0) {
                                uploads.addAll(upload2);
                            }

//                            privateValue = 0;

                            Log.i(TAG, "onDataChange: " + uploads.size());
                            if (uploads.size() > 0) {
                                adapter = new CustomAdapter(ProfileActivity.this, uid, uploads, gender);
                                viewPager.setAdapter(adapter);

                                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                        int i = position + 1;
                                        tvCount.setText(i + " / " + uploads.size());
                                    }

                                    @Override
                                    public void onPageSelected(int position) {

                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int state) {

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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void showMenu(View view) {
        PopupMenu popup = new PopupMenu(ProfileActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

        MenuItem bedMenuItem = popup.getMenu().findItem(R.id.one);
        if (account_type == 1) {
            bedMenuItem.setTitle("Hide profile");
            account_type = 2;
        } else {
            bedMenuItem.setTitle("Unhide profile");
            account_type = 1;
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.one) {
                    if (account_type == 2) {
                        alertDialogHideProfile();
                    } else {
                        alertDialogHideProfile2();
                    }

                    return true;
                }
                if (id == R.id.two) {
                    alertDialogAccountRemove();

                    return true;
                }

                return true;
            }
        });
        popup.show();
    }

    private void alertDialogAccountRemove() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure you want to remove your account?");
        dialog.setTitle("Account removal");
        dialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        active_hide_delete_Profile(fuser.getUid(), 3);
                        fuser.delete();

                        snackBar(clMain, "Account remove successfully");
                        FirebaseAuth.getInstance().signOut();
                        LoginManager.getInstance().logOut();
                        finish();
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    }
                });
        dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                snackBar(clMain, "cancel is clicked");
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void alertDialogHideProfile() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure you want to hide your profile? Users won't be able to find you through the site in any way."
        );
        dialog.setTitle("Profile visibility");
        dialog.setPositiveButton("Hide My Profile",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        active_hide_delete_Profile(fuser.getUid(), account_type);
                        snackBar(clMain, "Hide My Profile is clicked");
                    }
                });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void alertDialogHideProfile2() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Your profile is hidden. Do you want to make it public again?"
        );
        dialog.setTitle("Profile visibility");
        dialog.setPositiveButton("Yes ,make it public...",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        active_hide_delete_Profile(fuser.getUid(), account_type);
                        snackBar(clMain, "unhide My Profile is clicked");
                    }
                });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }


    private void alertDialogRequestPermission(String fusername) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Do you want to request permission to see private photo?");
        dialog.setTitle("Request Permission");
        dialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (tripL != null) {
                            PhotoRequestInstance.push().setValue(new Permit(fuser.getUid(), tripL.getUser().getId(), 0, false, false));
                            notify = true;
                            if (notify) {
                                sendNotifiaction(fuser.getUid(), tripL.getUser().getId(), fusername , "has requested for private photo","PhotoRequest");
                            }
                            notify=false;
                        } else if (userL != null) {
                            PhotoRequestInstance.push().setValue(new Permit(fuser.getUid(), userL.getUser().getId(), 0, false, false));
                            notify = true;
                            if (notify) {
                                sendNotifiaction(fuser.getUid(), userL.getUser().getId(), fusername , "has requested for private photo","PhotoRequest");
                            }
                            notify=false;
                        }

                        alertDialogRP();
                    }
                });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                snackBar(clMain, "cancel is clicked");
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void alertDialogAlreadyRequest() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Already requested");
        dialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {

                    }
                });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void alertDialogRP() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Request permission..!");
        dialog.setPositiveButton("Request sent",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {

                    }
                });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    @OnClick({R.id.textProfile, R.id.iv_edit_profile, R.id.floatingActionButton2, R.id.iv_fav_user, R.id.fab_backFromProfile, R.id.iv_menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.textProfile:
                if (textProfile.getText().toString().equalsIgnoreCase("Request Private photos") && privateValue == 1) {
                    alertDialogAlreadyRequest();
                } else if (textProfile.getText().toString().equalsIgnoreCase("Request Private photos")) {
                    alertDialogRequestPermission(fusername);
                } else {
                    startActivity(new Intent(this, EditPhotoActivity.class));
                }
                break;

            case R.id.iv_edit_profile:
                startActivity(new Intent(this, EditProfileActivity.class));
                break;

            case R.id.floatingActionButton2:
                Intent intent = new Intent(this, MessageActivity.class);
                intent.putExtra("userid", profileId);
                startActivity(intent);

                break;

            case R.id.fab_backFromProfile:
                finish();
                break;

            case R.id.iv_fav_user:
                if (tripL != null) {
                    if (tripL.getUserImg().getFav() == 1) {
                        removeFav(fuser.getUid(), tripL.getUser().getId());
                        tripL.getUserImg().setFav(0);
                        ivFavUser.setImageResource(R.drawable.ic_action_fav_add);
                    } else {
                        notify = true;
                        setFav(fuser.getUid(), tripL.getUser().getId());
                        tripL.getUserImg().setFav(1);
                        ivFavUser.setImageResource(R.drawable.ic_action_fav_remove);
                        if (notify) {
                            sendNotifiaction(fuser.getUid(), tripL.getUser().getId(), fusername, "has added you to Favourite","Favourite");
                        }
                        notify = false;
                    }
                } else if (userL != null) {
                    if (userL.getFav() == 1) {
                        removeFav(fuser.getUid(), userL.getUser().getId());
                        userL.setFav(0);
                        ivFavUser.setImageResource(R.drawable.ic_action_fav_add);
                    } else {
                        notify = true;
                        setFav(fuser.getUid(), userL.getUser().getId());
                        userL.setFav(1);
                        ivFavUser.setImageResource(R.drawable.ic_action_fav_remove);
                        if (notify) {
                            sendNotifiaction(fuser.getUid(), userL.getUser().getId(), fusername, "has added you to Favourite","Favourite");
                        }
                        notify = false;
                    }
                }

                break;

            case R.id.iv_menu:
                showMenu(view);
                break;

        }
    }
}
