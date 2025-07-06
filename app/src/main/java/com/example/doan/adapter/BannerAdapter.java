package com.example.doan.adapter;

import com.example.doan.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private final List<Integer> bannerImages;

    public BannerAdapter() {
        this.bannerImages = new ArrayList<>();
    }

    public BannerAdapter(List<Integer> bannerImages) {
        this.bannerImages = (bannerImages != null) ? bannerImages : new ArrayList<>();
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        holder.itemView.setLayoutParams(layoutParams);

        if (!bannerImages.isEmpty()) {
            int realPosition = position % bannerImages.size();
            holder.bannerImage.setImageResource(bannerImages.get(realPosition));
        }
    }

    @Override
    public int getItemCount() {
        return (!bannerImages.isEmpty()) ? bannerImages.size() * 1000 : 1;
    }

    public void setBannerImages(List<Integer> newImages) {
        if (newImages != null) {
            this.bannerImages.clear();
            this.bannerImages.addAll(newImages);
            notifyDataSetChanged();
        }
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.bannerImage);
        }
    }
}
