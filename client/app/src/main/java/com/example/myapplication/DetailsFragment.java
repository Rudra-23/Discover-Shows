package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.function.LongFunction;

public class DetailsFragment extends Fragment {

    JSONObject Details;
    String Name ="", Venue ="", Date = "", Time = "", PriceMin ="", PriceMax ="",
            StatusCode="", BuyTicketAt = "", Seatmap = "", Genres = "", Artists = "";

    public void parseDetails(JSONObject Details) {
        ((ProgressBar)getActivity().findViewById(R.id.DetailsProgressBar)).setVisibility(View.GONE);
        ArrayList<String> details_arr = new ArrayList<String>();
        Log.d("Details value",Details.toString());
        try {
            if(!Details.isNull("name")) {
                Name = Details.getString("name");
            }

            if(!Details.isNull("_embedded") && !Details.getJSONObject("_embedded").isNull("venues")
            && !Details.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).isNull("name")) {
                Venue = Details.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
            }


            if(!Details.isNull("dates")) {
                if(!Details.getJSONObject("dates").isNull("start")) {
                    if(!Details.getJSONObject("dates").getJSONObject("start").isNull("localDate")) {
                        Date = Details.getJSONObject("dates").getJSONObject("start").getString("localDate");
                    }
                    if(!Details.getJSONObject("dates").getJSONObject("start").isNull("localTime")) {
                        Time = Details.getJSONObject("dates").getJSONObject("start").getString("localTime");
                    }

                }
                if(!Details.getJSONObject("dates").isNull("status") && !Details.getJSONObject("dates").getJSONObject("status").isNull("code")) {
                    StatusCode = Details.getJSONObject("dates").getJSONObject("status").getString("code");
                }

            }

            if(!Details.isNull("priceRanges")) {
                if(!Details.getJSONArray("priceRanges").getJSONObject(0).isNull("min")) {
                    PriceMin = Details.getJSONArray("priceRanges").getJSONObject(0).getString("min");
                }
                if(!Details.getJSONArray("priceRanges").getJSONObject(0).isNull("max")) {
                    PriceMax = Details.getJSONArray("priceRanges").getJSONObject(0).getString("max");
                }
            }



            if(!Details.isNull("url"))
                BuyTicketAt = Details.getString("url");


            if(!Details.isNull("seatmap") && !Details.getJSONObject("seatmap").isNull("staticUrl"))
                Seatmap = Details.getJSONObject("seatmap").getString("staticUrl");

            ArrayList<String> genres = new ArrayList<String>();
            for(String ele: new String[]{"segment", "genre", "subGenre", "type", "subType"}) {
                if(!Details.isNull("classifications") && !Details.getJSONArray("classifications").getJSONObject(0).isNull(ele)
                && !Details.getJSONArray("classifications").getJSONObject(0).getJSONObject(ele).isNull("name")) {
                    String val = Details.getJSONArray("classifications").getJSONObject(0).getJSONObject(ele).getString("name");
                    if(!val.equals("Undefined")) {
                        genres.add(val);
                    }
                }
            }

            Genres = String.join(" | ", genres);


            ArrayList<String> artists = new ArrayList<String>();
            if(!Details.isNull("_embedded") && !Details.getJSONObject("_embedded").isNull("attractions")) {
                for(int a = 0; a < Details.getJSONObject("_embedded").getJSONArray("attractions").length(); a++) {
                    if(!Details.getJSONObject("_embedded").getJSONArray("attractions").isNull(a)
                            && !Details.getJSONObject("_embedded").getJSONArray("attractions").getJSONObject(a).isNull("name")) {
                        String val = Details.getJSONObject("_embedded").getJSONArray("attractions").getJSONObject(a).getString("name");
                        if(!val.equals("Undefined")) {
                            artists.add(val);
                        }
                    }
                }
            }


            Artists = String.join(" ", artists);


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_details, container, false);

        DetailsActivity activity = (DetailsActivity)getActivity();
        Details = activity.sendDetails();

        parseDetails(Details);


        if(!Artists.equals("")) {
            ((TextView)view.findViewById(R.id.DetailsArtists)).setText(Artists);
            ((TextView)view.findViewById(R.id.DetailsArtists)).setSelected(true);
        }
        else
            ((ConstraintLayout)view.findViewById(R.id.ArtistsRow)).setVisibility(View.GONE);

        if(!Venue.equals("")) {
            ((TextView)view.findViewById(R.id.DetailsVenue)).setText(Venue);
            ((TextView)view.findViewById(R.id.DetailsVenue)).setSelected(true);
        }
        else {
            ((ConstraintLayout)view.findViewById(R.id.VenueRow)).setVisibility(View.GONE);
        }

        if(!Date.equals(""))
            ((TextView)view.findViewById(R.id.DetailsDate)).setText(Date);
        else
            ((ConstraintLayout)view.findViewById(R.id.DateRow)).setVisibility(View.GONE);

        if(!Time.equals(""))
            ((TextView)view.findViewById(R.id.DetailsTime)).setText(Time);
        else
            ((ConstraintLayout)view.findViewById(R.id.TimeRow)).setVisibility(View.GONE);

        if(!Genres.equals("")) {
            ((TextView)view.findViewById(R.id.DetailsGenre)).setText(Genres);
            ((TextView)view.findViewById(R.id.DetailsGenre)).setSelected(true);
        }
        else
            ((ConstraintLayout)view.findViewById(R.id.GenreRow)).setVisibility(View.GONE);


        if(!PriceMin.equals("") && !PriceMax.equals(""))
            ((TextView)view.findViewById(R.id.DetailsPrice)).setText(PriceMin + " - " + PriceMax + " (USD)" );
        else
            ((ConstraintLayout)view.findViewById(R.id.PriceRow)).setVisibility(View.GONE);

        if(!BuyTicketAt.equals("")) {
            ((TextView)view.findViewById(R.id.DetailsUrl)).setText(Html.fromHtml("<u>" + BuyTicketAt+"</u>"));
            ((TextView)view.findViewById(R.id.DetailsUrl)).setSelected(true);
        }
        else
            ((ConstraintLayout)view.findViewById(R.id.UrlRow)).setVisibility(View.GONE);

        if(!StatusCode.equals("")) {
            ((TextView)view.findViewById(R.id.DetailsStatusCode)).setTextColor(Color.WHITE);
            Drawable drawable = getResources().getDrawable(R.drawable.round_borders);
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;

            if(StatusCode.equals("onsale")) {
                ((TextView)view.findViewById(R.id.DetailsStatusCode)).setText("On Sale");
                gradientDrawable.setColor(Color.parseColor("#66A154"));
            }
            else if(StatusCode.equals("offsale")) {
                ((TextView)view.findViewById(R.id.DetailsStatusCode)).setText("Off Sale");
                gradientDrawable.setColor(Color.parseColor("#FF0000"));
            }
            else if(StatusCode.equals("cancelled")) {
                ((TextView)view.findViewById(R.id.DetailsStatusCode)).setText("Cancelled");
                gradientDrawable.setColor(Color.parseColor("#000000"));
            }
            else if(StatusCode.equals("rescheduled")){
                ((TextView)view.findViewById(R.id.DetailsStatusCode)).setText("Rescheduled");
                gradientDrawable.setColor(Color.parseColor("#FFA500"));
            }
            else {
                ((TextView)view.findViewById(R.id.DetailsStatusCode)).setText("Postponed");
                gradientDrawable.setColor(Color.parseColor("#FFA500"));
            }
            ((TextView)view.findViewById(R.id.DetailsStatusCode)).setBackground(drawable);
        }
        else {
            ((ConstraintLayout)view.findViewById(R.id.StatusRow)).setVisibility(View.GONE);
        }

        ((TextView)view.findViewById(R.id.DetailsUrl)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(BuyTicketAt);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        ImageView seat_map = (ImageView) view.findViewById(R.id.DetailsSeatmap);
        Glide.with(getActivity()).load(Seatmap).into(seat_map);



        return view;
    }
}