package com.example.doan.adapter; // Tạo package mới cho adapters admin

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.example.doan.model.User; // Sử dụng lại User model
import com.google.android.material.button.MaterialButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PendingAuthorRequestAdapter extends RecyclerView.Adapter<PendingAuthorRequestAdapter.RequestViewHolder> {

    private Context context;
    private List<User> requestList; // Danh sách các User có yêu cầu chờ duyệt
    private OnRequestListener listener;

    public interface OnRequestListener {
        void onApprove(User user);
        void onReject(User user);
    }

    public PendingAuthorRequestAdapter(Context context, List<User> requestList, OnRequestListener listener) {
        this.context = context;
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_author_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        User user = requestList.get(position);
        if (user != null) {
            holder.tvUserName.setText(user.getUsername() != null ? user.getUsername() : "N/A");
            holder.tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
            holder.tvAuthorBio.setText("Giới thiệu: " + (user.getAuthorBio() != null ? user.getAuthorBio() : "Chưa có giới thiệu"));

            // Tải ảnh đại diện
            // Đã sửa từ user.getAvatar() thành user.getAvatarUrl() để khớp với User model và JSON
            String avatarName = user.getAvatar();
            if (avatarName != null && !avatarName.isEmpty()) {
                int resourceId = context.getResources().getIdentifier(
                        avatarName, "drawable", context.getPackageName());
                if (resourceId != 0) {
                    Glide.with(context).load(resourceId).into(holder.imgUserAvatar);
                } else {
                    Glide.with(context).load(avatarName)
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(holder.imgUserAvatar);
                }
            } else {
                holder.imgUserAvatar.setImageResource(R.drawable.avatar);
            }

            // Set OnClickListeners cho các nút duyệt/từ chối
            holder.btnApprove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onApprove(user);
                }
            });

            holder.btnReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReject(user);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgUserAvatar;
        TextView tvUserName, tvUserEmail, tvAuthorName, tvAuthorBio;
        MaterialButton btnApprove, btnReject;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvAuthorBio = itemView.findViewById(R.id.tvAuthorBio);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
