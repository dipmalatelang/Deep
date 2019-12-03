package com.travel.cotravel.fragment.account.profile.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.R;
import com.travel.cotravel.login.LoginActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends BaseActivity {

    @BindView(R.id.etCurrentPassword)
    EditText etCurrentPassword;

    @BindView(R.id.etNewpassword)
    EditText etNewpassword;

    @BindView(R.id.etConfirmpassword)
    EditText etConfirmpassword;
    @BindView(R.id.btnSaveNow)
    Button btnSaveNow;

    String newPass, currentpasword, Confirmpassword;
    @BindView(R.id.cl_changepwd)
    ConstraintLayout clChangepwd;
    SharedPreferences sharedPreferences;
    String email_id, txt_password;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.home_admob)
    AdView homeAdmob;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        ButterKnife.bind(this);


        etCurrentPassword.setOnTouchListener((view, motionEvent) -> ChangeHidePwd(motionEvent, etCurrentPassword));
        etNewpassword.setOnTouchListener((view, motionEvent) -> ChangeHidePwd(motionEvent, etNewpassword));
        etConfirmpassword.setOnTouchListener((view, motionEvent) -> ChangeHidePwd(motionEvent, etConfirmpassword));

        initAdmob();
    }

    protected void initAdmob() {
        MobileAds.initialize(this, getString(R.string.app_id));
        homeAdmob = (AdView) findViewById(R.id.home_admob);
        if (AppSettings.ENABLE_ADMOB) {
            homeAdmob.setVisibility(View.VISIBLE);
            AdRequest.Builder builder = new AdRequest.Builder();
            AdRequest adRequest = builder.build();
            // Start loading the ad in the background.
            homeAdmob.loadAd(adRequest);
        } else {
            homeAdmob.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.btnSaveNow})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.btnSaveNow) {
            currentpasword = etCurrentPassword.getText().toString();
            newPass = etNewpassword.getText().toString();
            Confirmpassword = etConfirmpassword.getText().toString();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
            if (sharedPreferences.contains("Email")) {
                email_id = (sharedPreferences.getString("Email", ""));
            }
            if (sharedPreferences.contains("Password")) {
                txt_password = (sharedPreferences.getString("Password", ""));

            }

            if (currentpasword.equalsIgnoreCase("")) {
                etCurrentPassword.setError("Enter Current Password");
            } else if (newPass.equalsIgnoreCase("")) {
                etNewpassword.setError("Enter new Password");
            } else if (Confirmpassword.equalsIgnoreCase("")) {
                etConfirmpassword.setError("Enter Confirm Password");
            } else if (!newPass.equals(Confirmpassword)) {
                snackBar(clChangepwd, "Current and Confirm Password Not Match");
            } else if (newPass.length() < 8) {
                snackBar(clChangepwd, "password must be at least 8 characters");
            } else {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(email_id, txt_password);
                Objects.requireNonNull(user).reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        snackBar(clChangepwd, "Password updated");
                                        new Handler().postDelayed(new Runnable() {

                                            @Override
                                            public void run() {

                                                FirebaseAuth.getInstance().signOut();
                                                LoginManager.getInstance().logOut();
                                                finish();
                                                startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                                            }

                                        }, 900);

                                    } else {
                                        snackBar(clChangepwd, "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            snackBar(clChangepwd, "Enter valid Password");
                        }

                    }
                });

            }
        }

    }

    public boolean ChangeHidePwd(MotionEvent event, EditText input_password) {
        final int DRAWABLE_RIGHT = 2;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (input_password.getRight() - input_password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {


                if (!input_password.getTransformationMethod().toString().contains("Password")) {
                    input_password.setTransformationMethod(new PasswordTransformationMethod());
                    input_password.setSelection(input_password.getText().length());
                    input_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_eye_off, 0);

                } else {
                    input_password.setTransformationMethod(new HideReturnsTransformationMethod());
                    input_password.setSelection(input_password.getText().length());
                    input_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_eye, 0);
                }
                return true;
            }
        }
        return false;
    }
}


