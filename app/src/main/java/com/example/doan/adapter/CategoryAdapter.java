package com.example.doan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.doan.R;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> categories;
    private final boolean[] selectedItems;

    public CategoryAdapter(Context context, List<String> categories) {
        this.context = context;
        this.categories = categories;
        this.selectedItems = new boolean[categories.size()];
    }


    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        }

        TextView txtCategory = convertView.findViewById(R.id.txtCategory);
        txtCategory.setText(categories.get(position));

        // Cập nhật trạng thái khi chọn
        txtCategory.setSelected(selectedItems[position]);

        txtCategory.setOnClickListener(v -> {
            selectedItems[position] = !selectedItems[position];
            notifyDataSetChanged();
        });

        return convertView;
    }
}


