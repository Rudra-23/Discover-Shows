package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class FavoriteFragment extends Fragment implements FavTableInterface {

    FavTableAdapter TableAdapter;


    @Override
    public void onItemClick(int position) {
        Log.d("Events id",FavEvents.get(position).Id);
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("EventDetailsId", FavEvents.get(position).Id);
        intent.putExtra("EventDetailsName", FavEvents.get(position).Name);
        startActivity(intent);
    }

    @Override
    public void onButtonClick(int position) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                "ForSavingEventsToFav1", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String is_available = sharedPref.getString(FavEvents.get(position).Id,"");
        if(!is_available.equals("")) {
            editor.remove(FavEvents.get(position).Id);
            editor.apply();

            Snackbar snackbar = Snackbar
                    .make(getView(), FavEvents.get(position).Name + " removed from favorites", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(Color.parseColor("#9b9b9b"));
            snackbar.setTextColor(Color.parseColor("#000000"));
            snackbar.show();

            FavEvents.remove(position);
            TableAdapter.notifyItemRemoved(position);

//            if(getFragmentManager().findFragmentByTag("EventsTag") != null) {
//                EventTableFragment fragment = (EventTableFragment) getFragmentManager().findFragmentByTag("EventsTag");
//                fragment.onRefresh();
//            }


        }
        if(FavEvents.size() == 0) {
            ((TextView) getActivity().findViewById(R.id.NoFavMessage)).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        TableAdapter.notifyDataSetChanged();
    }

    class FavoriteArr {
        public String Date = "";
        public String Time = "";
        public String Genre = "";
        public String Venue = "";
        public String Name = "";
        public String ImageUrl = "";
        public String Id = "";
        public String TimeStamp = "";
    }

    ArrayList<FavoriteArr> FavEvents = new ArrayList<>();
    public void getFavorites() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                "ForSavingEventsToFav1", getActivity().MODE_PRIVATE);

        Map<String, ?> allEntries = sharedPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            ArrayList<String> json_data = gson.fromJson(entry.getValue().toString(), type);
            FavoriteArr obj = new FavoriteArr();
            obj.Id = json_data.get(0);
            obj.Name = json_data.get(1);
            obj.Date = json_data.get(2);
            obj.Time = json_data.get(3);
            obj.Venue = json_data.get(4);
            obj.Genre = json_data.get(5);
            obj.ImageUrl = json_data.get(6);
            obj.TimeStamp = json_data.get(7);

            FavEvents.add(obj);
        }
    }

    public void createFavView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.FavRecyclerView);
        TableAdapter = new FavTableAdapter(getActivity(), FavEvents, this);
        recyclerView.setAdapter(TableAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

//        view.setTag("FavouriteTag");

        FavPipeline(view);

        createFavView(view);
        return view;
    }

    public void FavPipeline(View view) {
        FavEvents.clear();
        getFavorites();

        Comparator<FavoriteArr> ageComparator = new Comparator<FavoriteArr>() {
            @Override
            public int compare(FavoriteArr e1, FavoriteArr e2) {
                return (e1.TimeStamp).compareTo(e2.TimeStamp);
            }
        };

        // Sort the ArrayList using the custom comparator
        Collections.sort(FavEvents, ageComparator);

        if(FavEvents.size() != 0) {
            ((TextView)view.findViewById(R.id.NoFavMessage)).setVisibility(View.GONE);
        }
        else {
            ((TextView)view.findViewById(R.id.NoFavMessage)).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        RecyclerView recyclerView = getActivity().findViewById(R.id.FavRecyclerView);
        FavTableAdapter adapter = (FavTableAdapter) recyclerView.getAdapter();

        FavPipeline(getView());

        adapter.notifyDataSetChanged();
    }

}