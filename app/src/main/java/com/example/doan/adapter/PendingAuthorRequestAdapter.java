package com.example.doan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.example.doan.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class PendingAuthorRequestAdapter extends RecyclerView.Adapter<PendingAuthorRequestAdapter.PendingAuthorViewHolder> {
    private Context context;
    private ArrayList<User> pendingRequestsList;

    public PendingAuthorRequestAdapter(Context context, ArrayList<User> pendingRequestsList) {
        this.context = context;
        this.pendingRequestsList = pendingRequestsList;
    }

    @NonNull
    @Override
    public PendingAuthorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_author_request, parent, false);
        return new PendingAuthorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingAuthorViewHolder holder, int position) {
        User user = pendingRequestsList.get(position);

        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(context).load(user.getAvatar()).into(holder.imgUserAvatar);
        } else {
            Glide.with(context).load(R.drawable.avatar).into(holder.imgUserAvatar);
        }

        holder.tvUserName.setText(user.getUsername());
        holder.tvUserEmail.setText(user.getEmail());
        holder.tvAuthorName.setText("Tên tác giả đăng ký: " + user.getAuthorName());
        holder.tvAuthorBio.setText("Giới thiệu: " + user.getAuthorBio());

        // Xử lý sự kiện khi nhấn nút Duyệt
        holder.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    User userToApprove = pendingRequestsList.get(adapterPosition);
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userToApprove.getUserId());
                    // Cập nhật trạng thái thành "approved" khi người dùng được duyệt
                    userRef.child("requestStatus").setValue("approved");
                    userRef.child("role").setValue("author");

                    pendingRequestsList.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                    Toast.makeText(context, "Đã duyệt yêu cầu của " + userToApprove.getUsername(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý sự kiện khi nhấn nút Từ chối
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    User userToReject = pendingRequestsList.get(adapterPosition);
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userToReject.getUserId());
                    // Cập nhật trạng thái thành "rejected" khi người dùng bị từ chối
                    userRef.child("requestStatus").setValue("rejected");
                    userRef.child("role").setValue("user");

                    pendingRequestsList.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                    Toast.makeText(context, "Đã từ chối yêu cầu của " + userToReject.getUsername(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pendingRequestsList.size();
    }

    public static class PendingAuthorViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgUserAvatar;
        TextView tvUserName;
        TextView tvUserEmail;
        TextView tvAuthorName;
        TextView tvAuthorBio;
        MaterialButton btnApprove;
        MaterialButton btnReject;

        public PendingAuthorViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvAuthorBio = itemView.findViewById(R.id.tvAuthorBio);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}