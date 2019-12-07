package com.travel.cotravel.fragment.account.profile.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.switchDisplayNumber)
    Switch switchDisplayNumber;
    Boolean displayPhone, emailAllNotify, emailMsgNotify, smsMsgNotify;
    @BindView(R.id.switchEmailAllNotify)
    Switch switchEmailAllNotify;
    @BindView(R.id.switchEmailMsgNotify)
    Switch switchEmailMsgNotify;
    @BindView(R.id.switchSmsMsgNotify)
    Switch switchSmsMsgNotify;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.tv_notification)
    TextView tvNotification;
    @BindView(R.id.textInput_email)
    TextInputLayout textInputEmail;
    @BindView(R.id.textInput_phone)
    TextInputLayout textInputPhone;
    @BindView(R.id.tv_help)
    TextView tvHelp;
    @BindView(R.id.constrainlayout_help)
    ConstraintLayout constrainlayoutHelp;
    @BindView(R.id.home_admob)
    AdView homeAdmob;

    private FirebaseUser fuser;

    AdView mAdmobView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        switchDisplayNumber.setOnCheckedChangeListener(this);
        switchEmailAllNotify.setOnCheckedChangeListener(this);
        switchEmailMsgNotify.setOnCheckedChangeListener(this);
        switchSmsMsgNotify.setOnCheckedChangeListener(this);


        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("DisplayPhone")) {
            displayPhone = (sharedPreferences.getBoolean("DisplayPhone", false));

            if (displayPhone) {
                switchDisplayNumber.setChecked(true);
            }
        }

        if (sharedPreferences.contains("EmailAllNotify")) {
            emailAllNotify = (sharedPreferences.getBoolean("EmailAllNotify", false));

            if (emailAllNotify) {
                switchEmailAllNotify.setChecked(true);
            }
        }

        if (sharedPreferences.contains("EmailMsgNotify")) {
            emailMsgNotify = (sharedPreferences.getBoolean("EmailMsgNotify", false));

            if (emailMsgNotify) {
                switchEmailMsgNotify.setChecked(true);
            }
        }

        if (sharedPreferences.contains("SmsMsgNotify")) {
            smsMsgNotify = (sharedPreferences.getBoolean("SmsMsgNotify", false));

            if (smsMsgNotify) {
                switchSmsMsgNotify.setChecked(true);
            }
        }
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


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchDisplayNumber:
                saveSettingInfo("DisplayPhone", isChecked);
                setShowNumber(fuser.getUid(), isChecked);
                break;

            case R.id.switchEmailMsgNotify:
                saveSettingInfo("EmailMsgNotify", isChecked);
                break;

            case R.id.switchSmsMsgNotify:
                saveSettingInfo("SmsMsgNotify", isChecked);
                break;

            case R.id.switchEmailAllNotify:
                saveSettingInfo("EmailAllNotify", isChecked);
                break;
        }
    }

    @OnClick(R.id.constrainlayout_help)
    public void onViewClicked() {
        startActivity(new Intent(this,PrivacyPolicyHelp.class));
    }
}
