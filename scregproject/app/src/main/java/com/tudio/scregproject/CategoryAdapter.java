package com.tudio.scregproject;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<String> categoryList;
    private int selectedPosition = 0;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public CategoryAdapter(List<String> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categoryList.get(position);
        holder.textView.setText(category);

        if (selectedPosition == position) {
            // SEÇİLİ KATEGORİ
            holder.textView.setBackgroundResource(R.drawable.category_selected);
            holder.textView.setTextColor(Color.WHITE);

            // Fontu yükle
            Typeface typeface = ResourcesCompat.getFont(holder.textView.getContext(), R.font.gb);
            holder.textView.setTypeface(typeface);
        } else {
            // DİĞERLERİ (varsayılan stiller)
            holder.textView.setBackgroundResource(R.drawable.category_unselected);
            holder.textView.setTextColor(Color.BLACK);
            // Fontu yükle
            Typeface typeface = ResourcesCompat.getFont(holder.textView.getContext(), R.font.gr);
            holder.textView.setTypeface(typeface);
        }

        holder.textView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onCategoryClick(category);
        });
    }


    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewCategory);
        }
    }
}

