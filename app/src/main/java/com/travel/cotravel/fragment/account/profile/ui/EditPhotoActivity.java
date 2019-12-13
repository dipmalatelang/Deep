package com.travel.cotravel.fragment.account.profile.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.UploadService;
import com.travel.cotravel.fragment.account.profile.adapter.MyAdapter;
import com.travel.cotravel.fragment.account.profile.module.Upload;
import com.travel.cotravel.fragment.account.profile.videoTrimmer.videoTrimmer.VideoTrimmerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.travel.cotravel.Constants.PicturesInstance;
import static com.travel.cotravel.Constants.STORAGE_PERMISSION_CODE;


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
    private static final int VIDEO_TRIM=345;

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
                        public void playVideo(String url) {

                            Intent intent = new Intent(EditPhotoActivity.this, ViewVideoActivity.class);
                            intent.putExtra("VideoUrl", url);
                            startActivity(intent);
                        }

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
                } else {
                    llPublicPhotos.setVisibility(View.GONE);
                }

                if (private_uploads.size() > 0) {
                    llPrivatePhotos.setVisibility(View.VISIBLE);
                    private_adapter = new MyAdapter(EditPhotoActivity.this, fuser.getUid(), gender, private_uploads, new MyAdapter.PhotoInterface() {

                        @Override
                        public void playVideo(String url) {

                        }

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
                } else {
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
            uploadImg(filePath);
        } else if (requestCode == PICK_VIDEO_REQUEST && data != null && data.getData() != null) {
            videoPath = data.getData();
            checkDurationOfVideo(videoPath);
        }
        else if (requestCode == VIDEO_TRIM) {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String videoPath = data.getExtras().getString("INTENT_VIDEO_FILE");
                        uploadVideo(Uri.parse(videoPath));
                        Toast.makeText(EditPhotoActivity.this, "Video stored at " + videoPath, Toast.LENGTH_LONG).show();
                    }
                }
        }
    }

    private void checkDurationOfVideo(Uri videoPath) {
        try {
            if (videoPath != null) {
                Log.i(TAG, "checkDurationOfVideo: working");
                MediaPlayer mp = MediaPlayer.create(this, videoPath);
                int duration = mp.getDuration();
                mp.release();

                if ((duration / 1000) > 10) {
                    // Show Your Messages
                    snackBar(llSelectImage, "Video duration is more than 10s");
                    if(isPermissionGranted(EditPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    {
                        startActivityForResult(new Intent(EditPhotoActivity.this, VideoTrimmerActivity.class).putExtra("EXTRA_PATH", getPath(videoPath)), VIDEO_TRIM);
//                        overridePendingTransition(0,0);
                    }
                    else {
                        requestForPermission(EditPhotoActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);
                    }

                } else {
                    uploadVideo(videoPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPath(Uri uri) {
//        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

     /*   // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(this, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(this, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else*/ if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.i(TAG, "getPath: content");
            return getDataColumn(this, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.i(TAG, "getPath: file");
            return uri.getPath();
        }

        return "";
    }

/*    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }*/

    private String getDataColumn(Context context, Uri uri, String selection,
                                 String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            int currentApiVersion = Build.VERSION.SDK_INT;
            //TODO changes to solve gallery video issue
            if (currentApiVersion > Build.VERSION_CODES.M && uri.toString().contains(getString(R.string.app_provider))) {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (cursor.getString(column_index) != null) {
                        String state = Environment.getExternalStorageState();
                        File file;
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", cursor.getString(column_index));
                        } else {
                            file = new File(context.getFilesDir(), cursor.getString(column_index));
                        }
                        return file.getAbsolutePath();
                    }
                    return "";
                }
            } else {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    if (cursor.getString(column_index) != null) {
                        return cursor.getString(column_index);
                    }
                    return "";
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return "";
    }

    private void uploadVideo(Uri videoPath) {
        if (videoPath != null) {

            startService(new Intent(this, UploadService.class)
                    .putExtra(UploadService.EXTRA_VIDEO_URI, videoPath)
                    .setAction(UploadService.ACTION_UPLOAD_VIDEO));
        }
    }

    private void uploadImg(Uri filePath) {

        if (filePath != null) {
            startService(new Intent(this, UploadService.class)
                    .putExtra(UploadService.EXTRA_IMG_URI, filePath)
                    .setAction(UploadService.ACTION_UPLOAD_IMG));
        }
    }

}
