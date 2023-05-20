package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// Code inspired from https://youtu.be/Mc0XT58A1Z4
public class FavTableAdapter extends RecyclerView.Adapter<FavTableAdapter.MyViewHolder>{

    private final FavTableInterface favTableInterface;
    Context context;
    ArrayList<FavoriteFragment.FavoriteArr> Events;

    public FavTableAdapter(Context context, ArrayList<FavoriteFragment.FavoriteArr> Events, FavTableInterface favTableInterface) {
        this.context = context;
        this.Events = Events;
        this.favTableInterface = favTableInterface;
    }

    @NonNull
    @Override
    public FavTableAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fav_table_row, parent, false);
        return new FavTableAdapter.MyViewHolder(view, favTableInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull FavTableAdapter.MyViewHolder holder, int position) {
        holder.EventName.setText(Events.get(position).Name);
        holder.EventName.setSelected(true);
        holder.EventDate.setText(Events.get(position).Date);

        if(!Events.get(position).Time.equals("")) {
            SimpleDateFormat inFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat outFormat = new SimpleDateFormat("h:mm a");
            Date date = null;
            try {
                date = inFormat.parse(Events.get(position).Time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.EventTime.setText(outFormat.format(date));
        }
        holder.EventVenue.setText(Events.get(position).Venue);
        holder.EventVenue.setSelected(true);
        holder.EventGenre.setText(Events.get(position).Genre);

        Glide.with(holder.itemView.getContext()).load(Events.get(position).ImageUrl).apply(RequestOptions.bitmapTransform(new RoundedCorners(25))).into(holder.EventImage);
    }

    @Override
    public int getItemCount() {
        return Events.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView EventName, EventDate, EventTime, EventVenue, EventGenre;
        ImageView EventImage;

        ImageButton AddToFav;

        public MyViewHolder(@NonNull View itemView, FavTableInterface favTableInterface) {
            super(itemView);

            EventName = itemView.findViewById(R.id.FavName);
            EventDate = itemView.findViewById(R.id.FavDate);
            EventTime = itemView.findViewById(R.id.FavTime);
            EventGenre = itemView.findViewById(R.id.FavGenre);
            EventVenue = itemView.findViewById(R.id.FavVenue);
            EventImage = itemView.findViewById(R.id.FavImage);
            AddToFav = itemView.findViewById(R.id.FavAddToFav);

            AddToFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(favTableInterface != null) {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION) {
                            favTableInterface.onButtonClick(pos);
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(favTableInterface != null) {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION) {
                            favTableInterface.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }
}
