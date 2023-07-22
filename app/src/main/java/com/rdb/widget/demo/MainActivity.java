package com.rdb.widget.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.rdb.widget.DragBar;
import com.rdb.widget.HorizontalProgressBar;
import com.rdb.widget.PolygonProgressBar;
import com.rdb.widget.demo.R;
import com.rdb.widget.RadioButton;
import com.rdb.widget.RatingBar;
import com.rdb.widget.RingProgressBar;
import com.rdb.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress(50);
        seekBar.setOnProgressChangeListener(new DragBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DragBar seekBar, int progress, boolean fromUser) {
                Log.e(TAG, "onProgressChanged  " + progress + " " + fromUser);
            }
        });
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setProgress(50);
        ratingBar.setNumStars(15);
        ratingBar.setOnProgressChangeListener(new DragBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DragBar dragBar, int progress, boolean fromUser) {
                Log.e(TAG, "onRatingChanged  " + progress + " " + fromUser);
            }
        });
        final HorizontalProgressBar progressBar1 = findViewById(R.id.progressBar1);
        progressBar1.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                progressBar1.setProgress((int) (Math.random() * 100));
            }
        });
        final RingProgressBar progressBar11 = findViewById(R.id.progressBar11);
        progressBar11.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                progressBar11.setProgress((int) (Math.random() * 100));
            }
        });
        final RingProgressBar progressBar12 = findViewById(R.id.progressBar12);
        progressBar12.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                progressBar12.setProgress((int) (Math.random() * 100));
            }
        });
        final RingProgressBar progressBar13 = findViewById(R.id.progressBar13);
        progressBar13.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                progressBar13.setProgress((int) (Math.random() * 100));
            }
        });
        final RingProgressBar progressBar14 = findViewById(R.id.progressBar14);
        progressBar14.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                progressBar14.setProgress((int) (Math.random() * 100));
            }
        });

        final PolygonProgressBar progressBar41 = findViewById(R.id.progressBar41);
        progressBar41.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                progressBar41.setProgress((int) (Math.random() * 100));
            }
        });

        final PolygonProgressBar progressBar42 = findViewById(R.id.progressBar42);
        progressBar42.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                progressBar42.setProgress((int) (Math.random() * 100));
            }
        });

        final PolygonProgressBar progressBar43 = findViewById(R.id.progressBar43);
        progressBar43.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                progressBar43.setProgress((int) (Math.random() * 100));
            }
        });

        final PolygonProgressBar progressBar44 = findViewById(R.id.progressBar44);
        progressBar44.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar44.setProgress((int) (Math.random() * 100));
            }
        });

        RadioButton radioButton1 = findViewById(R.id.radio1);
        RadioButton radioButton2 = findViewById(R.id.radio2);
        RadioButton radioButton3 = findViewById(R.id.radio3);
        radioButton1.group(new RadioButton.OnGroupCheckedChangeListener() {
            @Override
            public void onCheckedChanged(int checkedId) {
                Log.e(TAG, "onCheckedChanged  " + checkedId);
            }
        }, radioButton2, radioButton3);
    }
}