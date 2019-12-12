package com.travel.cotravel.fragment.account.profile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.verify.EditPhoneActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrivacyPolicyHelp extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.tv_contact_us)
    TextView tvContactUs;

    @BindView(R.id.tv_privacy_policy)
    TextView tvPrivacyPolicy;

    @BindView(R.id.tv_terms_and_condition)
    TextView tvTermsAndCondition;
    @BindView(R.id.home_admob)
    AdView homeAdmob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy_help);
        ButterKnife.bind(this);


    }

    @OnClick({R.id.tv_contact_us, R.id.tv_privacy_policy, R.id.tv_terms_and_condition})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_contact_us:
                startActivity(new Intent(this, ContactUs.class));

                break;
            case R.id.tv_privacy_policy:
                startActivity(new Intent(this, PrivacyPolicy.class));

                break;
            case R.id.tv_terms_and_condition:
                startActivity(new Intent(this,Terms_and_Condition.class));

                break;
        }
    }
}

