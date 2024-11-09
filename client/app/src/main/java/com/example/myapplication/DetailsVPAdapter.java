package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DetailsVPAdapter extends FragmentStateAdapter {
    public DetailsVPAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DetailsFragment();
            case 1:
                return new ArtistFragment();
            case 2:
                return new VenueFragment();
            default:
                return new DetailsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
