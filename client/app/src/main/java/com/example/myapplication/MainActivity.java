package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.Manifest;

public class MainActivity extends AppCompatActivity {

    // Code used from ChatGPT
    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        if (fineLocationGranted != null && fineLocationGranted) {

                        } else if (coarseLocationGranted != null && coarseLocationGranted) {

                        } else {

                        }
                    }
            );


    String keyword;
    String category;
    String distanceString;
    int distance = 10;
    boolean location;
    String locationText;

    public void searchEvents(View v) {

        keyword = ((EditText)findViewById(R.id.keyword)).getText().toString();
        distanceString = ((EditText)findViewById(R.id.distance)).getText().toString();
        distance = 10;
        if(!distanceString.equals("")) distance = Integer.parseInt(distanceString);
        category = ((Spinner)findViewById(R.id.category)).getSelectedItem().toString();
        location = ((Switch)findViewById(R.id.location)).isChecked();
        locationText = ((EditText)findViewById(R.id.locationText)).getText().toString();

        Log.d("values", keyword + ' ' + ' ' + String.valueOf(distance) + ' ' + category + ' ' + location + ' ' + locationText);

        if(location == true) {

            String url = "https://ipinfo.io/?token=7fdf450033aa3c";
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest req = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String latitude = response.getString("loc").substring(0, response.getString("loc").indexOf(','));
                                String longitude = response.getString("loc").substring(response.getString("loc").indexOf(',')+1);
                                getEvents(keyword, distance, category, latitude, longitude);
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
        else {
            String url =  "https://maps.googleapis.com/maps/api/geocode/json?address=" + locationText + "&key=AIzaSyDDk9nogjHpNyOVEvllMjcfcpq9RzuScJM";

            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest req = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d("Google", response.toString());
                        JSONArray arr = response.getJSONArray("results");

                        if(arr.length() != 0) {
                            String latitude = arr.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat");
                            String longitude = arr.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng");

                            getEvents(keyword, distance, category, latitude, longitude);
                        }
                        else {
                            getEvents(keyword, distance, category, null, null);
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
    }

    public void getEvents(String keyword, int distance, String category, String latitude, String longitude) {
        if(category.equals("All")) {
            category = "Default";
        }
        Log.d("TicketMaster", keyword + String.valueOf(distance) + category + latitude + longitude);
        String url =  "https://my-angular-app8.wl.r.appspot.com/search/events"
                + "?keyword=" + keyword
                + "&category=" + category
                + "&distance=" + distance
                +"&latitude=" + latitude
                + "&longitude=" + longitude;

        Bundle bundle = new Bundle();
        bundle.putString("EventsUrl", url);
        if(latitude == null || longitude == null)
            bundle.putString("EventsNoLoc", "true");
        else
            bundle.putString("EventsNoLoc", "false");

        EventTableFragment EventFrag = new EventTableFragment();
        EventFrag.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.slide_out,
                        R.anim.slide_in,
                        R.anim.slide_out
                )
                .replace(R.id.root_frame, EventFrag, "EventsTag")
                .addToBackStack(null)
                .commit();
    }

    public void checkEvents(View v) {
        String keyword = ((EditText)findViewById(R.id.keyword)).getText().toString();
        if(keyword.trim().equals("")) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.MainLayout), "Keyword Missing", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(Color.parseColor("#9b9b9b"));
            snackbar.setTextColor(Color.parseColor("#000000"));
            snackbar.show();
            return;
        }
        if(!((Switch)findViewById(R.id.location)).isChecked()) {
            String LocationText = ((EditText)findViewById(R.id.locationText)).getText().toString().trim();
            if(LocationText.equals("")) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.MainLayout), "Location Missing", Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(Color.parseColor("#9b9b9b"));
                snackbar.setTextColor(Color.parseColor("#000000"));
                snackbar.show();
                return;
            }
        }
        searchEvents(v);
    }

    public void clearEvents(View v) {
        ((EditText)findViewById(R.id.keyword)).setText("");
        ((EditText)findViewById(R.id.distance)).setText("10");
        ((Spinner)findViewById(R.id.category)).setSelection(0);
        ((Switch)findViewById(R.id.location)).setChecked(false);
        ((EditText)findViewById(R.id.locationText)).setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.grey)));

        ActionBar actionBar = getSupportActionBar(); // get reference to the ActionBar
        SpannableString title = new SpannableString("EventFinder");
        title.setSpan(new ForegroundColorSpan(Color.parseColor("#8BC34A")), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(title);

        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.mainTab); // get the reference of TabLayout
        TabLayout.Tab searchTab = tabLayout.newTab();
        TabLayout.Tab favouriteTab = tabLayout.newTab();
        searchTab.setText("Search");
        favouriteTab.setText("Favorite");
        tabLayout.addTab(searchTab,true);
        tabLayout.addTab(favouriteTab);

        ViewPager2 viewPager2 = findViewById(R.id.viewpager2);
        VPAdapter myvpadapter = new VPAdapter(this);

        viewPager2.setAdapter(myvpadapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }
}