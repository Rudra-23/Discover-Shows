package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EventTableFragment extends Fragment implements EventsTableInterface {

    ArrayList<EventsArr> Events;
    JSONObject EventsJSON;
    EventsTableAdapter TableAdapter;

    public ArrayList<String> getEventArraytoPass(int position) {
        ArrayList<String> temp = new ArrayList<>();

        temp.add(Events.get(position).Id);
        temp.add(Events.get(position).Name);
        temp.add(Events.get(position).Date);
        temp.add(Events.get(position).Time);
        temp.add(Events.get(position).Venue);
        temp.add(Events.get(position).Genre);
        temp.add(Events.get(position).ImageUrl);

        return temp;
    }

    @Override
    public void onRefresh() {
        TableAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onItemClick(int position) {
        Log.d("Events id",Events.get(position).Id);
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("EventDetailsId", Events.get(position).Id);
        intent.putExtra("EventDetailsName", Events.get(position).Name);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Events.get(position).TimeStamp = timestamp.toString();

        Gson gson = new Gson();
        ArrayList<String> temp = getEventArraytoPass(position);
        temp.add(Events.get(position).TimeStamp);
        String json = gson.toJson(temp);
        intent.putExtra("EventDetailsJSON", json);

        startActivity(intent);
    }

    @Override
    public void onButtonClick(int position) {

        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                "ForSavingEventsToFav1", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String is_available = sharedPref.getString(Events.get(position).Id,"");

        if(is_available.equals("")) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Events.get(position).TimeStamp = timestamp.toString();

            ArrayList<String> temp = getEventArraytoPass(position);

            temp.add(Events.get(position).TimeStamp);
            Gson gson = new Gson();
            String json = gson.toJson(temp);

            editor.putString(Events.get(position).Id, json);
            editor.apply();
            Snackbar snackbar = Snackbar
                    .make(getView(), Events.get(position).Name + " added to favorites", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(Color.parseColor("#9b9b9b"));
            snackbar.setTextColor(Color.parseColor("#000000"));
            snackbar.show();

        }
        else {
            editor.remove(Events.get(position).Id);
            editor.apply();
            Snackbar snackbar = Snackbar
                    .make(getView(), Events.get(position).Name + " removed from favorites", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(Color.parseColor("#9b9b9b"));
            snackbar.setTextColor(Color.parseColor("#000000"));
            snackbar.show();
        }
        TableAdapter.notifyItemChanged(position);

//        if(getFragmentManager().findFragmentByTag("f1") != null) {
//            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("f1")).commit();
//        }

    }



    class EventsArr {
        public String Date = "";
        public String Time = "";
        public String Genre = "";
        public String Venue = "";
        public String Name = "";
        public String ImageUrl = "";
        public String Id = "";

        public String TimeStamp = "";
    }
    public ArrayList<EventsArr> parseEvents(JSONObject response) {
        try {
            ArrayList<EventsArr> obj_arr = new ArrayList<EventsArr>();

            if(response.isNull("_embedded")) {
                getActivity().findViewById(R.id.NoArtistsMessage).setVisibility(View.VISIBLE);
                return obj_arr;
            }
            JSONArray Events = response.getJSONObject("_embedded").getJSONArray("events");

            for(int i = 0; i < Events.length(); i++) {
                EventsArr obj = new EventsArr();
                obj.Id = Events.getJSONObject(i).getString("id");
                obj.Name = Events.getJSONObject(i).getString("name");
                if(!Events.getJSONObject(i).isNull("dates") && !Events.getJSONObject(i).getJSONObject("dates").isNull("start") &&
                        !Events.getJSONObject(i).getJSONObject("dates").getJSONObject("start").isNull("localDate")) {
                    obj.Date = Events.getJSONObject(i).getJSONObject("dates").getJSONObject("start").getString("localDate");
                }

                if(!Events.getJSONObject(i).isNull("dates") && !Events.getJSONObject(i).getJSONObject("dates").isNull("start") &&
                        !Events.getJSONObject(i).getJSONObject("dates").getJSONObject("start").isNull("localTime")) {
                    obj.Time = Events.getJSONObject(i).getJSONObject("dates").getJSONObject("start").getString("localTime");
                }

                obj.Venue = Events.getJSONObject(i).getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
                obj.Genre = Events.getJSONObject(i).getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");

                if(!Events.getJSONObject(i).isNull("images") && !Events.getJSONObject(i).getJSONArray("images").getJSONObject(0).isNull("url")) {
                    obj.ImageUrl = Events.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("url");
                }

                obj_arr.add(obj);
            }
            return obj_arr;

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void showEvents(ArrayList<EventsArr> Events, View view) {
        RecyclerView recyclerView = view.findViewById(R.id.MyEventsTableView);
        TableAdapter = new EventsTableAdapter(getActivity(), Events, this);
        recyclerView.setAdapter(TableAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        view.findViewById(R.id.ProgressBarId).setVisibility(View.GONE);
    }

    public void getEvents(String url, View view) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest req = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Events = parseEvents(response);
                    EventsJSON = response;

                    Comparator<EventsArr> ageComparator = new Comparator<EventsArr>() {
                        @Override
                        public int compare(EventsArr e1, EventsArr e2) {
                            return (e1.Date + " " + e1.Time).compareTo(e2.Date + " " + e2.Time);
                        }
                    };

                    // Sort the ArrayList using the custom comparator
                    Collections.sort(Events, ageComparator);

                    showEvents(Events, view);

                } catch (Exception e) {
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

        View view =  inflater.inflate(R.layout.fragment_event_table, container, false);

        view.findViewById(R.id.NoArtistsMessage).setVisibility(View.GONE);

        ConstraintLayout btn = (ConstraintLayout) view.findViewById(R.id.backBtn);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                FragmentTransaction trans = getFragmentManager()
//                        .beginTransaction();
//
//                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                trans.addToBackStack(null);
//                trans.commit();

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack(
                        fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getId(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        String url = getArguments().getString("EventsUrl");
        String Noloc = getArguments().getString("EventsNoLoc");
        if(Noloc == "true") {
            view.findViewById(R.id.NoArtistsMessage).setVisibility(View.VISIBLE);
            view.findViewById(R.id.ProgressBarId).setVisibility(View.GONE);
        }
        else
            getEvents(url, view);



        return view;
    }
}
