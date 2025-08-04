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

        holder.tvPackageName.setText(record.getPackageName());
        holder.tvPackagePrice.setText(record.getPackagePrice());
        holder.tvPurchaseDate.setText("Ngày mua: " + record.getPurchaseDate());
        holder.tvPurchaseId.setText("ID: " + record.getPurchaseId());
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    static class PurchaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvPackageName, tvPackagePrice, tvPurchaseDate, tvPurchaseId;

        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPackageName = itemView.findViewById(R.id.tvItemPackageName);
            tvPackagePrice = itemView.findViewById(R.id.tvItemPackagePrice);
            tvPurchaseDate = itemView.findViewById(R.id.tvItemPurchaseDate);
            tvPurchaseId = itemView.findViewById(R.id.tvItemPurchaseId);
        }
    }
}