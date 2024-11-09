package com.example.myapplication;

import static android.R.layout.select_dialog_item;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener  {

    Boolean isClicked = false;
    public ArrayList<String> getSuggestions(String value, AutoCompleteTextView actv) {

        ArrayList<String> suggestions = new ArrayList<String>();
        String url = "https://my-angular-app8.wl.r.appspot.com/suggest?keyword=" + value;
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest req = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if(!response.isNull("_embedded") &&
                            !response.getJSONObject("_embedded").isNull("attractions")) {
                        JSONArray Suggestions_arr = response.getJSONObject("_embedded").getJSONArray("attractions");
                        for(int i = 0; i<Suggestions_arr.length(); i++) {
                            suggestions.add(Suggestions_arr.getJSONObject(i).getString("name"));
                        }
                    }
                    Log.d("Suggestions", suggestions.toString());

                    ArrayAdapter<String> adapter_arr = new ArrayAdapter<String>
                            (getActivity(), R.layout.simple_spinner_dropdown_item, suggestions.toArray(new String[suggestions.size()]));
                    actv.setThreshold(1);
                    actv.setAdapter(adapter_arr);
                    actv.refreshAutoCompleteResults();
                    getView().findViewById(R.id.SuggestionProgressBar).setVisibility(View.GONE);
//                    android.R.layout.select_dialog_item
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
        return suggestions;
    }

    public SearchFragment() {

    }

    private Spinner spinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_search, container, false);

        view.findViewById(R.id.SuggestionProgressBar).setVisibility(View.GONE);

        AutoCompleteTextView actv = (AutoCompleteTextView) view.findViewById(R.id.keyword);

        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                isClicked = true;
            }
        });
        actv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("") && !isClicked) {
                    getSuggestions(s.toString(), actv);
                    view.findViewById(R.id.SuggestionProgressBar).setVisibility(View.VISIBLE);
                }
                isClicked = !isClicked;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });



        //Spinner


        spinner = view.findViewById(R.id.category);


        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this.getActivity(), R.array.category, R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Switch onOffSwitch = (Switch)  view.findViewById(R.id.location);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true) {
                    view.findViewById(R.id.locationText).setVisibility(View.GONE);

                }
                else {
                    view.findViewById(R.id.locationText).setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}