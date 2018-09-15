package com.example.firat.bodyshape;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.firat.bodyshapeocv.R;

public class ShowCalculation extends AppCompatActivity {

    TextView txtView;


    String value ="OVAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        txtView = findViewById(R.id.textView);
        Bundle extras = getIntent().getExtras();

        //String value = extras.getString("key");

        String s = getIntent().getStringExtra("key");
        txtView.setText(value);
        setContentView(R.layout.activity_show_calculation);

            //The key argument here must match that used in the other activity

    }
}
