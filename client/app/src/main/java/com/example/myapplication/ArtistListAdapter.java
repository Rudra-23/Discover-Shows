package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

// Code inspired from https://youtu.be/Mc0XT58A1Z4
public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.MyViewHolder> {

    Context context;
    ArrayList<ArtistFragment.ArtistsArr> ArtistsList;
    public ArtistListAdapter(Context context, ArrayList<ArtistFragment.ArtistsArr> ArtistsList) {
        this.context = context;
        this.ArtistsList = ArtistsList;
    }

    @NonNull
    @Override
    public ArtistListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.artists_table_row, parent, false);
        return new ArtistListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistListAdapter.MyViewHolder holder, int position) {
        holder.ArtistsName.setText(ArtistsList.get(position).Name);
        holder.ArtistsPopularity.setText(ArtistsList.get(position).Popularity);

        holder.progressBar.setRotation(180);
        holder.progressBar.setProgress(Integer.parseInt(ArtistsList.get(position).Popularity));

        holder.ArtistsFollowers.setText(ArtistsList.get(position).Followers + " Followers");

        String text = "<u> Check out on Spotify </u>";

        holder.Spotify_link.setText(Html.fromHtml(text));
        holder.Spotify_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(ArtistsList.get(position).Spotify_link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        holder.Spotify_link.setMovementMethod(LinkMovementMethod.getInstance());

        Glide.with(holder.itemView.getContext()).load(ArtistsList.get(position).Image).apply(RequestOptions.bitmapTransform(new RoundedCorners(25))).into(holder.ArtistsImage);

        if(ArtistsList.get(position).Albums.size() >=1)
            Glide.with(holder.itemView.getContext()).load(ArtistsList.get(position).Albums.get(0)).apply(RequestOptions.bitmapTransform(new RoundedCorners(25))).into(holder.ArtistAlbum1);
        else
            Glide.with(holder.itemView.getContext()).load("").apply(RequestOptions.bitmapTransform(new RoundedCorners(25))).into(holder.ArtistAlbum1);

        if(ArtistsList.get(position).Albums.size() >=2)
            Glide.with(holder.itemView.getContext()).load(ArtistsList.get(position).Albums.get(1)).apply(RequestOptions.bitmapTransform(new RoundedCorners(25))).into(holder.ArtistAlbum2);
        else
            Glide.with(holder.itemView.getContext()).load("").apply(RequestOptions.bitmapTransform(new RoundedCorners(25))).into(holder.ArtistAlbum2);

        if(ArtistsList.get(position).Albums.size() >=3)
            Glide.with(holder.itemView.getContext()).load(ArtistsList.get(position).Albums.get(2)).apply(RequestOptions.bitmapTransform(new RoundedCorners(25))).into(holder.ArtistAlbum3);
        else
            Glide.with(holder.itemView.getContext()).load("").apply(RequestOptions.bitmapTransform(new RoundedCorners(25))).into(holder.ArtistAlbum3);
    }

    @Override
    public int getItemCount() {
        return ArtistsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ArtistsImage, ArtistAlbum1, ArtistAlbum2, ArtistAlbum3;
        TextView ArtistsName, ArtistsFollowers, ArtistsPopularity, Spotify_link;
        ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ArtistsImage = itemView.findViewById(R.id.ArtistImage);
            ArtistsName = itemView.findViewById(R.id.ArtistName);
            ArtistsFollowers = itemView.findViewById(R.id.ArtistFollowers);
            ArtistsPopularity = itemView.findViewById(R.id.ArtistPopularity);
            progressBar = itemView.findViewById(R.id.ArtistPopBar);

            ArtistAlbum1 = itemView.findViewById(R.id.ArtistAlbum1);
            ArtistAlbum2 = itemView.findViewById(R.id.ArtistAlbum2);
            ArtistAlbum3 = itemView.findViewById(R.id.ArtistAlbum3);
            Spotify_link = itemView.findViewById(R.id.Spotify_link);

        }
    }
}
