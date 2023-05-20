package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    String Id, Name, is_available, EventsJSON;
    JSONObject DetailsData;
    JSONArray ArtistsData;
    JSONObject VenueData;

    Bundle bundle = new Bundle();

    public JSONObject sendDetails() {
        return DetailsData;
    }

    public JSONArray sendArtists() {
        return ArtistsData;
    }
    public JSONObject sendVenue() {
        return VenueData;
    }

    public void getValues(String Id) {
        RequestQueue queue = Volley.newRequestQueue(DetailsActivity.this);

        String detailsUrl = "https://my-angular-app8.wl.r.appspot.com/search/id?eventid=" + Id;

        Log.d("url", detailsUrl);
        JsonObjectRequest req = new JsonObjectRequest(detailsUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    DetailsData = response;

                    //Get Artists

                    ArrayList<String> MusicArtistTeam = new ArrayList<String>();

                    JSONArray MusicList = DetailsData.getJSONObject("_embedded").getJSONArray("attractions");
                    for(int i = 0; i<MusicList.length(); i++) {
                        if(MusicList.getJSONObject(i).getJSONArray("classifications")
                                .getJSONObject(0).getJSONObject("segment")
                                .getString("name").equals("Music")) {
                            MusicArtistTeam.add(MusicList.getJSONObject(i).getString("name"));
                        }
                    }
                    String MusicFinal = "[";
                    for(int i = 0; i<MusicArtistTeam.size(); i++) {
                        MusicFinal += '"'+ MusicArtistTeam.get(i).toString() + '"';
                        if(i != MusicArtistTeam.size()-1) {
                            MusicFinal += ',';
                        }
                    }
                    MusicFinal += "]";
                    Log.d("Music Team", MusicFinal);

                    MusicFinal = MusicFinal.replaceAll("&", "%26");
                    String ArtistsUrl = "https://my-angular-app8.wl.r.appspot.com/spotify?name=" + MusicFinal;

                    // Get Venue
                    RequestQueue queue = Volley.newRequestQueue(DetailsActivity.this);

                    JsonArrayRequest req = new JsonArrayRequest(ArtistsUrl, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                ArtistsData = response;

                                String venue_name = DetailsData.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");

                                RequestQueue queue = Volley.newRequestQueue(DetailsActivity.this);

                                String venueUrl = "https://my-angular-app8.wl.r.appspot.com/search/venue?venue=" + venue_name;
                                JsonObjectRequest req = new JsonObjectRequest(venueUrl, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            VenueData = response;

                                            CreateDetailsPage();

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
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


    public void CreateDetailsPage() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.DetailsTab); // get the reference of TabLayout

        ViewPager2 DetailsviewPager2 = findViewById(R.id.DetailsPager2);
        DetailsVPAdapter Detailsvpadapter = new DetailsVPAdapter(this);

        DetailsviewPager2.setAdapter(Detailsvpadapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                DetailsviewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        DetailsviewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

    }

    public void ShowFacebook() {
        Uri uri = null;
        try {
            uri = Uri.parse("https://www.facebook.com/sharer/sharer.php?u="+ DetailsData.getString("url") +"&quote=Check%20"+ Name +"%20at%20ticketmaster.");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void ShowTwitter() {
        Uri uri = null;
        try {
            uri = Uri.parse("https://twitter.com/intent/tweet?url=" + DetailsData.getString("url")+ "&text=Check%20"+ Name +"%20at%20ticketmaster.");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.facebook:
                ShowFacebook();
                return true;
            case R.id.twitter:
                ShowTwitter();
                return true;
            case R.id.favButton:
                SharedPreferences sharedPref = this.getSharedPreferences(
                        "ForSavingEventsToFav1", this.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if(is_available.equals("")) {
                    editor.putString(Id, EventsJSON);
                    editor.apply();
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.DetailsLayout), Name + " added to favorites", Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.parseColor("#9b9b9b"));
                    snackbar.setTextColor(Color.parseColor("#000000"));
                    snackbar.show();
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.heart_filled));
                }
                else {
                    editor.remove(Id);
                    editor.apply();
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.DetailsLayout), Name + " removed from favorites", Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.parseColor("#9b9b9b"));
                    snackbar.setTextColor(Color.parseColor("#000000"));
                    snackbar.show();
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.heart_outline));
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_action_bar, menu);
        MenuItem item = menu.findItem(R.id.favButton);
        if(is_available.equals("")) {
            item.setIcon(ContextCompat.getDrawable(this, R.drawable.heart_outline));
        }
        else {
            item.setIcon(ContextCompat.getDrawable(this, R.drawable.heart_filled));
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.grey)));

        View actionBarView = getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(actionBarView);

        TextView myTextView = actionBarView.findViewById(R.id.my_textview);
        ImageButton imageButton = actionBarView.findViewById(R.id.backActivity);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


//        getSupportActionBar().setCustomView(R.layout.activity_details);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.DetailsTab); // get the reference of TabLayout
        TabLayout.Tab detailsTab = tabLayout.newTab();
        TabLayout.Tab artistTab = tabLayout.newTab();
        TabLayout.Tab venueTab = tabLayout.newTab();

        tabLayout.addTab(detailsTab,true);
        tabLayout.addTab(artistTab);
        tabLayout.addTab(venueTab);


        detailsTab.setCustomView(R.layout.custom_tab_layout);
        artistTab.setCustomView(R.layout.custom_tab_layout);
        venueTab.setCustomView(R.layout.custom_tab_layout);

        View tabView = tabLayout.getTabAt(0).getCustomView();
        ImageView tabIcon = tabView.findViewById(R.id.tab_icon);
        TextView tabText = tabView.findViewById(R.id.tab_text);
        tabIcon.setImageResource(R.drawable.info_icon);
        tabText.setText("DETAILS");
        tabText.setTextColor(Color.parseColor("#4CAF50"));

        tabView = tabLayout.getTabAt(1).getCustomView();
        tabIcon = tabView.findViewById(R.id.tab_icon);
        tabText = tabView.findViewById(R.id.tab_text);
        Drawable drawable = getDrawable(R.drawable.artist_icon);
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        tabIcon.setImageDrawable(drawable);
        tabText.setText("ARTIST(S)");

        tabView = tabLayout.getTabAt(2).getCustomView();
        tabIcon = tabView.findViewById(R.id.tab_icon);
        tabText = tabView.findViewById(R.id.tab_text);
        drawable = getDrawable(R.drawable.venue_icon);
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        tabIcon.setImageDrawable(drawable);
        tabText.setText("VENUE");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View tabView = tab.getCustomView();
                ((TextView)tabView.findViewById(R.id.tab_text)).setTextColor(Color.parseColor("#4CAF50"));
                ImageView tabIcon = tabView.findViewById(R.id.tab_icon);
                Drawable drawable = tabIcon.getDrawable();
                drawable.setColorFilter(Color.parseColor("#50C31B"), PorterDuff.Mode.SRC_IN);
                tabIcon.setImageDrawable(drawable);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View tabView = tab.getCustomView();
                ((TextView)tabView.findViewById(R.id.tab_text)).setTextColor(Color.WHITE);
                ImageView tabIcon = tabView.findViewById(R.id.tab_icon);
                Drawable drawable = tabIcon.getDrawable();
                drawable.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                tabIcon.setImageDrawable(drawable);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });



        Bundle extras = getIntent().getExtras();
        Id = extras.getString("EventDetailsId");
        Name = extras.getString("EventDetailsName");
        EventsJSON = extras.getString("EventDetailsJSON");

        SharedPreferences sharedPref = getSharedPreferences(
                "ForSavingEventsToFav1", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        is_available = sharedPref.getString(Id,"");

        myTextView.setText(Name);
        myTextView.setSelected(true);
//       getSupportActionBar().setTitle(Name);

        getValues(Id);
    }
}