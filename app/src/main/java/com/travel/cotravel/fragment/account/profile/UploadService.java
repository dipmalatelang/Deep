package com.travel.cotravel.fragment.account.profile;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.module.Upload;
import com.travel.cotravel.fragment.account.profile.ui.EditPhotoActivity;

import java.util.Objects;

import static com.travel.cotravel.Constants.PicturesInstance;


public class UploadService extends BaseTaskService {

    public static final String ACTION_UPLOAD_IMG = "action_upload_img";
    public static final String ACTION_UPLOAD_VIDEO = "action_upload_video";
    private StorageReference storageReference;
    private FirebaseUser fuser;
    public static final String EXTRA_IMG_URI = "extra_img_uri";
    public static final String EXTRA_VIDEO_URI = "extra_video_uri";
    public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";

    public static final String UPLOAD_COMPLETED = "upload_completed";
    public static final String UPLOAD_ERROR = "upload_error";

    @Override
    public void onCreate() {
        super.onCreate();

        storageReference = FirebaseStorage.getInstance().getReference();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_UPLOAD_VIDEO.equals(intent.getAction())) {
            Uri fileUri = intent.getParcelableExtra(EXTRA_VIDEO_URI);
            uploadVideoFromUri(fileUri);
        }
        else if(ACTION_UPLOAD_IMG.equals(intent.getAction())) {
            Uri fileUri = intent.getParcelableExtra(EXTRA_IMG_URI);
            uploadImgFromUri(fileUri);
        }
        return START_REDELIVER_INTENT;
    }

    private void uploadImgFromUri(Uri fileUri)   {
        taskStarted();
        showProgressNotification(getString(R.string.upload), 0, 0);

        final StorageReference sRef = storageReference.child("uploads/" + System.currentTimeMillis() + "." + getFileExtension(fileUri));

        sRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                        String uploadId = PicturesInstance.child(fuser.getUid()).push().getKey();

                        sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String getDownloadImageUrl = Objects.requireNonNull(task.getResult()).toString();
                                    Log.i("FirebaseImages", getDownloadImageUrl);

                                    Upload upload;
                              /*      if (public_uploads.size() == 0) {
                                        upload = new Upload(uploadId, "Image", getDownloadImageUrl, 1);
                                    } else {*/
                                        upload = new Upload(uploadId, "Image", getDownloadImageUrl, 2);
//                                    }


                                    PicturesInstance.child(fuser.getUid()).child(Objects.requireNonNull(uploadId)).setValue(upload);


                                    broadcastImgUploadFinished(task.getResult(), fileUri);
                                    showImgUploadFinishedNotification(task.getResult(), fileUri);
                                    taskCompleted();
                                }
                            }
                        });
                    }
                })


                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        broadcastImgUploadFinished(null, fileUri);
                        showImgUploadFinishedNotification(null, fileUri);
                        taskCompleted();
                    }
                })
                .addOnProgressListener(taskSnapshot -> {

                    showProgressNotification(getString(R.string.upload)+ taskSnapshot.getBytesTransferred()+" of "+taskSnapshot.getTotalByteCount(),
                            taskSnapshot.getBytesTransferred(),
                            taskSnapshot.getTotalByteCount());
                });
    }

    private void uploadVideoFromUri(Uri fileUri)
    {
        taskStarted();
        showProgressNotification(getString(R.string.upload), 0, 0);

        Log.i("TAG", "uploadVideoFromUri: "+fileUri);
        final StorageReference sRef = storageReference.child("uploads/" + System.currentTimeMillis() + "." + getFileExtension(fileUri));

        sRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                        String uploadId = PicturesInstance.child(fuser.getUid()).push().getKey();

                        sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                   String getDownloadImageUrl = Objects.requireNonNull(task.getResult()).toString();
                                    Log.i("FirebaseImages", getDownloadImageUrl);

                                    Upload upload = new Upload(uploadId, "Video", getDownloadImageUrl, 2);

                                    PicturesInstance.child(fuser.getUid()).child(Objects.requireNonNull(uploadId)).setValue(upload);

                                    broadcastVideoUploadFinished(task.getResult(), fileUri);
                                    showVideoUploadFinishedNotification(task.getResult(), fileUri);
                                    taskCompleted();
                                }
                            }
                        });
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.i("TAG", "onFailure: "+exception.getMessage());
                        broadcastVideoUploadFinished(null, fileUri);
                        showVideoUploadFinishedNotification(null, fileUri);
                        taskCompleted();
                    }
                })
                .addOnProgressListener(taskSnapshot -> {
                    showProgressNotification(getString(R.string.upload)+ taskSnapshot.getBytesTransferred()+" of "+taskSnapshot.getTotalByteCount(),
                            taskSnapshot.getBytesTransferred(),
                            taskSnapshot.getTotalByteCount());
                });
    }

    private boolean broadcastImgUploadFinished(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        boolean success = downloadUrl != null;

        String action = success ? UPLOAD_COMPLETED : UPLOAD_ERROR;

        Intent broadcast = new Intent(action)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_IMG_URI, fileUri);
        return LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(broadcast);
    }

    private boolean broadcastVideoUploadFinished(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        boolean success = downloadUrl != null;

        String action = success ? UPLOAD_COMPLETED : UPLOAD_ERROR;

        Intent broadcast = new Intent(action)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_VIDEO_URI, fileUri);
        return LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(broadcast);
    }

    private void showImgUploadFinishedNotification(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        // Hide the progress notification
        dismissProgressNotification();

        // Make Intent to MainActivity
        Intent intent = new Intent(this, EditPhotoActivity.class)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_IMG_URI, fileUri)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean success = downloadUrl != null;
        String caption = success ? "Upload finished" : "Upload failed";
        showFinishedNotification(caption, intent, success);
    }


    private void showVideoUploadFinishedNotification(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        // Hide the progress notification
        dismissProgressNotification();

        // Make Intent to MainActivity
        Intent intent = new Intent(this, EditPhotoActivity.class)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_VIDEO_URI, fileUri)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean success = downloadUrl != null;
        String caption = success ? "Upload finished" : "Upload failed";
        showFinishedNotification(caption, intent, success);
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
