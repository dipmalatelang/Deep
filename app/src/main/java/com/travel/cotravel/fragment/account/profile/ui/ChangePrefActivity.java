package com.travel.cotravel.fragment.account.profile.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePrefActivity extends BaseActivity {

    ArrayList<String> travel_with = new ArrayList<>();
    ArrayList<String> range_age = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @BindView(R.id.sp_age_from)
    Spinner spAgeFrom;
    @BindView(R.id.sp_age_to)
    Spinner spAgeTo;
    ArrayAdapter<String> adapter_age_from, adapter_age_to;
    ArrayList<String> array_age;
    SharedPreferences sharedPreferences;
    @BindView(R.id.cb_regi_girl)
    CheckBox cbRegiGirl;
    @BindView(R.id.cb_regi_men)
    CheckBox cbRegiMen;
    AdView homeAdmob;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pref);
        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("TravelWith")) {
            travel_with = new Gson().fromJson((sharedPreferences.getString("TravelWith", "")), new TypeToken<ArrayList<String>>() {}.getType());
        }
        if (sharedPreferences.contains("AgeRange")) {
            range_age = new Gson().fromJson((sharedPreferences.getString("AgeRange", "")), new TypeToken<ArrayList<String>>() {}.getType());
        }
        setPopup();
        setSpinner();
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

    private void setSpinner() {

        if(travel_with!=null)
        {
            if(travel_with.size()>0)
            {
                for (int i = 0; i < travel_with.size(); i++) {
                    if (travel_with.get(i).equalsIgnoreCase("Female")) {
                        cbRegiGirl.setChecked(true);
                    }
                    else if (travel_with.get(i).equalsIgnoreCase("Male")) {
                        cbRegiMen.setChecked(true);
                    }
                }
            }
        }



        array_age = new ArrayList<>(Arrays.asList("18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
                "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70",
                "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90",
                "91", "92", "93", "94", "95", "96", "97", "98", "99"));

        adapter_age_from = new ArrayAdapter<>(ChangePrefActivity.this, android.R.layout.simple_spinner_item, array_age);
        adapter_age_from.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter_age_to = new ArrayAdapter<>(ChangePrefActivity.this, android.R.layout.simple_spinner_item, array_age);
        adapter_age_to.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spAgeFrom.setAdapter(adapter_age_from);
        spAgeTo.setAdapter(adapter_age_to);

        spAgeFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) view).setTextColor(Color.WHITE); //Change selected text color
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spAgeTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) view).setTextColor(Color.WHITE); //Change selected text color
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(range_age!=null)
        {
            if(range_age.size()>0)
            {
                spAgeFrom.setSelection(adapter_age_from.getPosition(range_age.get(0)));
                spAgeTo.setSelection(adapter_age_to.getPosition(range_age.get(1)));
            }
        }

    }

    private void setPopup() {
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            ListPopupWindow AgeFrompopupWindow = (ListPopupWindow) popup.get(spAgeFrom);
            ListPopupWindow AgeTopopupWindow = (ListPopupWindow) popup.get(spAgeTo);

            Objects.requireNonNull(AgeFrompopupWindow).setHeight(500);
            Objects.requireNonNull(AgeTopopupWindow).setHeight(500);

        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
        }
    }


    @OnClick({R.id.cb_regi_girl, R.id.cb_regi_men, R.id.btn_save_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cb_regi_girl:
                boolean checked = ((CheckBox) view).isChecked();
                if (checked) {
                    travel_with.add("Female");
                } else {
                    travel_with.remove("Female");
                }

                break;
            case R.id.cb_regi_men:
                boolean checkedmen = ((CheckBox) view).isChecked();
                if (checkedmen) {
                    travel_with.add("Male");
                } else {
                    travel_with.remove("Male");
                }
                break;

            case R.id.btn_save_register:
                range_age.clear();
                String str_age_from = spAgeFrom.getSelectedItem().toString();
                String str_age_to = spAgeTo.getSelectedItem().toString();
                range_age.add(str_age_from);
                range_age.add(str_age_to);
                updateRegister(travel_with, range_age);
                updateUI(mAuth.getCurrentUser());
                break;


        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            retrieveUserDetail(currentUser);

        }
    }
}
