package com.example.kakihomeui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://kaki-real-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

    private final List<MyItems> myItemsList = new ArrayList<>();
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
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
        setContentView(R.layout.activity_main);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
        if(acc!=null){
            String accName = acc.getDisplayName();
            String accEmail = acc.getEmail();
            //name.setText(accName);
            //email.setText(accEmail);
        }
        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // getting data from shared prefs and
        // storing it in our string variable.
        semail = sharedpreferences.getString(EMAIL_KEY, null);

        // initializing our textview and button.
        TextView welcomeTV = findViewById(R.id.idTVWelcome);
        welcomeTV.setText("Welcome \n"+semail);

        // getting Recycle View from xml file
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // setting recyclerView size fixed for every item in the recycleView
        recyclerView.setHasFixedSize(true);

        // setting layout manager to the recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // clear old item/ users from list to add new data
                myItemsList.clear();
                // getting all children from user root
                for(DataSnapshot Posts : snapshot.child("Posts").child("postID").getChildren()){

                    if(Posts.hasChild("email") && Posts.hasChild("date") && Posts.hasChild("datePosted")
                            && Posts.hasChild("description") && Posts.hasChild("attendees") && Posts.hasChild("location")
                    && Posts.hasChild("time") && Posts.hasChild("title")){

                        // getting users details from the Firebase Database and store into the List 1 by 1
                        final String getEmail = Posts.child("email").getValue(String.class);
                        final String getDate = Posts.child("date").getValue(String.class);
                        final String getDateP = Posts.child("datePosted").getValue(String.class);
                        final String getDes = Posts.child("description").getValue(String.class);
                        final String getAttendees = Posts.child("attendees").getValue(String.class);
                        final String getLocation = Posts.child("location").getValue(String.class);
                        final String getTime = Posts.child("time").getValue(String.class);
                        final String getTitle = Posts.child("title").getValue(String.class);

                        // creating user item with user details
                        MyItems myItems = new MyItems(getEmail, getDate, getDateP,
                                getDes, getAttendees, getLocation, getTime, getTitle);

                        // adding this user item to List
                        myItemsList.add(myItems);
                    }


                }

                // after all the post has added to list
                // now set adapter to recycleView
                recyclerView.setAdapter(new MyAdapter(myItemsList, MainActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext()
                                ,MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.friend:
                        startActivity(new Intent(getApplicationContext()
                                ,friends.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.chat:
                        startActivity(new Intent(getApplicationContext()
                                ,chat.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext()
                                ,profile.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.post:
                        startActivity(new Intent(getApplicationContext()
                                , addpost.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
}