package com.travel.cotravel.fragment.trip;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.BuildConfig;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.trip.adapter.TripListAdapter;
import com.travel.cotravel.fragment.trip.module.TripData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.travel.cotravel.Constants.TripsInstance;


public class AddTripActivity extends BaseActivity {

    FirebaseUser fuser;
    Calendar mcalendar = Calendar.getInstance();
    int day, month, year;
    TripListAdapter mtripAdapter;
    ArrayList<TripData> trips = new ArrayList<>();
    @BindView(R.id.trip_relativelayout)
    CoordinatorLayout tripRelativelayout;
    String edit_id = "";
    LinearLayoutManager ll_manager;
    @BindView(R.id.et_location)
    TextInputEditText et_location;
    @BindView(R.id.til_location)
    TextInputLayout til_location;
    Date tvDateFrom, tvDateTo;
    @BindView(R.id.tv_from_date1)
    TextInputEditText tvFromDate1;
    @BindView(R.id.tv_to_date1)
    TextInputEditText tvToDate1;
    @BindView(R.id.et_note)
    TextInputEditText etNote;
    @BindView(R.id.btn_add_trip)
    Button btnAddTrip;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.recyclerview_trips)
    RecyclerView recyclerviewTrips;
    boolean isPresent=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        ButterKnife.bind(this);
        Places.initialize(getApplicationContext(), BuildConfig.map_api_key);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        day = mcalendar.get(Calendar.DAY_OF_MONTH);
        month = mcalendar.get(Calendar.MONTH);
        year = mcalendar.get(Calendar.YEAR);

/*        Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateTostr = simpleDateFormat.format(today);*/

        ll_manager = new LinearLayoutManager(AddTripActivity.this);
        recyclerviewTrips.setLayoutManager(ll_manager);

        displayTripList(fuser.getUid());

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void displayTripList(String uid) {

        TripsInstance.orderByKey().equalTo(uid)
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                trips.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        TripData tripData = snapshot1.getValue(TripData.class);

                                        trips.add(0, tripData);
                                    }
                                }
                                mtripAdapter = new TripListAdapter(AddTripActivity.this, uid, trips, new TripListAdapter.TripListInterface() {
                                    @Override
                                    public void sendTripLiist(List<TripData> tripDataList, int position) {
                                        snackBar(tripRelativelayout, "Edit");
                                        et_location.setText(tripDataList.get(position).getLocation());
                                        etNote.setText(tripDataList.get(position).getTrip_note());
                                        tvFromDate1.setText(tripDataList.get(position).getFrom_date());
                                        tvToDate1.setText(tripDataList.get(position).getTo_date());
                                        edit_id = tripDataList.get(position).getId();
                                        appbar.setExpanded(true);
                                    }

                                    @Override
                                    public void removeTrip(String uid, String id) {
                                        TripsInstance.child(uid).child(id).removeValue();
                                    }
                                });
                                recyclerviewTrips.setAdapter(mtripAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
    }

    private void Trips(String edit_id, String et_locations, String et_notes, String tv_from_dates, String tv_to_dates) {
        String userId;
        if (!edit_id.equalsIgnoreCase("")) {
            userId = edit_id;
            snackBar(tripRelativelayout, "Trip edited Successfully..!");
            dismissProgressDialog();
        } else {
            userId = TripsInstance.child(fuser.getUid()).push().getKey();
            snackBar(tripRelativelayout, "Trip added Successfully..!");
            dismissProgressDialog();
        }

        TripData tripData = new TripData(userId, et_locations, et_notes, tv_from_dates, tv_to_dates);
        TripsInstance.child(fuser.getUid()).child(Objects.requireNonNull(userId)).setValue(tripData);

        displayTripList(fuser.getUid());
        clearText();

    }

    private void clearText() {
        etNote.setText("");
        et_location.setText("");
        tvFromDate1.setText("");
        tvToDate1.setText("");
        et_location.clearFocus();
        etNote.clearFocus();
        tvFromDate1.clearFocus();
        tvToDate1.clearFocus();
        edit_id = "";
    }

    private void addTrip(String edit_id, String et_locations, String et_notes, String tv_from_dates, String tv_to_dates) {
        try {
            tvDateFrom = new SimpleDateFormat("dd/MM/yyyy").parse(tv_from_dates);
            tvDateTo = new SimpleDateFormat("dd/MM/yyyy").parse(tv_to_dates);

            if (!tvDateFrom.after(tvDateTo)) {
             if(trips.size()>0)
             {
                 for (int i = 0; i < trips.size(); i++) {
                     try {
                         Date listDateFrom = new SimpleDateFormat("dd/MM/yyyy").parse(trips.get(i).getFrom_date());
                         Date listDateTo = new SimpleDateFormat("dd/MM/yyyy").parse(trips.get(i).getTo_date());

                         Log.i(TAG, "addTrip: "+tvDateFrom+" "+tvDateTo);
                         Log.i(TAG, "addTrip: "+listDateFrom+" "+listDateTo);

                         Log.i(TAG, "addTrip: "+tvDateFrom.before(listDateFrom)+" && "+tvDateTo.before(listDateFrom)+" || "+tvDateFrom.after(listDateTo)+" && "+tvDateTo.after(listDateTo));
                         if ((tvDateFrom.before(listDateFrom) && tvDateTo.before(listDateFrom)) || (tvDateFrom.after(listDateTo) && tvDateTo.after(listDateTo))) { }
                         else {
                             isPresent=true;
                             Log.i(TAG, "addTrip: Got in");
                         }

                     } catch (ParseException e) {
                         e.printStackTrace();
                     }
                 }
                 if(isPresent)
                 {
                     String userId;
                     if (!edit_id.equalsIgnoreCase("")) {
                         userId = edit_id;

                         TripData tripData = new TripData(userId, et_locations, et_notes, tv_from_dates, tv_to_dates);
                         TripsInstance.child(fuser.getUid()).child(Objects.requireNonNull(userId)).setValue(tripData);

                         displayTripList(fuser.getUid());
                         clearText();

                         snackBar(tripRelativelayout, "Trip edited Successfully..!");
                         dismissProgressDialog();
                     }
                     else {
                         snackBar(tripRelativelayout, "Maybe Trip has already planned for these days");
                         dismissProgressDialog();
                     }

                 }
                 else {

                     String userId = TripsInstance.child(fuser.getUid()).push().getKey();

                     TripData tripData = new TripData(userId, et_locations, et_notes, tv_from_dates, tv_to_dates);
                     TripsInstance.child(fuser.getUid()).child(Objects.requireNonNull(userId)).setValue(tripData);

                     displayTripList(fuser.getUid());
                     clearText();

                     snackBar(tripRelativelayout, "Trip added Successfully..!");
                     dismissProgressDialog();
                 }
             }
             else {
                 String userId = TripsInstance.child(fuser.getUid()).push().getKey();

                 TripData tripData = new TripData(userId, et_locations, et_notes, tv_from_dates, tv_to_dates);
                 TripsInstance.child(fuser.getUid()).child(Objects.requireNonNull(userId)).setValue(tripData);

                 displayTripList(fuser.getUid());
                 clearText();

                 snackBar(tripRelativelayout, "Trip added Successfully..!");
                 dismissProgressDialog();

//                 Trips(edit_id, et_locations, et_notes, tv_from_dates, tv_to_dates);
             }
            }
            else {
                snackBar(tripRelativelayout, "Enter proper dates");
                dismissProgressDialog();
            }

       /*     if (trips.size() > 0) {
                if (!tvDateFrom.after(tvDateTo)) {
                    for (int i = 0; i < trips.size(); i++) {
                        try {
                            Date listDateFrom = new SimpleDateFormat("dd/MM/yyyy").parse(trips.get(i).getFrom_date());
                            Date listDateTo = new SimpleDateFormat("dd/MM/yyyy").parse(trips.get(i).getTo_date());

                                if ((tvDateFrom.before(listDateFrom) && tvDateTo.before(listDateFrom)) || (tvDateFrom.after(listDateTo) && tvDateTo.after(listDateTo))) {
                                } else {
                                    isPresent=true;
                                }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if(isPresent)
                    {
                        snackBar(tripRelativelayout, "Maybe Trip has already planned for these days");
                        dismissProgressDialog();
                        isPresent=false;
                    }
                    else {
                        Trips(edit_id, et_locations, et_notes, tv_from_dates, tv_to_dates);
                    }

                } else {
                    snackBar(tripRelativelayout, "Enter proper dates");
                    dismissProgressDialog();
                }
            } else if (!tvDateFrom.after(tvDateTo)) {
                Trips(edit_id, et_locations, et_notes, tv_from_dates, tv_to_dates);
            } else {
                snackBar(tripRelativelayout, "Enter proper dates");
                dismissProgressDialog();
            }*/
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    int AUTOCOMPLETE_REQUEST_CODE = 111;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                et_location.setText(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                Status status = Autocomplete.getStatusFromIntent(data);

            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    @OnClick({R.id.tv_from_date1, R.id.tv_to_date1, R.id.et_location, R.id.til_location, R.id.btn_add_trip})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.btn_add_trip:
                hideKeyboard();
                showProgressDialog();
                String et_locations = et_location.getText().toString().trim();
                String et_notes = etNote.getText().toString().trim();
                String tv_from_dates = tvFromDate1.getText().toString().trim();
                String tv_to_dates = tvToDate1.getText().toString().trim();

                if (TextUtils.isEmpty(et_locations) || TextUtils.isEmpty(et_notes) || TextUtils.isEmpty(tv_from_dates) || TextUtils.isEmpty(tv_from_dates)) {
                    Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
                    snackBar(tripRelativelayout, "All fileds are required !");
                    dismissProgressDialog();
                } else {
                    isPresent=false;
                    addTrip(edit_id, et_locations, et_notes, tv_from_dates, tv_to_dates);
                }
                break;

            case R.id.tv_from_date1:
                DatePickerDialog.OnDateSetListener fromlistener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String sMonth, sDay;
                        month = monthOfYear + 1;
                        day = dayOfMonth;

                        if (month < 10) {
                            sMonth = "0" + month;
                        } else {
                            sMonth = String.valueOf(month);
                        }

                        if (day < 10) {
                            sDay = "0" + day;
                        } else {
                            sDay = String.valueOf(day);
                        }

                        tvFromDate1.setText(new StringBuilder().append(sDay)
                                .append("/").append(sMonth).append("/").append(year)
                                .append(" "));

                    }
                };
                DatePickerDialog fromdpDialog = new DatePickerDialog(this, fromlistener, year, month, day);
                fromdpDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                fromdpDialog.show();
                break;
            case R.id.tv_to_date1:
                DatePickerDialog.OnDateSetListener tolistener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String sMonth, sDay;
                        month = monthOfYear + 1;
                        day = dayOfMonth;

                        if (month < 10) {
                            sMonth = "0" + month;
                        } else {
                            sMonth = String.valueOf(month);
                        }

                        if (day < 10) {
                            sDay = "0" + day;
                        } else {
                            sDay = String.valueOf(day);
                        }

                        tvToDate1.setText(new StringBuilder().append(sDay)
                                .append("/").append(sMonth).append("/").append(year)
                                .append(" "));

                    }
                };
                DatePickerDialog todpDialog = new DatePickerDialog(this, tolistener, year, month, day);
                todpDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                todpDialog.show();
                break;
            case R.id.et_location:
            case R.id.til_location:
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                break;

        }
    }
}
