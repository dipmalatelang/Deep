package com.travel.cotravel.fragment.account.profile.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.module.Permit;
import com.travel.cotravel.fragment.account.profile.verify.ViewPhotoRequestAdapter;
import com.travel.cotravel.fragment.visitor.UserImg;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.travel.cotravel.Constants.PhotoRequestInstance;


public class ViewPhotoRequestActivity extends BaseActivity {
    @BindView(R.id.rv_view_photo_request)
    RecyclerView rvViewPhotoRequest;
    @BindView(R.id.txtNoData)
    TextView txtNoData;
    private FirebaseUser fuser;
    ViewPhotoRequestAdapter viewPhotoRequestAdapter;
    ValueEventListener requestSeenListener, photoRequestListener, removePhotoRequestListener;
    boolean notify = false;
    String fusername;
    SharedPreferences sharedPreferences;
    AdView mAdmobView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo_request);
        ButterKnife.bind(this);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("Name")) {
            fusername = (sharedPreferences.getString("Name", ""));
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvViewPhotoRequest.setLayoutManager(linearLayoutManager);

        int type = getIntent().getIntExtra("intentType", 0);
        ArrayList<UserImg> userList = new Gson().fromJson(getIntent().getStringExtra("userList"), new TypeToken<ArrayList<UserImg>>() {
        }.getType());

        viewPhotoRequestAdapter = new ViewPhotoRequestAdapter(this, userList, type, new ViewPhotoRequestAdapter.ViewPhotoRequestInterface() {
            @Override
            public void seenRequest(String id) {
                requestSeen(id, type);
            }

            @Override
            public void acceptRequest(String id, int pos) {
                acceptPhotoRequest(id, 1);
                notify = true;
                if (notify) {
                    sendNotifiaction(fuser.getUid(), id, fusername, "has accepted your photo request","GivenPermit");
                }
                notify=false;
                Objects.requireNonNull(userList).remove(pos);
                viewPhotoRequestAdapter.notifyDataSetChanged();
                snackBar(rvViewPhotoRequest, "Accept");
                if (userList.size() <= 0) {
                    txtNoData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void denyRequest(String id, int pos) {
                acceptPhotoRequest(id, 2);
                Objects.requireNonNull(userList).remove(pos);
                viewPhotoRequestAdapter.notifyDataSetChanged();
                snackBar(rvViewPhotoRequest, "Deny");
                if (userList.size() <= 0) {
                    txtNoData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void hidePhotoRequest(String id, int pos) {
                removePhotoRequest(id);
                Objects.requireNonNull(userList).remove(pos);
                viewPhotoRequestAdapter.notifyDataSetChanged();
                if (userList.size() <= 0) {
                    txtNoData.setVisibility(View.VISIBLE);
                }

            }
        });

        rvViewPhotoRequest.setAdapter(viewPhotoRequestAdapter);
        viewPhotoRequestAdapter.notifyDataSetChanged();
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

    private void acceptPhotoRequest(String userid, int i) {
        photoRequestListener = PhotoRequestInstance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Permit permit = snapshot.getValue(Permit.class);
                    if (Objects.requireNonNull(permit).getReceiver().equals(fuser.getUid()) && permit.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status", i);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void requestSeen(String userid, int type) {
        requestSeenListener = PhotoRequestInstance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Permit permit = snapshot.getValue(Permit.class);
                    if (type == 3 || type == 1) {
                        if (Objects.requireNonNull(permit).getReceiver().equals(fuser.getUid()) && permit.getSender().equals(userid)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("receiverCheck", true);
                            snapshot.getRef().updateChildren(hashMap);
                        }
                    } else if (type == 2) {
                        if (Objects.requireNonNull(permit).getSender().equals(fuser.getUid()) && permit.getReceiver().equals(userid)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("senderCheck", true);
                            snapshot.getRef().updateChildren(hashMap);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (removePhotoRequestListener != null) {
            PhotoRequestInstance.removeEventListener(removePhotoRequestListener);
        }
        if (requestSeenListener != null) {
            PhotoRequestInstance.removeEventListener(requestSeenListener);
        }
        if (photoRequestListener != null) {
            PhotoRequestInstance.removeEventListener(photoRequestListener);
        }
    }

    private void removePhotoRequest(String userid) {
        removePhotoRequestListener = PhotoRequestInstance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Permit permit = snapshot.getValue(Permit.class);
                    if (Objects.requireNonNull(permit).getReceiver().equals(fuser.getUid()) && permit.getSender().equals(userid)) {

                        snapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
