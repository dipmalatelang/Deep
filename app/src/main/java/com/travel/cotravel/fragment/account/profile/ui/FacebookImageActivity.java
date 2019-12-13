package com.travel.cotravel.fragment.account.profile.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.adapter.DetailFBAdapter;
import com.travel.cotravel.fragment.account.profile.adapter.FB_Adapter;
import com.travel.cotravel.fragment.account.profile.module.Upload;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.travel.cotravel.Constants.PicturesInstance;


public class FacebookImageActivity extends BaseActivity {

    @BindView(R.id.fb_recyclerview)
    RecyclerView fbRecyclerview;
    @BindView(R.id.login_button)
    LoginButton loginButton;
    private ArrayList<FbImage> lstFBImages;
    private ArrayList<Images> photoAlbums = new ArrayList<>();
    FB_Adapter fb_adapter;
    private FirebaseUser fuser;
    private CallbackManager mCallbackManager;
    String gender;
    SharedPreferences sharedPreferences;
    String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_facebook_image);
        ButterKnife.bind(this);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        mCallbackManager = CallbackManager.Factory.create();

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 3);
        fbRecyclerview.setLayoutManager(mGridLayoutManager);

        sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Gender")) {
            gender = (sharedPreferences.getString("Gender", ""));

        }

        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                new GraphRequest(
                        loginResult.getAccessToken(),
                        "/" + AccessToken.getCurrentAccessToken().getUserId() + "/albums",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    if (response.getError() == null) {
                                        JSONObject joMain = response.getJSONObject();
                                        if (joMain.has("data")) {
                                            JSONArray jaData = joMain.optJSONArray("data");
                                            for (int i = 0; i < jaData.length(); i++) {
                                                JSONObject joAlbum = jaData.getJSONObject(i);
                                                GetFacebookImages(joAlbum.optString("id"), joAlbum.optString("name"));
                                            }
                                        }
                                    } else {
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        getAlbum();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void getAlbum() {

        if (AccessToken.getCurrentAccessToken() != null) {

            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + AccessToken.getCurrentAccessToken().getUserId() + "/albums",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try {
                                if (response.getError() == null) {
                                    JSONObject joMain = response.getJSONObject();
                                    if (joMain.has("data")) {
                                        JSONArray jaData = joMain.optJSONArray("data");
                                        for (int i = 0; i < jaData.length(); i++) {
                                            JSONObject joAlbum = jaData.getJSONObject(i);
                                            GetFacebookImages(joAlbum.optString("id"), joAlbum.optString("name"));

                                        }
                                    }
                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();

        } else {
            loginButton.performClick();
        }
    }


    public void GetFacebookImages(String albumId, String name) {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "images");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + albumId + "/photos",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        try {
                            if (response.getError() == null) {

                                JSONObject joMain = response.getJSONObject();
                                if (joMain.has("data")) {
                                    JSONArray jaData = joMain.optJSONArray("data");
                                    lstFBImages = new ArrayList<>();
                                    for (int i = 0; i < Objects.requireNonNull(jaData).length(); i++)//Get no. of images
                                    {
                                        JSONObject joAlbum = jaData.getJSONObject(i);
                                        JSONArray jaImages = joAlbum.getJSONArray("images");

                                        if (jaImages.length() > 0) {

                                            lstFBImages.add(new FbImage(jaImages.getJSONObject(0).getString("source"), 0));//lstFBImages is Images object array
                                        }
                                    }

                                    if (lstFBImages.size() > 0) {
                                        PicturesInstance.child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    Upload upload = ds.getValue(Upload.class);

                                                    if (upload.getName().equalsIgnoreCase("FB_Image")) {
                                                        for (int k = 0; k < lstFBImages.size(); k++) {
                                                            if (upload.getUrl().equals(lstFBImages.get(k).getUrl())) {
                                                                lstFBImages.get(k).setStatus(1);
                                                            }
                                                        }

                                                    }

                                                }
                                                photoAlbums.add(new Images(albumId, name, lstFBImages));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });

                                    }


                                }

                                Log.i(TAG, "onCompleted: photoAlbums "+photoAlbums.size());
                                fb_adapter = new FB_Adapter(FacebookImageActivity.this, fuser.getUid(), gender, photoAlbums, new FB_Adapter.FbInterface() {
                                    @Override
                                    public void proceed(ArrayList<FbImage> image_url) {

                                        DetailFBAdapter fbadapter = new DetailFBAdapter(FacebookImageActivity.this, image_url, gender, new DetailFBAdapter.DetailFbInterface() {
                                            @Override
                                            public void fetchFbImage(String imgUrl) {
                                                String uploadId = PicturesInstance.child(fuser.getUid()).push().getKey();
                                                Upload upload = new Upload(uploadId, "FB_Image", imgUrl, 2);
                                                PicturesInstance.child(fuser.getUid()).child(Objects.requireNonNull(uploadId)).setValue(upload);
                                            }
                                        });

                                        fbRecyclerview.setAdapter(fbadapter);
                                    }
                                });

                                fbRecyclerview.setAdapter(fb_adapter);

                            } else {
                                Log.v("TAG", response.getError().toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).executeAsync();
    }

    public class Images {
        String id, name;
        ArrayList<FbImage> image_Url;

        public Images(String id, String name, ArrayList<FbImage> image_Url) {
            this.id = id;
            this.name = name;
            this.image_Url = image_Url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<FbImage> getImage_Url() {
            return image_Url;
        }

        public void setImage_Url(ArrayList<FbImage> image_Url) {
            this.image_Url = image_Url;
        }
    }

    public class FbImage {
        String url;
        int status;

        public FbImage(String url, int status) {
            this.url = url;
            this.status = status;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}