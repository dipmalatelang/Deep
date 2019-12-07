package com.travel.cotravel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;


import com.facebook.login.LoginManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.travel.cotravel.fragment.account.MyProfileFragment;
import com.travel.cotravel.fragment.chat.ChatFragment;
import com.travel.cotravel.fragment.favourite.FavouriteFragment;
import com.travel.cotravel.fragment.trip.TripFragment;
import com.travel.cotravel.fragment.visitor.VisitorFragment;
import com.travel.cotravel.login.LoginActivity;

import java.util.HashMap;

import static com.travel.cotravel.Constants.UsersInstance;


public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    FirebaseUser fUser;
    Fragment fragment;
    BottomNavigationView navView;
    ConstraintLayout container;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String fragmentValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);

        container = findViewById(R.id.container);

    }

    private void storedFragment(String fragmentString)
    {
        sharedPreferences=getSharedPreferences("LoginDetails",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        editor.putString("loadFrag",fragmentString);
        editor.apply();

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        getBackRunningFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }

    private void status(String status) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        UsersInstance.child(fUser.getUid()).updateChildren(hashMap);
    }

    private void getBackRunningFragment()
    {
        sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("loadFrag")) {
            fragmentValue = (sharedPreferences.getString("loadFrag", ""));
        }
        if(fragmentValue==null)
        {
            fragment = new TripFragment();
            navView.setSelectedItemId(R.id.nav_trip);
        }
        else {
            if(fragmentValue.equalsIgnoreCase("profile"))
            {
                fragment = new MyProfileFragment();
                navView.setSelectedItemId(R.id.nav_account);
            }
            else if(fragmentValue.equalsIgnoreCase("chat"))
            {
                fragment = new ChatFragment();
                navView.setSelectedItemId(R.id.nav_chat);
            }
            else if(fragmentValue.equalsIgnoreCase("favourite"))
            {
                fragment = new FavouriteFragment();
                navView.setSelectedItemId(R.id.nav_favorites);
            }
            else if(fragmentValue.equalsIgnoreCase("visitor"))
            {
                fragment = new VisitorFragment();
                navView.setSelectedItemId(R.id.nav_vistor);
            }
            else
            {
                fragment = new TripFragment();
                navView.setSelectedItemId(R.id.nav_trip);
            }
        }

        loadFragment(fragment);

    }

    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_main_screen, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_account:
                fragment = new MyProfileFragment();
                storedFragment("profile");
                break;
            case R.id.nav_chat:
                fragment = new ChatFragment();
                storedFragment("chat");
                break;
            case R.id.nav_favorites:
                fragment = new FavouriteFragment();
                storedFragment("favourite");
                break;
            case R.id.nav_trip:
                fragment = new TripFragment();
                storedFragment("trip");
                break;
            case R.id.nav_vistor:
                fragment = new VisitorFragment();
                storedFragment("visitor");
                break;
        }

        return loadFragment(fragment);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trip_menu, menu);
        return true;
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        snackBar(container, "Please click Back again to exit");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                navView.setSelectedItemId(R.id.nav_trip);
                fragment = new TripFragment();
                loadFragment(fragment);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                snackBar(container, "Logout");
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}
