package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class VenueFragment extends Fragment {

    JSONObject Venue;
    String Name = "";
    String Street_Address = "";
    String City = "";
    String State = "";
    String Phone = "";
    String Open_hours = "", Child_rule = "", General_rule = "";
    String Zipcode ="";


    String Latitude = "", Longitude = "";

    public void parseVenue(JSONObject Venue) {
        try {
            Venue = Venue.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
            if(!Venue.isNull("name"))
                Name = Venue.getString("name");

            if(!Venue.isNull("address") && !Venue.getJSONObject("address").isNull("line1"))
                Street_Address = Venue.getJSONObject("address").getString("line1");

            if(!Venue.isNull("city") && !Venue.getJSONObject("city").isNull("name"))
                City = Venue.getJSONObject("city").getString("name");

            if(!Venue.isNull("state") && !Venue.getJSONObject("state").isNull("name"))
                State = Venue.getJSONObject("state").getString("name");

            if(!Venue.isNull("postalCode"))
                Zipcode = Venue.getString("postalCode");

            if(!Venue.isNull("boxOfficeInfo")) {
                if(!Venue.getJSONObject("boxOfficeInfo").isNull("phoneNumberDetail")) {
                    Phone = Venue.getJSONObject("boxOfficeInfo").getString("phoneNumberDetail");
                }
                if(!Venue.getJSONObject("boxOfficeInfo").isNull("openHoursDetail")) {
                    Open_hours = Venue.getJSONObject("boxOfficeInfo").getString("openHoursDetail");
                }
            }

            if(!Venue.isNull("generalInfo")) {
                if(!Venue.getJSONObject("generalInfo").isNull("generalRule")) {
                    General_rule = Venue.getJSONObject("generalInfo").getString("generalRule");
                }
                if(!Venue.getJSONObject("generalInfo").isNull("childRule")) {
                    Child_rule = Venue.getJSONObject("generalInfo").getString("childRule");
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public void getLocation(String Address, String City, String State, String Zipcode) {
        String url =  "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                Address + "," + City + "," + State + "," + Zipcode
                + "&key=AIzaSyDDk9nogjHpNyOVEvllMjcfcpq9RzuScJM";

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest req = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Google", response.toString());
                    JSONArray arr = response.getJSONArray("results");

                    if(arr.length() != 0) {

                        Latitude = arr.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat");
                        Longitude = arr.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng");

                        Bundle bundle = new Bundle();
                        bundle.putString("Latitude", Latitude);
                        bundle.putString("Longitude", Longitude);

                        MapFragment mapFragment = new MapFragment();
                        mapFragment.setArguments(bundle);

                        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                        ft.replace(R.id.mapViewContainer, mapFragment);
                        ft.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error: ", error.getMessage());
            }
        });

        queue.add(req);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_venue, container, false);

        DetailsActivity activity = (DetailsActivity)getActivity();
        Venue = activity.sendVenue();

        parseVenue(Venue);

        getLocation(Street_Address, City, State , Zipcode);

        ((TextView)view.findViewById(R.id.VenueName)).setText(Name);

        if(!Street_Address.equals(""))
            ((TextView)view.findViewById(R.id.VenueAddress)).setText(Street_Address);
        else
            ((ConstraintLayout)view.findViewById(R.id.VenueAddRow)).setVisibility(View.GONE);

        if(!City.equals("") && !State.equals(""))
            ((TextView)view.findViewById(R.id.VenueCity)).setText(City + ", " + State);
        else
            ((ConstraintLayout)view.findViewById(R.id.VenueCityRow)).setVisibility(View.GONE);

        if(!Phone.equals(""))
            ((TextView)view.findViewById(R.id.VenueContact)).setText(Phone);
        else
            ((ConstraintLayout)view.findViewById(R.id.VenueContactRow)).setVisibility(View.GONE);

        ((TextView)view.findViewById(R.id.VenueName)).setSelected(true);
        ((TextView)view.findViewById(R.id.VenueAddress)).setSelected(true);
        ((TextView)view.findViewById(R.id.VenueCity)).setSelected(true);
        ((TextView)view.findViewById(R.id.VenueContact)).setSelected(true);




        TextView GR =  (TextView)view.findViewById(R.id.VenueGeneralRule);

        if(!General_rule.equals("")) {

            GR.setText(General_rule);
            GR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(GR.getMaxLines() == Integer.MAX_VALUE) {
                        GR.setMaxLines(3);
                        GR.setEllipsize(TextUtils.TruncateAt.END);
                    }
                    else {
                        GR.setMaxLines(Integer.MAX_VALUE);
                        GR.setEllipsize(null);
                    }
                }
            });
        }
        else {
            ((TextView)view.findViewById(R.id.GeneralRuleTag)).setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.VenueGeneralRule)).setVisibility(View.GONE);
        }


        TextView CR = ((TextView)view.findViewById(R.id.VenueChildRule));

        if(!Child_rule.equals("")) {
            CR.setText(Child_rule);

            CR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CR.getMaxLines() == Integer.MAX_VALUE) {
                        CR.setMaxLines(3);
                        CR.setEllipsize(TextUtils.TruncateAt.END);
                    }
                    else {
                        CR.setMaxLines(Integer.MAX_VALUE);
                        CR.setEllipsize(null);
                    }
                }
            });
        }
        else {
            ((TextView)view.findViewById(R.id.ChildRuleTag)).setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.VenueChildRule)).setVisibility(View.GONE);
        }

        TextView OH = ((TextView)view.findViewById(R.id.VenueOpenHours));
        if(!Open_hours.equals("")) {
            OH.setText(Open_hours);

            OH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(OH.getMaxLines() == Integer.MAX_VALUE) {
                        OH.setMaxLines(3);
                        OH.setEllipsize(TextUtils.TruncateAt.END);
                    }
                    else {
                        OH.setMaxLines(Integer.MAX_VALUE);
                        OH.setEllipsize(null);
                    }
                }
            });
        }
        else {
            ((TextView)view.findViewById(R.id.OpenHoursTag)).setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.VenueOpenHours)).setVisibility(View.GONE);
        }

        return view;
    }


}