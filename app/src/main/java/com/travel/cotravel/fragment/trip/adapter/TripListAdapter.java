package com.travel.cotravel.fragment.trip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.travel.cotravel.R;
import com.travel.cotravel.fragment.trip.module.TripData;

import java.util.List;


public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.TripHolder>{

    String uid;
    Context context;
    List<TripData> tripDataList;


    public TripListAdapter(Context context, String uid, List<TripData> tripDataList,TripListInterface listener) {
        this.context = context;
        this.tripDataList = tripDataList;
        this.uid=uid;
        this.listener=listener;
    }


    @Override
    public int getItemCount() {
        return tripDataList.size();
    }

    public class TripHolder extends RecyclerView.ViewHolder{

        TextView tv_city,tv_date;
        Button btn_edit,btn_delete;

        public TripHolder(View itemView) {
            super(itemView);
            tv_city= itemView.findViewById(R.id.tv_city);
            tv_date= itemView.findViewById(R.id.tv_date);
            btn_edit=itemView.findViewById(R.id.btn_edit);
            btn_delete=itemView.findViewById(R.id.btn_delete);

        }
    }

    @Override
    public void onBindViewHolder(final TripHolder holder, int position) {
        final TripData tripDetails=tripDataList.get(position);
        String myString = tripDetails.getLocation();

        String City = myString.substring(0,1).toUpperCase() + myString.substring(1);
        holder.tv_city.setText(City);
        holder.tv_date.setText(tripDetails.getFrom_date()+" - "+tripDetails.getTo_date());

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                listener.removeTrip(uid,tripDataList.get(position).getId());
                tripDataList.remove(position);

                notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        });

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.sendTripLiist(tripDataList,position);

            }
        });
        
    }

    TripListInterface listener;
    public interface TripListInterface{
        void sendTripLiist(List<TripData> tripDataList, int position);
        void removeTrip(String uid, String id);
    }


    @NonNull
    @Override
    public TripHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_trips, parent, false);
        return new TripHolder(v);
    }

}
