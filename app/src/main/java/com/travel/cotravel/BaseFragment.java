package com.travel.cotravel;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.travel.cotravel.fragment.chat.module.APIService;
import com.travel.cotravel.fragment.chat.module.Chat;
import com.travel.cotravel.fragment.chat.module.Client;
import com.travel.cotravel.fragment.chat.module.Data;
import com.travel.cotravel.fragment.chat.module.MyResponse;
import com.travel.cotravel.fragment.chat.module.Sender;
import com.travel.cotravel.fragment.chat.module.Token;
import com.travel.cotravel.fragment.trip.module.PlanTrip;
import com.travel.cotravel.fragment.trip.module.TripList;
import com.travel.cotravel.fragment.trip.module.User;
import com.travel.cotravel.fragment.visitor.UserImg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.travel.cotravel.Constants.ChatsInstance;
import static com.travel.cotravel.Constants.FavoritesInstance;
import static com.travel.cotravel.Constants.ProfileVisitorInstance;
import static com.travel.cotravel.Constants.TokensInstance;
import static com.travel.cotravel.Constants.TrashInstance;


public abstract class BaseFragment extends Fragment {

    public List<String> visitArray = new ArrayList<>();
    public List<String> favArray = new ArrayList<>();
    public int fav_int;
    public List<User> myDetail = new ArrayList<>();
    public List<TripList> tripList = new ArrayList<>();
    public long now = System.currentTimeMillis();
    public List<Date> dates = new ArrayList<>();
    private Date closest;
    public List<PlanTrip> from_to_dates = new ArrayList<>();
    String tripNote = "";
    String theLastMessage, theLastMsgTime, theLastMsgDate;
    Boolean textType;
    boolean notify = false;

    APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService .class);



    public List<TripList> findClosestDate(List<Date> dates, UserImg userImg) {

        User user = userImg.getUser();

        closest = Collections.min(dates, new Comparator<Date>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public int compare(Date d1, Date d2) {
                long diff1 = Math.abs(d1.getTime() - now);
                long diff2 = Math.abs(d2.getTime() - now);
                return Long.compare(diff1, diff2);
            }
        });

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        String dateOutput = simpleDateFormat.format(closest);
        String dateOutput1 = simpleDateFormat1.format(closest);
        int visit_id = getVisit(visitArray, user.getId());

        for (int i = 0; i < from_to_dates.size(); i++) {

            if (from_to_dates.get(i).getDate_from().contains(dateOutput1)) {
                String dateFromTo = from_to_dates.get(i).getDate_from() + " - " + from_to_dates.get(i).getDate_to();

                TripList tripListClass = new TripList(user, userImg, from_to_dates.get(i).getLocation(), tripNote, dateFromTo, visit_id);
//            TripList tripListClass = new TripList(user.getId(), user.getUsername(), userImg.getPictureUrl(), user.getPhone(), user.getAge(),user.getGender(), user.getAbout_me(), user.getLocation(), user.getNationality(), user.getLang(), user.getHeight(), user.getBody_type(), user.getEyes(), user.getHair(), user.getLooking_for(), user.getTravel_with(), user.getVisit(), from_to_dates.get(i).getLocation(), tripNote, dateFromTo, user.getAccount_type(),userImg.getFav(), visit_id);
                tripList.add(tripListClass);
            }
        }

        if (tripList.size() < 1) {

            TripList tripListClass = new TripList(user, userImg,  "", tripNote, "", visit_id);
//            TripList tripListClass = new TripList(user.getId(), user.getUsername(), userImg.getPictureUrl(), user.getPhone(), user.getAge(), user.getGender(), user.getAbout_me(), user.getLocation(), user.getNationality(), user.getLang(), user.getHeight(), user.getBody_type(), user.getEyes(), user.getHair(), user.getLooking_for(), user.getTravel_with(), user.getVisit(), "", tripNote, "", user.getAccount_type(), userImg.getFav(), visit_id);
            tripList.add(tripListClass);
        }

        return tripList;
    }

    public void setProfile(String uid, String id, String name) {
        ProfileVisitorInstance.child(id).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("TAG", "onDataChange: "+dataSnapshot.getChildrenCount());
                if (!dataSnapshot.exists()) {
                    ProfileVisitorInstance.child(id).child(uid).child("id").setValue(uid);
                    notify = true;
                    if (notify) {
                        sendNotifiaction(uid, id, name , "has visited your profile","ProfileVisitor");
                    }
                    notify=false;
                    Toast.makeText(getActivity(), "First Visit", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void sendNotifiaction(String uid, String userid, final String username, final String message, String cxtString) {
        Query query = TokensInstance.orderByKey().equalTo(userid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(uid, R.mipmap.ic_launcher, username + " " + message, "Notification",cxtString,
                            userid);

                    Sender sender = new Sender(data, Objects.requireNonNull(token).getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (Objects.requireNonNull(response.body()).success != 1) {
//                                            snackBar(message_realtivelayout, "Failed!");
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void hiddenProfileDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        dialog.setMessage("You can't view hidden profile.");
        dialog.setTitle("Profile is hidden");
        dialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {

                       snackBar(getView(),"ok is clicked");
                    }
                });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    public void clearSharedPref() {
        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    private int getVisit(List<String> favArray, String id) {
        for (int i = 0; i < favArray.size(); i++) {
            if (favArray.get(i).equalsIgnoreCase(id)) {
                fav_int = 1;
                return fav_int;
            } else {
                fav_int = 0;
            }
        }
        return fav_int;
    }

    public void setFav(String uid, String id) {

        FavoritesInstance
                .child(uid)
                .child(id).child("id").setValue(id);
    }


    public void setTrash(String uid, String id)
    {
        TrashInstance.child(uid).child(id).child("id").setValue(id);
    }

    public void removeTrash(String uid, String id)
    {
        TrashInstance.child(uid).child(id).removeValue();
    }

    public void removeFav(String uid, String id) {

        FavoritesInstance
                .child(uid).child(id).removeValue();

    }


    public void snackBar(View constrainlayout, String s) {
        Snackbar snackbar = Snackbar.make(constrainlayout, s, Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        snackbar.show();
    }


    public void showProgressDialog() {
        if (!Objects.requireNonNull(getActivity()).isFinishing()) {
            ProgressActivity.showDialog(getActivity());
        }
    }

    public void dismissProgressDialog() {
        if (!Objects.requireNonNull(getActivity()).isFinishing()) {
            ProgressActivity.dismissDialog();
        }
    }


    public void checkForLastMsg(Context mContext, final String userid, TextView last_msg, TextView last_msg_time, ConstraintLayout rl_chat) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        ChatsInstance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                theLastMessage = "default";
                theLastMsgTime="default";
                theLastMsgDate="default";

                textType = true;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                            theLastMsgTime= chat.getMsg_time();
                            theLastMsgDate=chat.getMsg_date();
                        }
                    }

                    if (Objects.requireNonNull(chat).getSender().equals(userid) && !chat.isIsseen()) {

                        textType = chat.isIsseen();
                    }

                }

                if ("default".equals(theLastMessage)) {
                    last_msg.setText("No Message");
                    last_msg_time.setText("No Time");

                } else {
                    last_msg.setText(theLastMessage);
                    last_msg_time.setText(theLastMsgTime);
                }

                if(!textType)
                {
                    last_msg.setTextColor(mContext.getResources().getColor(R.color.black));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
