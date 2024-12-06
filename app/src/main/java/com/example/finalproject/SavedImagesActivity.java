package com.example.finalproject;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SavedImagesActivity extends AppCompatActivity {
    private RecyclerView savedImagesRecyclerView;
    private ArrayList<ImageModel> imageList;
    private SavedImagesAdapter adapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_images);

        savedImagesRecyclerView = findViewById(R.id.savedImagesRecyclerView);
        databaseHelper = new DatabaseHelper(this);

        imageList = databaseHelper.getAllImages();
        adapter = new SavedImagesAdapter(this, imageList);

        // Set up the RecyclerView
        savedImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        savedImagesRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            ImageModel selectedImage = imageList.get(position);
            boolean isDeleted = databaseHelper.deleteImage(selectedImage.getId());

            if (isDeleted) {
                imageList.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(SavedImagesActivity.this, "Image deleted successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SavedImagesActivity.this, "Failed to delete image.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}