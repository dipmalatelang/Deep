package com.travel.cotravel.fragment.account.profile.ui;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.adapter.MyAdapter;
import com.travel.cotravel.fragment.account.profile.module.Upload;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.travel.cotravel.Constants.PicturesInstance;


public class EditPhotoActivity extends BaseActivity {

    @BindView(R.id.public_recyclerView)
    RecyclerView publicRecyclerView;
    @BindView(R.id.private_recyclerView)
    RecyclerView privateRecyclerView;
    @BindView(R.id.ll_select_image)
    LinearLayout llSelectImage;
    @BindView(R.id.ll_public_photos)
    LinearLayout llPublicPhotos;
    @BindView(R.id.ll_private_photos)
    LinearLayout llPrivatePhotos;
    private FirebaseUser fuser;
    private ArrayList<Upload> public_uploads, private_uploads, upload1, upload2;
    private MyAdapter public_adapter, private_adapter;
    private Uri filePath;
    private Uri videoPath;
    private static final int PICK_IMAGE_REQUEST = 234;
    private static final int PICK_VIDEO_REQUEST = 123;

    private StorageReference storageReference;
    private String getDownloadImageUrl;
    SharedPreferences sharedPreferences;
    String gender;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);
        ButterKnife.bind(this);

        showProgressDialog();
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 3);
        publicRecyclerView.setLayoutManager(mGridLayoutManager);
        GridLayoutManager mGridLayoutManager1 = new GridLayoutManager(this, 3);
        privateRecyclerView.setLayoutManager(mGridLayoutManager1);

        sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Gender")) {
            gender = (sharedPreferences.getString("Gender", ""));

        }

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference();

        PicturesInstance.child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                dismissProgressDialog();
                upload1 = new ArrayList<>();
                upload2 = new ArrayList<>();
                public_uploads = new ArrayList<>();
                private_uploads = new ArrayList<>();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    if (Objects.requireNonNull(upload).getType() == 3) {
                        private_uploads.add(upload);
                    } else if (upload.getType() == 1) {
                        upload1.add(upload);
                    } else if (upload.getType() == 2) {
                        upload2.add(upload);
                    }

                }

                if (upload1.size() > 0) {
                    public_uploads.addAll(upload1);
                }
                if (upload2.size() > 0) {
                    public_uploads.addAll(upload2);
                }

                if (public_uploads.size() > 0) {
                    llPublicPhotos.setVisibility(View.VISIBLE);
                    public_adapter = new MyAdapter(EditPhotoActivity.this, fuser.getUid(), gender, public_uploads, new MyAdapter.PhotoInterface() {

                        @Override
                        public void setProfilePhoto(String id, String previousValue, int pos) {
                            PicturesInstance
                                    .child(fuser.getUid())
                                    .child(id).child("type").setValue(1);

                            if (!previousValue.equals("") && !previousValue.equals(id))
                                PicturesInstance.child(fuser.getUid()).child(previousValue).child("type").setValue(2);
                            Log.i(TAG, "setProfilePhoto: " + public_uploads.get(pos).getUrl());
                            profilePhotoDetails(public_uploads.get(pos).getUrl());
                        }

                        @Override
                        public void removePhoto(String id) {
                            PicturesInstance.child(fuser.getUid()).child(id).removeValue();
                        }

                        @Override
                        public void setPhotoAsPrivate(String id) {
                            PicturesInstance
                                    .child(fuser.getUid())
                                    .child(id).child("type").setValue(3);
                            public_adapter.notifyDataSetChanged();
                        }
                    });

                    publicRecyclerView.setAdapter(public_adapter);
                }
                else {
                    llPublicPhotos.setVisibility(View.GONE);
                }

                if (private_uploads.size() > 0) {
                    llPrivatePhotos.setVisibility(View.VISIBLE);
                    private_adapter = new MyAdapter(EditPhotoActivity.this, fuser.getUid(), gender, private_uploads, new MyAdapter.PhotoInterface() {

                        @Override
                        public void setProfilePhoto(String id, String previousValue, int pos) {
                            PicturesInstance
                                    .child(fuser.getUid())
                                    .child(id).child("type").setValue(1);

                            if (!previousValue.equals("") && !previousValue.equals(id))
                                PicturesInstance.child(fuser.getUid()).child(previousValue).child("type").setValue(2);
                            profilePhotoDetails(private_uploads.get(pos).getUrl());
                        }

                        @Override
                        public void removePhoto(String id) {
                            PicturesInstance.child(fuser.getUid()).child(id).removeValue();
                        }

                        @Override
                        public void setPhotoAsPrivate(String id) {
                            PicturesInstance
                                    .child(fuser.getUid())
                                    .child(id).child("type").setValue(2);
                            private_adapter.notifyDataSetChanged();
                        }
                    });
                    privateRecyclerView.setAdapter(private_adapter);
                }
                else {
                    llPrivatePhotos.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                dismissProgressDialog();
            }
        });


    }

    private void profilePhotoDetails(String imageUrl) {

        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ImageUrl", imageUrl);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.video:
                showVideoChooser();
                break;
            case R.id.gallery:
                showImageChooser();
                break;

            case R.id.facebook:

                startActivity(new Intent(this, FacebookImageActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    public void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadFile(filePath);
        }
        else if(requestCode == PICK_VIDEO_REQUEST && data!=null && data.getData() !=null)
        {
            videoPath=data.getData();
            checkDurationOfVideo(videoPath);
        }
    }

    private void checkDurationOfVideo(Uri videoPath) {
        try {
            if (videoPath != null) {
                Log.i(TAG, "checkDurationOfVideo: working");
                MediaPlayer mp = MediaPlayer.create(this, videoPath);
                int duration = mp.getDuration();
                mp.release();

                if((duration/1000) > 10){
                    // Show Your Messages
                    snackBar(llSelectImage, "Video duration is more than 10s");
                }else{
                    uploadVideo(videoPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getFileExtension(Uri uri) {
        ContentResolver cR = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadVideo(Uri videoPath){
        if (videoPath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            final StorageReference sRef = storageReference.child("uploads/" + System.currentTimeMillis() + "." + getFileExtension(videoPath));

            sRef.putFile(videoPath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();

                            snackBar(llSelectImage, "File Uploaded ");

                            String uploadId = PicturesInstance.child(fuser.getUid()).push().getKey();

                            sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        getDownloadImageUrl = Objects.requireNonNull(task.getResult()).toString();
                                        Log.i("FirebaseImages", getDownloadImageUrl);

//                                        Upload upload;
                                     /*   if (public_uploads.size() == 0) {
                                            upload = new Upload(uploadId, "Video", getDownloadImageUrl, 1);
                                        } else {*/
                                        Upload  upload = new Upload(uploadId, "Video", getDownloadImageUrl, 2);
//                                        }

                                        PicturesInstance.child(fuser.getUid()).child(Objects.requireNonNull(uploadId)).setValue(upload);
                                    } else {
                                        snackBar(llSelectImage, Objects.requireNonNull(task.getException()).getMessage());
                                    }
                                }
                            });
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(taskSnapshot -> {

                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    });
        } else {
            snackBar(llSelectImage, "Please Select a Video");
        }
    }
    private void uploadFile(Uri filePath) {

        if (filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            final StorageReference sRef = storageReference.child("uploads/" + System.currentTimeMillis() + "." + getFileExtension(filePath));

            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();

                            snackBar(llSelectImage, "File Uploaded ");

                            String uploadId = PicturesInstance.child(fuser.getUid()).push().getKey();

                            sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        getDownloadImageUrl = Objects.requireNonNull(task.getResult()).toString();
                                        Log.i("FirebaseImages", getDownloadImageUrl);

                                        Upload upload;
                                        if (public_uploads.size() == 0) {
                                            upload = new Upload(uploadId, "Image", getDownloadImageUrl, 1);
                                        } else {
                                            upload = new Upload(uploadId, "Image", getDownloadImageUrl, 2);
                                        }


                                        PicturesInstance.child(fuser.getUid()).child(Objects.requireNonNull(uploadId)).setValue(upload);
                                    } else {
                                        snackBar(llSelectImage, Objects.requireNonNull(task.getException()).getMessage());
                                    }
                                }
                            });
                        }
                    })


                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(taskSnapshot -> {

                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    });
        } else {
            snackBar(llSelectImage, "Please Select a Image");
        }
    }
}
