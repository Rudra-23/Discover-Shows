package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class VPAdapter extends FragmentStateAdapter {
    public VPAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RootFragment();
            case 1:
                return new FavoriteFragment();
            default:
                return new RootFragment();
        }
    }


    @Override
    public int getItemCount() {
        return 2;
    }



}
