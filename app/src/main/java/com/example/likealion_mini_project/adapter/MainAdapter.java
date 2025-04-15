package com.example.likealion_mini_project.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.likealion_mini_project.DetailActivity;
import com.example.likealion_mini_project.R;
import com.example.likealion_mini_project.databinding.ItemMainBinding;
import com.example.likealion_mini_project.model.Student;
import com.example.likealion_mini_project.util.BitmapUtil;
import com.example.likealion_mini_project.util.DialogUtil;

import java.util.ArrayList;

class MainViewHolder extends RecyclerView.ViewHolder {
    ItemMainBinding binding;

    MainViewHolder(ItemMainBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}

public class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {

    private final ArrayList<Student> students;
    private final Activity context;

    public MainAdapter(ArrayList<Student> students, Activity context) {
        this.students = students;
        this.context = context;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMainBinding binding = ItemMainBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        Student student = students.get(position);
        Bitmap bitmap = BitmapUtil.getGalleryImageFromFile(context, student.getPhoto());


        holder.binding.tvName.setText(student.getName());
        if (bitmap != null) {
            holder.binding.ivProfile.setImageBitmap(bitmap);
        }



        holder.binding.ivProfile.setOnClickListener(v -> {
            if (bitmap != null) DialogUtil.showCustomDialog(context, bitmap);
            else DialogUtil.showCustomDialog(context, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_student_large));
        });

        holder.binding.ivCall.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                callPhone(student.getPhone());
            } else {
                DialogUtil.showToast(context, context.getString(R.string.permission_denied));
            }

        });

        holder.binding.tvName.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);

            // primary key 넘겨주기
            intent.putExtra("id", student.getId());

            // 되돌아왔을 때 사후처리 고려  // 없음 !
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return students.size();
    }

    private void callPhone(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
            context.startActivity(intent);
        } else {
            DialogUtil.showToast(context, context.getString(R.string.main_list_phone_error));
        }
    }
}
