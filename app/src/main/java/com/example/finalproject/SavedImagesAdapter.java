package com.example.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SavedImagesAdapter extends RecyclerView.Adapter<SavedImagesAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ImageModel> imageList;
    private OnItemClickListener listener;

    public SavedImagesAdapter(Context context, ArrayList<ImageModel> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageModel image = imageList.get(position);


        holder.savedImageDateTextView.setText(image.getDate());  

        // Loading image using Glide
        Glide.with(context)
                .load(image.getHdUrl())
                .into(holder.savedImageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView savedImageDateTextView;
        ImageView savedImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            savedImageDateTextView = itemView.findViewById(R.id.savedImageDateTextView);
            savedImageView = itemView.findViewById(R.id.savedImageView); // Initialize the ImageView
        }
    }

    // Set the item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}