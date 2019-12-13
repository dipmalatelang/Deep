package com.travel.cotravel.fragment.visitor.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.travel.cotravel.MainActivity;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.visitor.UserImg;

import java.util.List;

public class VisitorAdapter extends RecyclerView.Adapter<VisitorAdapter.VisitorViewHolder >
{
    private Context mContext;
    private List<UserImg> mTrip;
    private String uid;


    public VisitorAdapter(Context mContext, String uid, List<UserImg> mTrip, VisitorInterface listener)
    {
        this.uid=uid;
        this.mContext = mContext;
        this.mTrip = mTrip;
        this.listener=listener;

    }


    @NonNull
    @Override
    public VisitorViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_visitor, parent, false);
        return new VisitorViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final VisitorViewHolder holder, int position)
    {

        final UserImg tList = mTrip.get(position);

        if(tList.getUser().getGender().equalsIgnoreCase("Female"))
        {
            if (tList.getUser().getAccount_type() == 1) {
                Glide.with(mContext).asBitmap().load(mTrip.get(position).getPictureUrl()).placeholder(R.drawable.no_photo_female)
                        .override(450, 600)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                holder.mImage.setImageBitmap(resource);
                            }
                        });
            }
            else {
                Glide.with(mContext).load(R.drawable.hidden_photo_female_thumb).placeholder(R.drawable.hidden_photo_female_thumb)
                        .override(450, 600)
                     .into(holder.mImage);
            }
        }
        else {
            if (tList.getUser().getAccount_type() == 1) {
                Glide.with(mContext).asBitmap().load(mTrip.get(position).getPictureUrl()).placeholder(R.drawable.no_photo_male)
                        .override(450, 600)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                holder.mImage.setImageBitmap(resource);
                            }
                        });
            }
            else {
                Glide.with(mContext).load(R.drawable.hidden_photo_male_thumb).placeholder(R.drawable.hidden_photo_male_thumb)
                        .override(450, 600)
                     .into(holder.mImage);
            }
        }



        holder.mTitle.setText(tList.getUser().getName());

        holder.mCity.setVisibility(View.GONE);


        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.setProfileVisit(uid,tList.getUser().getId());
                listener.setData(tList,position);

            }
        });


        holder.ivTitle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.delete));

        holder.ivTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).removeVisit(uid, mTrip.get(position).getUser().getId());
                mTrip.remove(position);
                notifyDataSetChanged();
            }
        });
    }





    @Override
    public int getItemCount() {
        return mTrip.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class VisitorViewHolder extends RecyclerView.ViewHolder
    {

        ImageView mImage;
        ImageView ivTitle;
        TextView mTitle, mCity;
        CardView mCardView;

        VisitorViewHolder(View itemView)
        {
            super(itemView);

            mImage = itemView.findViewById(R.id.ivImage);
            ivTitle=itemView.findViewById(R.id.ivTitle);
            mTitle = itemView.findViewById(R.id.tvTitle);
            mCity = itemView.findViewById(R.id.tvCity);
//            mDate = itemView.findViewById(R.id.tvDate);
            mCardView = itemView.findViewById(R.id.cardview);
        }
    }

/*    private class BitmapAsync extends AsyncTask<String, Void, Bitmap>
    {
        ImageView imageView;

        public BitmapAsync(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... args) {
            try {
                URL url = new URL(args[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap yourBitmap = BitmapFactory.decodeStream(input);
                Log.i("TAGi", "doInBackground: "+yourBitmap.getWidth()+" "+yourBitmap.getHeight());
                yourBitmap = Bitmap.createScaledBitmap(yourBitmap,(300), (300), true);
                return yourBitmap;

            }catch (NullPointerException n){
                return null;
            }
            catch (IOException e) {
                // Log exception
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Glide.with(mContext).load(getRoundedCornerBitmap(bitmap)).into(imageView);
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }*/

    VisitorInterface listener;
    public interface VisitorInterface{
        void setProfileVisit(String uid, String id);
        void setData(UserImg mTrip, int position);
    }
}