package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class ArtistFragment extends Fragment {

    class ArtistsArr {
        public String Name = "";
        public String Followers = "";
        public  String Popularity = "";
        public String Spotify_link = "";
        public String Image = "";
        public ArrayList<String> Albums;
    }
    JSONArray Artists;
    ArrayList<ArtistsArr> ArtistsList;

    public ArrayList<ArtistsArr> parseArtists(JSONArray Artists) {
        ArrayList<ArtistsArr> ArtistsList = new ArrayList<ArtistsArr>();
        for(int i = 0; i<Artists.length(); i++) {
            try {
                ArtistsArr obj = new ArtistsArr();
                obj.Name = Artists.getJSONObject(i).getString("name");
                obj.Popularity = Artists.getJSONObject(i).getString("popularity");
                obj.Followers = Artists.getJSONObject(i).getString("followers");
                if(obj.Followers.length() <= 6 && obj.Followers.length() >= 4) {
                    obj.Followers = obj.Followers.substring(0,obj.Followers.length() - 3);
                    obj.Followers += "K";
                }
                else if(obj.Followers.length() > 6) {
                    obj.Followers = obj.Followers.substring(0,obj.Followers.length() - 6);
                    obj.Followers += "M";
                }

                obj.Spotify_link = Artists.getJSONObject(i).getString("spotify_link");
                obj.Image = Artists.getJSONObject(i).getString("image");
                ArrayList<String> albums = new ArrayList<String>();
                JSONArray Albums_Array = Artists.getJSONObject(i).getJSONArray("albums");
                for(int idx = 0; idx<Albums_Array.length(); idx++) {
                    albums.add(Albums_Array.getString(idx));
                }
                obj.Albums = albums;
                ArtistsList.add(obj);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return ArtistsList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_artist, container, false);

        ((TextView)view.findViewById(R.id.NoArtistsMessage)).setVisibility(View.VISIBLE);
        DetailsActivity activity = (DetailsActivity)getActivity();
        Artists =  activity.sendArtists();
        ArtistsList = parseArtists(Artists);
        view.findViewById(R.id.ArtistsProgressBar).setVisibility(View.GONE);

        if(ArtistsList.size() != 0) {
            ((TextView)view.findViewById(R.id.NoArtistsMessage)).setVisibility(View.GONE);
            RecyclerView recyclerView = view.findViewById(R.id.ArtistRecyclerView);
            ArtistListAdapter adapter = new ArtistListAdapter(getActivity(), ArtistsList);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }


        return view;
    }
}