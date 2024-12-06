package com.example.finalproject;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private TextView dateTextView, imageTitleTextView, hdUrlTextView;
    private ImageView imageView;
    private Button fetchButton, saveButton, viewSavedButton;
    private String selectedDate, imageUrl, hdUrl;

    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;  // Declare ExecutorService

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTextView = findViewById(R.id.datePickerLabel);
        imageTitleTextView = findViewById(R.id.imageTitleTextView);
        hdUrlTextView = findViewById(R.id.hdUrlTextView);
        imageView = findViewById(R.id.imageView);
        fetchButton = findViewById(R.id.fetchButton);
        saveButton = findViewById(R.id.saveButton);
        viewSavedButton = findViewById(R.id.viewSavedButton);

        databaseHelper = new DatabaseHelper(this);

        executorService = Executors.newSingleThreadExecutor(); // Initialize ExecutorService

        // Date Picker
        dateTextView.setOnClickListener(v -> openDatePicker());

        // Fetch Image
        fetchButton.setOnClickListener(v -> {
            if (selectedDate == null) {
                Toast.makeText(this, "Please select a date first.", Toast.LENGTH_SHORT).show();
            } else {
                executorService.submit(() -> {
                    new FetchImageTask().execute(selectedDate);
                });
            }
        });

        // Save Image
        saveButton.setOnClickListener(v -> saveImage());

        // View Saved Images
        viewSavedButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SavedImagesActivity.class);
            startActivity(intent);
        });
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    dateTextView.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveImage() {
        if (selectedDate == null || imageUrl == null || hdUrl == null) {
            Toast.makeText(this, "No image to save.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = databaseHelper.insertImage(selectedDate, imageUrl, hdUrl);
        if (isInserted) {
            Toast.makeText(this, "Image saved successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save image.", Toast.LENGTH_SHORT).show();
        }
    }

    private class FetchImageTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String date = params[0];
            try {
                String apiKey = "EPmN5aN8f3lbPiGIVnO2G7kzvVWl1jlKiM33VShb";
                String apiUrl = "https://api.nasa.gov/planetary/apod?api_key=" + apiKey + "&date=" + date;

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return new JSONObject(response.toString());
            } catch (Exception e) {
                Log.e("FetchImageTask", "Error fetching image", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result == null) {
                Toast.makeText(MainActivity.this, "Failed to fetch image.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String title = result.getString("title");
                imageUrl = result.getString("url");
                hdUrl = result.getString("hdurl");

                imageTitleTextView.setText(title);
                hdUrlTextView.setText("HD Image: " + hdUrl);

                // Using Glide to load the image
                Glide.with(MainActivity.this)
                        .load(imageUrl)
                        .into(imageView);

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Error parsing image data.", Toast.LENGTH_SHORT).show();
                Log.e("FetchImageTask", "Error parsing JSON", e);
            }
        }
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "NasaApod.db";
        private static final String TABLE_NAME = "SavedImages";
        private static final String COL_ID = "ID";
        private static final String COL_DATE = "Date";
        private static final String COL_IMAGE_URL = "ImageUrl";
        private static final String COL_HD_URL = "HdUrl";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_DATE + " TEXT, " +
                    COL_IMAGE_URL + " TEXT, " +
                    COL_HD_URL + " TEXT)";
            db.execSQL(createTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        public boolean insertImage(String date, String imageUrl, String hdUrl) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_DATE, date);
            contentValues.put(COL_IMAGE_URL, imageUrl);
            contentValues.put(COL_HD_URL, hdUrl);

            long result = db.insert(TABLE_NAME, null, contentValues);
            db.close();
            return result != -1;
        }

        public ArrayList<ImageModel> getAllImages() {
            SQLiteDatabase db = this.getReadableDatabase();
            ArrayList<ImageModel> imageList = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String date = cursor.getString(1);
                String imageUrl = cursor.getString(2);
                String hdUrl = cursor.getString(3);
                imageList.add(new ImageModel(id, date, imageUrl, hdUrl));
            }
            cursor.close();
            db.close();
            return imageList;
        }

        public boolean deleteImage(int id) {
            SQLiteDatabase db = this.getWritableDatabase();
            int result = db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
            db.close();
            return result > 0;
        }
    }
}