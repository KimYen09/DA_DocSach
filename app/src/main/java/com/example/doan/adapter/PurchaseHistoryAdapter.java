<<<<<<< HEAD
// Create new Java class: PurchaseHistoryAdapter.java
package com.example.doan.adapter; // Or your adapter package

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.premium.PurchaseRecord;

import java.util.List;

public class PurchaseHistoryAdapter extends RecyclerView.Adapter<PurchaseHistoryAdapter.PurchaseViewHolder> {

    private Context context;
    private List<PurchaseRecord> purchaseList;

    public PurchaseHistoryAdapter(Context context, List<PurchaseRecord> purchaseList) {
        this.context = context;
        this.purchaseList = purchaseList;
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchase_history, parent, false); // You'll create this layout
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        PurchaseRecord record = purchaseList.get(position);
        holder.tvPackageName.setText("Gói: " + record.getPackageName());
        holder.tvPackagePrice.setText("Giá: " + record.getPackagePrice());
        holder.tvPurchaseDate.setText("Ngày mua: " + record.getPurchaseDate());
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    static class PurchaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvPackageName, tvPackagePrice, tvPurchaseDate;

        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPackageName = itemView.findViewById(R.id.tvItemPackageName);
            tvPackagePrice = itemView.findViewById(R.id.tvItemPackagePrice);
            tvPurchaseDate = itemView.findViewById(R.id.tvItemPurchaseDate);
        }
    }
=======
// Create new Java class: PurchaseHistoryAdapter.java
package com.example.doan.adapter; // Or your adapter package

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.premium.PurchaseRecord;

import java.util.List;

public class PurchaseHistoryAdapter extends RecyclerView.Adapter<PurchaseHistoryAdapter.PurchaseViewHolder> {

    private Context context;
    private List<PurchaseRecord> purchaseList;

    public PurchaseHistoryAdapter(Context context, List<PurchaseRecord> purchaseList) {
        this.context = context;
        this.purchaseList = purchaseList;
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchase_history, parent, false); // You'll create this layout
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        PurchaseRecord record = purchaseList.get(position);
        holder.tvPackageName.setText("Gói: " + record.getPackageName());
        holder.tvPackagePrice.setText("Giá: " + record.getPackagePrice());
        holder.tvPurchaseDate.setText("Ngày mua: " + record.getPurchaseDate());
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    static class PurchaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvPackageName, tvPackagePrice, tvPurchaseDate;

        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPackageName = itemView.findViewById(R.id.tvItemPackageName);
            tvPackagePrice = itemView.findViewById(R.id.tvItemPackagePrice);
            tvPurchaseDate = itemView.findViewById(R.id.tvItemPurchaseDate);
        }
    }
>>>>>>> 544230bfed368006ff7c551f97449dac2f325339
}