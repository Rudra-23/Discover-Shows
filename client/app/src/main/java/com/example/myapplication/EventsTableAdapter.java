package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.metrics.Event;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
public class EventsTableAdapter extends RecyclerView.Adapter<EventsTableAdapter.MyViewHolder> {

    private final EventsTableInterface eventsTableInterface;


    Context context;
    ArrayList<EventTableFragment.EventsArr> Events;
    public EventsTableAdapter(Context context, ArrayList<EventTableFragment.EventsArr> Events, EventsTableInterface eventsTableInterface) {
        this.context = context;
        this.Events = Events;
        this.eventsTableInterface = eventsTableInterface;
    }

    @NonNull
    @Override
    public EventsTableAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_events_table_row, parent, false);
        return new EventsTableAdapter.MyViewHolder(view, eventsTableInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsTableAdapter.MyViewHolder holder, int position) {
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

        SharedPreferences sharedPref = context.getSharedPreferences(
                "ForSavingEventsToFav1", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String is_available = sharedPref.getString(Events.get(position).Id,"");

        if(is_available.equals("")) {
            holder.AddToFav.setImageResource(R.drawable.heart_outline);
        }
        else {
            holder.AddToFav.setImageResource(R.drawable.heart_filled);
        }

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


        public MyViewHolder(@NonNull View itemView, EventsTableInterface eventsTableInterface) {
            super(itemView);


            EventName = itemView.findViewById(R.id.EventName);

            EventDate = itemView.findViewById(R.id.EventDate);
            EventTime = itemView.findViewById(R.id.EventTime);
            EventGenre = itemView.findViewById(R.id.EventGenre);
            EventVenue = itemView.findViewById(R.id.EventVenue);
            EventImage = itemView.findViewById(R.id.EventImage);
            AddToFav = itemView.findViewById(R.id.EventAddToFav);




            AddToFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(eventsTableInterface != null) {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION) {
                            eventsTableInterface.onButtonClick(pos);
                        }

                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(eventsTableInterface != null) {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION) {
                            eventsTableInterface.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }
}
