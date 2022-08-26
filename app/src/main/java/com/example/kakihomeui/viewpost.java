package com.example.kakihomeui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class viewpost extends AppCompatActivity {

    FirebaseAuth mAuth;
    String currentUserID;
    DatabaseReference UsersReference;
    DatabaseReference UsersRef;
    FirebaseDatabase mDatabase;
    FirebaseUser user;
    Button addCom;
    EditText typeCom;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<Comment> listComment;
    static String COMMENT_KEY = "Comments";

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
        setContentView(R.layout.activity_viewpost);


        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        RvComment = findViewById(R.id.rv_comment);

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


        addCom = findViewById(R.id.addComment);
        typeCom = findViewById(R.id.editText);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance("https://kaki-real-default-rtdb.asia-southeast1.firebasedatabase.app");

        // button listener
        addCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCom.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = mDatabase.getReference("Comment").child("commentID").push();
                String text = typeCom.getText().toString();
                String email = firebaseUser.getEmail();
                Comment comment = new Comment(text, email);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showMessage("comment added");
                        typeCom.setText("");
                        addCom.setVisibility(View.VISIBLE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("fail to add comment" + e.getMessage());
                    }
                });
            }
        });

        iniRvComment();
    }

    private void iniRvComment(){

        RvComment.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = mDatabase.getReference("Comment").child("commentID");
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap:snapshot.getChildren()) {
                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment);

                }

                commentAdapter = new CommentAdapter(getApplicationContext(), listComment);
                RvComment.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}