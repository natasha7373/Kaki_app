package com.example.kakihomeui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class addpost extends AppCompatActivity {

    FirebaseAuth mAuth;
    String currentUserID;
    DatabaseReference UsersReference;
    DatabaseReference UsersRef;
    FirebaseDatabase mDatabase;
    FirebaseUser user;
    ImageButton back;
    Button submit;
    EditText title, location, date, time, desc, attendees;
    private ProgressDialog loadingBar;

    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";

    // key for storing email.
    public static final String EMAIL_KEY = "email_key";

    // key for storing password.
    public static final String PASSWORD_KEY = "password_key";

    SharedPreferences sharedpreferences;
    String semail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);

        getSupportActionBar().setTitle("Add Post");

        mAuth = FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance();
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");

        title = findViewById(R.id.noticetitlebanner);
        location = findViewById(R.id.location_banner);
        date = findViewById(R.id.date_banner);
        time = findViewById(R.id.time_banner);
        desc = findViewById(R.id.noticedescbanner);
        attendees = findViewById(R.id.attendee_banner);
        submit = findViewById(R.id.post);
        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // getting data from shared prefs and
        // storing it in our string variable.
        semail = sharedpreferences.getString(EMAIL_KEY, null);
        loadingBar = new ProgressDialog(this);

        ImageButton button;
        button = findViewById(R.id.exit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPost();
            }
        });

    }

    public void uploadPost() {
        String postTitle = title.getText().toString();
        String postLocation = location.getText().toString();
        String postDate = date.getText().toString();
        String postTime = time.getText().toString();
        String postDesc = desc.getText().toString();
        String postAttendees = attendees.getText().toString();
        if (TextUtils.isEmpty(postTitle)) {
            title.setError("Please enter title.");
            title.requestFocus();
            return;
        } else if (TextUtils.isEmpty(postLocation)) {
            location.setError("Please enter a location.");
            location.requestFocus();
            return;
        } else if (TextUtils.isEmpty(postDate)) {
            date.setError("Please enter a date.");
            date.requestFocus();
            return;
        } else if (TextUtils.isEmpty(postTime)) {
            time.setError("Please enter a time.");
            time.requestFocus();
            return;
        } else if (TextUtils.isEmpty(postDesc)) {
            desc.setError("Please enter a description.");
            desc.requestFocus();
            return;
        } else if (TextUtils.isEmpty(postAttendees)) {
            attendees.setError("Please enter number of attendees.");
            attendees.requestFocus();
            return;
        } else {
            loadingBar.setTitle("Create New Post");
            loadingBar.setMessage("Please wait, while we create your new post.");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);
            addPostToDB(postTitle, postLocation, postDate, postTime, postDesc, postAttendees);
        }
    }

    public void addPostToDB(final String postTitle, final String postLocation, final String postDate, final String postTime, final String postDesc, final String postAttendees) {
        final FirebaseDatabase database=FirebaseDatabase.getInstance("https://kaki-real-default-rtdb.asia-southeast1.firebasedatabase.app");
        UsersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String email= semail;
                    Calendar calForDate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yy");
                    final String saveCurrentDate = currentDate.format(calForDate.getTime());

                    UsersReference=database.getReference().child("Posts").child("postID");
                    String postID = UsersReference.push().getKey();
                    HashMap userMap = new HashMap();
                    userMap.put("email", email);
                    userMap.put("title", postTitle);
                    userMap.put("location", postLocation);
                    userMap.put("date", postDate);
                    userMap.put("time", postTime);
                    userMap.put("description", postDesc);
                    userMap.put("attendees", postAttendees);
                    userMap.put("datePosted",saveCurrentDate);
                    UsersReference.child(postID).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(),"Posts added successfully",Toast.LENGTH_LONG).show();
                                Intent i=new Intent(addpost.this,MainActivity.class);
                                startActivity(i);
                                finish();
                                return;
                            }
                            else {
                                Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}