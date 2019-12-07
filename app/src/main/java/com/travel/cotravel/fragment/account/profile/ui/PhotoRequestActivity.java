package com.travel.cotravel.fragment.account.profile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.module.Permit;
import com.travel.cotravel.fragment.account.profile.module.Upload;
import com.travel.cotravel.fragment.trip.module.User;
import com.travel.cotravel.fragment.visitor.UserImg;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.travel.cotravel.Constants.FavoritesInstance;
import static com.travel.cotravel.Constants.PhotoRequestInstance;
import static com.travel.cotravel.Constants.PicturesInstance;
import static com.travel.cotravel.Constants.UsersInstance;


public class PhotoRequestActivity extends BaseActivity {
    @BindView(R.id.cl_Photo_Request)
    ConstraintLayout clPhotoRequest;
    @BindView(R.id.photo_rqst_count)
    TextView photoRqstCount;
    @BindView(R.id.photo_permits_count)
    TextView photoPermitsCount;
    @BindView(R.id.given_permits_count)
    TextView givenPermitsCount;
    private FirebaseUser fuser;
    ArrayList<UserImg> viewPhotoRequestList = new ArrayList<>();
    ArrayList<UserImg> photoPermitsList = new ArrayList<>();
    ArrayList<UserImg> givenPermitsList = new ArrayList<>();
    int prcount, ppcount, gpcount;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_requests);
        ButterKnife.bind(this);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        showProgressDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPhotoRequest();
        photoPermits();
        givenPermits();
    }

    @OnClick({R.id.btn_photo_rqst, R.id.btn_photo_permits, R.id.btn_given_permits})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_photo_rqst:
                Log.i(TAG, "onViewClicked: "+viewPhotoRequestList.size());
                if (viewPhotoRequestList.size() > 0) {
                    photoRqstCount.setVisibility(View.GONE);
                    Intent intent1 = new Intent(PhotoRequestActivity.this, ViewPhotoRequestActivity.class);
                    intent1.putExtra("intentType", 1);
                    intent1.putExtra("userList", new Gson().toJson(viewPhotoRequestList));
                    startActivity(intent1);
                } else {
                    snackBar(clPhotoRequest, "No Data");
                }
                break;
            case R.id.btn_photo_permits:
                Log.i(TAG, "onViewClicked: "+photoPermitsList.size());

                if (photoPermitsList.size() > 0) {
                    photoPermitsCount.setVisibility(View.GONE);
                    Intent intent2 = new Intent(PhotoRequestActivity.this, ViewPhotoRequestActivity.class);
                    intent2.putExtra("intentType", 2);
                    intent2.putExtra("userList", new Gson().toJson(photoPermitsList));
                    startActivity(intent2);
                } else {
                    snackBar(clPhotoRequest, "No Data");
                }
                break;
            case R.id.btn_given_permits:
                Log.i(TAG, "onViewClicked: "+givenPermitsList.size());

                if (givenPermitsList.size() > 0) {
                    givenPermitsCount.setVisibility(View.GONE);
                    Intent intent3 = new Intent(PhotoRequestActivity.this, ViewPhotoRequestActivity.class);
                    intent3.putExtra("intentType", 3);
                    intent3.putExtra("userList", new Gson().toJson(givenPermitsList));
                    startActivity(intent3);
                } else {
                    snackBar(clPhotoRequest, "No Data");
                }
                break;
        }
    }

    private void viewPhotoRequest() {
        PhotoRequestInstance.orderByChild("receiver").equalTo(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                viewPhotoRequestList=new ArrayList<>();
                prcount=0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Permit permit = ds.getValue(Permit.class);

                    if (Objects.requireNonNull(permit).getStatus() == 0) {
                        if(!permit.isReceiverCheck())
                        {
                            prcount++;
                        }
                        UsersInstance.child(permit.getSender()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                User user = dataSnapshot.getValue(User.class);
                                if(user!=null)
                                {
                                    UserImg userImg=new UserImg(user,"",0);

                                    PicturesInstance.child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                                                Upload mainPhoto = snapshot1.getValue(Upload.class);
                                                if (Objects.requireNonNull(mainPhoto).type == 1)
                                                    userImg.setPictureUrl(mainPhoto.getUrl());

                                            }
                                            FavoritesInstance.child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    if (snapshot.hasChild(user.getId())) {
                                                        userImg.setFav(1);
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
                                    viewPhotoRequestList.add(userImg);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                        if(prcount>0)
                        {
                            Log.i(TAG, "onCompleted: viewPhotoRequestList " + prcount);
                            photoRqstCount.setVisibility(View.VISIBLE);
                            photoRqstCount.setText(String.valueOf(prcount));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void photoPermits() {
        PhotoRequestInstance.orderByChild("sender").equalTo(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                photoPermitsList=new ArrayList<>();
                ppcount=0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i(TAG, "onDataChange: "+dataSnapshot.getChildrenCount());
                    Permit permit = ds.getValue(Permit.class);

                    if (Objects.requireNonNull(permit).getStatus() == 1) {
                        if(!permit.isSenderCheck())
                        {
                            ppcount++;
                        }

                        UsersInstance.child(permit.getReceiver()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                if(user!=null)
                                {
                                    UserImg userImg=new UserImg(user,"",0);
                                    PicturesInstance.child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                                                Upload mainPhoto = snapshot1.getValue(Upload.class);
                                                if (Objects.requireNonNull(mainPhoto).type == 1)
                                                    userImg.setPictureUrl(mainPhoto.getUrl());

                                            }
                                            FavoritesInstance.child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    if (snapshot.hasChild(user.getId())) {
                                                        userImg.setFav(1);
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
                                    photoPermitsList.add(userImg);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        if(ppcount>0)
                        {
                            Log.i(TAG, "onCompleted: photoPermitsList " + ppcount);
                            photoPermitsCount.setVisibility(View.VISIBLE);
                            photoPermitsCount.setText(String.valueOf(ppcount));
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void givenPermits() {
        PhotoRequestInstance.orderByChild("receiver").equalTo(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                givenPermitsList=new ArrayList<>();
                gpcount=0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Permit permit = ds.getValue(Permit.class);
                    if (Objects.requireNonNull(permit).getStatus() == 1) {
                        if(!permit.isReceiverCheck())
                        {
                            gpcount++;
                        }
                        UsersInstance.child(permit.getSender()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                if(user!=null)
                                {
                                    UserImg userImg=new UserImg(user,"",0);
                                    PicturesInstance.child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                                                Upload mainPhoto = snapshot1.getValue(Upload.class);
                                                if (Objects.requireNonNull(mainPhoto).type == 1)
                                                   userImg.setPictureUrl(mainPhoto.getUrl());

                                            }
                                            FavoritesInstance.child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    if (snapshot.hasChild(user.getId())) {
                                                        userImg.setFav(1);
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
                                    givenPermitsList.add(userImg);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        if(gpcount>0)
                        {
                            Log.i(TAG, "onCompleted: givenPermitsList " + gpcount);
                            givenPermitsCount.setVisibility(View.VISIBLE);
                            givenPermitsCount.setText(String.valueOf(gpcount));
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dismissProgressDialog();
    }


}
