package com.example.doan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.model.PremiumPackage;

import java.util.List;

public class PremiumPackageAdapter extends RecyclerView.Adapter<PremiumPackageAdapter.PremiumViewHolder> {
    private List<PremiumPackage> packageList;
    private OnPackageActionListener listener;

    public interface OnPackageActionListener {
        void onEditPackage(PremiumPackage premiumPackage);
        void onDeletePackage(PremiumPackage premiumPackage);
    }

    public PremiumPackageAdapter(List<PremiumPackage> packageList, OnPackageActionListener listener) {
        this.packageList = packageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PremiumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_premium_package, parent, false);
        return new PremiumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PremiumViewHolder holder, int position) {
        PremiumPackage pkg = packageList.get(position);
        holder.tvPackageName.setText(pkg.getName());
        holder.tvCreatedDate.setText("Ngày tạo: " + pkg.getCreatedDate());
        holder.tvDescription.setText(pkg.getDescription());
        holder.tvPrice.setText(pkg.getFormattedPrice());
        holder.tvDuration.setText(pkg.getDurationText());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditPackage(pkg);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeletePackage(pkg);
            }
        });
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public void updateData(List<PremiumPackage> newPackageList) {
        this.packageList = newPackageList;
        notifyDataSetChanged();
    }

    static class PremiumViewHolder extends RecyclerView.ViewHolder {
        TextView tvPackageName, tvCreatedDate, tvDescription, tvPrice, tvDuration;
        ImageButton btnEdit, btnDelete;

        public PremiumViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPackageName = itemView.findViewById(R.id.tvPackageName);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
