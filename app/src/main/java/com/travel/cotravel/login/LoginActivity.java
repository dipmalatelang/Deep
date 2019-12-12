package com.travel.cotravel.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.module.Upload;
import com.travel.cotravel.fragment.account.profile.verify.EditPhoneActivity;
import com.travel.cotravel.fragment.trip.module.User;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.travel.cotravel.Constants.PicturesInstance;
import static com.travel.cotravel.Constants.UsersInstance;


//import com.example.tgapplication.trips.TripActivity;

public class LoginActivity extends BaseActivity implements  View.OnKeyListener {

    @BindView(R.id.login_button)
    LoginButton loginButton;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.constrainlayout)
    ConstraintLayout constrainlayout;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.link_signup)
    TextView linkSignup;
    @BindView(R.id.input_email)
    EditText inputEmail;
    @BindView(R.id.input_password)
    EditText inputPassword;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    String value;
    boolean notify = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

     /*   SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        // Check if we need to display our OnboardingFragment
        if (!sharedPreferences.getBoolean("UserFirst", false)) {
            // The user hasn't seen the OnboardingFragment yet, so show it
        *//*    notify = true;
            if (notify) {
                sendNotifiaction(fuser.getUid(), tripL.getUser().getId(), fusername , "has requested for private photo");
            }
            notify=false;*//*
        }
*/

        tvRegister.setPaintFlags(tvRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvRegister.setText(getResources().getString(R.string.register));


        inputPassword.setOnTouchListener((view, motionEvent) -> showOrHidePwd(motionEvent, inputPassword));
        inputPassword.setOnKeyListener(this);

//        value = getIntent().getExtras().getString("nextActivity");





//        loginButton.setReadPermissions("email", "public_profile");
//        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.d("Tiger", "facebook:onSuccess:" + loginResult);
//                Log.d("Tiger", "facebook:token:" + loginResult.getAccessToken());
//                handleFacebookAccessToken(loginResult.getAccessToken().getToken());
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d("Tiger", "facebook:onCancel");
//                // ...
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.d("Tiger", "facebook:onError", error);
//                // ...
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(this).edit();
        sharedPreferencesEditor.putBoolean(
                "UserFirst", true);
        sharedPreferencesEditor.apply();
    }

    private void handleFacebookAccessToken(String token) {
        showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        Log.d("Tiger", "" + credential);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Tiger", "handleFacebookAccessToken:" + task.isSuccessful());
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Tiger", "signInWithCredential:success");
                            dismissProgressDialog();

                            UsersInstance.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        registerFromLogin();
                                    } else {
                                        updateUI(mAuth.getCurrentUser());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }

                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            retrieveUserDetail(currentUser);

        }
    }

    private void registerFromLogin() {

        FirebaseUser user = mAuth.getCurrentUser();
        ArrayList<String> travel_with = new ArrayList<>();
        ArrayList<String> looking_for = new ArrayList<>();
        ArrayList<String> range_age = new ArrayList<>();

        travel_with.add("Female");
        travel_with.add("Male");

        range_age.add("18");
        range_age.add("55");

        User userClass = new User(Objects.requireNonNull(user).getUid(), user.getDisplayName(), "offline", Objects.requireNonNull(user.getDisplayName()).toLowerCase(), "", "18", user.getEmail(), user.getProviderId(), "", "", "", "", "", "", travel_with, looking_for, range_age, "", user.getDisplayName().toLowerCase(), user.getPhoneNumber(), "", "", "", 1, false, "");
        UsersInstance.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(userClass);

        String uploadId = PicturesInstance.child(user.getUid()).push().getKey();
        PicturesInstance.child(user.getUid()).child(Objects.requireNonNull(uploadId)).setValue(new Upload(uploadId, "Image", Objects.requireNonNull(user.getPhotoUrl()).toString() + "?type=large", 1));

        if (mAuth != null)
            updateUI(mAuth.getCurrentUser());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void phoneLogin() {

    }


    private void emailLogin(String txt_email, String txt_password) {
        mAuth.signInWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dismissProgressDialog();
                            updateUI(mAuth.getCurrentUser());

                            Log.d(TAG, "onComplete: " + txt_email + " " + txt_password);
                            saveLoginDetails(txt_email, txt_password);

//                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
//                                        finish();
//

                        } else {
                            snackBar(constrainlayout, Objects.requireNonNull(task.getException()).getMessage());
                            dismissProgressDialog();
                        }
                    }
                });
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            btnLogin.performClick();
            return true;
        }
        return false;
    }

    @OnClick({R.id.btn_login, R.id.btn_phone_login, R.id.link_signup, R.id.tv_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_register:
                //                Log.i("Send while Login", value);
                Intent loginIntent = new Intent(LoginActivity.this, RegisterActivity.class);
//                loginIntent.putExtra("nextActivity", value);
                startActivity(loginIntent);

                break;
            case R.id.link_signup:
                Intent resetIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                resetIntent.putExtra("nextActivity", value);
                startActivity(resetIntent);
                break;


            case R.id.btn_login:
                showProgressDialog();
                String txt_email = inputEmail.getText().toString().trim();
                String txt_password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    dismissProgressDialog();
                    snackBar(constrainlayout, "All fileds are required !");

                } else if (!Patterns.EMAIL_ADDRESS.matcher(txt_email).matches()) {
                    dismissProgressDialog();
                    snackBar(constrainlayout, "please enter valid email address");
                } else {
                    Log.d(TAG, "onComplete1: " + txt_email + " " + txt_password);
//                    hideKeyboard(this);
                    emailLogin(txt_email, txt_password);

                }

           /*     if(CheckNetwork.isInternetAvailable(this)) //returns true if internet available
                {

                    showProgressDialog();
                }
                else
                {
                    dismissProgressDialog();
                    snackBar(constrainlayout, "no internet connection");
                }*/
                break;
            case R.id.btn_phone_login:
                startActivity(new Intent(this, EditPhoneActivity.class));
                break;
        }
    }
//    private boolean isNetworkConnected() {
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        return cm.getActiveNetworkInfo() != null;
//    }
}


