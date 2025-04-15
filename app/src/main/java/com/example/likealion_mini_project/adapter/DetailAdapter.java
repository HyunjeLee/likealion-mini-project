package com.example.likealion_mini_project.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.likealion_mini_project.databinding.ItemDetailBinding;

import java.util.ArrayList;
import java.util.Map;

class DetailViewHolder extends RecyclerView.ViewHolder {

    ItemDetailBinding binding;
    public DetailViewHolder(ItemDetailBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}

public class DetailAdapter extends RecyclerView.Adapter<DetailViewHolder> {

    ArrayList<Map<String, String>> dataArrayList;
    Activity context;

    public DetailAdapter(Activity context, ArrayList<Map<String, String>> dataArrayList) {
        this.context = context;
        this.dataArrayList = dataArrayList;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetailBinding binding = ItemDetailBinding.inflate(LayoutInflater.from(context));
        return new DetailViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {

        holder.binding.tvScore.setText(dataArrayList.get(position).get("score"));
        holder.binding.tvDate.setText(dataArrayList.get(position).get("date"));
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }
}
