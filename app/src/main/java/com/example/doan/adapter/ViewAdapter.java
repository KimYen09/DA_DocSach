package com.example.doan.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.doan.fragmenthome.AdminFragment;
import com.example.doan.fragmenthome.HomeFragment;
import com.example.doan.fragmenthome.InfoFragment;
import com.example.doan.fragmenthome.LibraryFragment;
import com.example.doan.fragmenthome.SearchFragment;

public class ViewAdapter extends FragmentStateAdapter {

    public ViewAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new HomeFragment();
            case 1: return new SearchFragment();
            case 2: return new LibraryFragment();
            case 3: return new AdminFragment();
            case 4: return new InfoFragment();
            default: throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
