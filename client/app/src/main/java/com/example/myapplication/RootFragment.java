package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RootFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_root, container, false);

        SearchFragment newFragment = new SearchFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.root_frame, newFragment);
        transaction.commit();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getFragmentManager().findFragmentByTag("EventsTag") != null) {
            EventTableFragment fragment = (EventTableFragment) getFragmentManager().findFragmentByTag("EventsTag");
            fragment.onRefresh();
        }
    }
}