package com.example.kakihomeui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class viewpost extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpost);

        ImageButton button;
        button = findViewById(R.id.exit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String email = getIntent().getStringExtra("EMAIL");
        String date = getIntent().getStringExtra("DATE");
        String dateP = getIntent().getStringExtra("DATE POSTED");
        String des = getIntent().getStringExtra("DES");
        String title = getIntent().getStringExtra("TITLE");
        String loc = getIntent().getStringExtra("LOC");
        String attendees = getIntent().getStringExtra("ATTENDEES");
        String time = getIntent().getStringExtra("TIME");

        TextView tEmail = findViewById(R.id.idEmail);
        TextView tDate = findViewById(R.id.idDate);
        TextView tDateP = findViewById(R.id.idDatepost);
        TextView tDes = findViewById(R.id.idDesc);
        TextView tTitle = findViewById(R.id.idTitle);
        TextView tLoc = findViewById(R.id.idLoc);
        TextView tAttendees = findViewById(R.id.idAttendees);
        TextView tTime = findViewById(R.id.idTime);

        tEmail.setText(email);
        tDate.setText(date);
        tDateP.setText(dateP);
        tDes.setText(des);
        tTitle.setText(title);
        tLoc.setText(loc);
        tAttendees.setText(attendees);
        tTime.setText(time);



    }
}