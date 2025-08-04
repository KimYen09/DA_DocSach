package com.example.doan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.model.PremiumPackage;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PremiumPackageAdapter extends RecyclerView.Adapter<PremiumPackageAdapter.ViewHolder> {

    private List<PremiumPackage> packageList;
    private OnPremiumPackageListener listener;

    public interface OnPremiumPackageListener {
        void onEditClick(PremiumPackage package_);
        void onDeleteClick(PremiumPackage package_);
        // Thêm methods để khớp với PremiumManagementActivity
        void onEditPackage(PremiumPackage premiumPackage);
        void onDeletePackage(PremiumPackage premiumPackage);
        void onToggleActiveStatus(PremiumPackage premiumPackage, boolean isActive);
    }

    public PremiumPackageAdapter(List<PremiumPackage> packageList, OnPremiumPackageListener listener) {
        this.packageList = packageList;
        this.listener = listener;
    }

    public void updateData(List<PremiumPackage> newList) {
        this.packageList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_premium_package, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PremiumPackage package_ = packageList.get(position);

        holder.tvPackageName.setText(package_.getName());
        holder.tvPackageDescription.setText(package_.getDescription());

        // Format giá tiền
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPackagePrice.setText(formatter.format(package_.getPrice()));

        // Hiển thị thời hạn
        String durationText = package_.getDuration() + " ngày";
        if (package_.getDuration() == 30) {
            durationText = "1 tháng";
        } else if (package_.getDuration() == 180) {
            durationText = "6 tháng";
        } else if (package_.getDuration() == 365) {
            durationText = "1 năm";
        }
        holder.tvPackageDuration.setText(durationText);

        // Hiển thị tính năng
        holder.tvPackageFeatures.setText(package_.getFeatures());

        // Hiển thị ngày tạo
        holder.tvCreatedDate.setText("Tạo: " + package_.getCreatedDate());

        // Hiển thị trạng thái
        holder.tvStatus.setText(package_.isActive() ? "Hoạt động" : "Tạm dừng");
        holder.tvStatus.setTextColor(package_.isActive() ?
                holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark) :
                holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));

        // Xử lý click events - sử dụng cả hai phương thức để tương thích
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(package_);
                listener.onEditPackage(package_);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(package_);
                listener.onDeletePackage(package_);
            }
        });
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPackageName, tvPackageDescription, tvPackagePrice, tvPackageDuration;
        TextView tvPackageFeatures, tvCreatedDate, tvStatus;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPackageName = itemView.findViewById(R.id.tvPackageName);
            tvPackageDescription = itemView.findViewById(R.id.tvPackageDescription);
            tvPackagePrice = itemView.findViewById(R.id.tvPackagePrice);
            tvPackageDuration = itemView.findViewById(R.id.tvPackageDuration);
            tvPackageFeatures = itemView.findViewById(R.id.tvPackageFeatures);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}