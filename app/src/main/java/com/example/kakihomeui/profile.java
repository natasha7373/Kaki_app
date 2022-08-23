package com.example.kakihomeui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.widget.Toast;
import android.net.Uri;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.canhub.cropper.CropImageView.CropResult;
import com.canhub.cropper.CropImageView.OnCropImageCompleteListener;
import com.canhub.cropper.CropImageView.OnSetImageUriCompleteListener;
//import com.canhub.cropper.sample.options_dialog.SampleOptionsBottomSheet;
//import com.canhub.cropper.sample.options_dialog.SampleOptionsEntity;
//import com.example.croppersample.R;
//import com.example.croppersample.databinding.FragmentCropImageViewBinding;



public class profile extends AppCompatActivity {

    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";

    // key for storing email.
    public static final String EMAIL_KEY = "email_key";

    // key for storing password.
    public static final String PASSWORD_KEY = "password_key";

    SharedPreferences sharedpreferences;

    private FirebaseAuth mAuth;
    String currentUserID;
    private TextView email;
    DatabaseReference UsersReference;
    DatabaseReference UsersRef;
    private ProgressDialog loadingBar;
    private StorageReference ProfileImgRef;
    private ImageView ProfileImg;
    final static int Gallery_Pick=1;
    private static final int RESULT_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProfileImgRef= FirebaseStorage.getInstance().getReference().child("Profile Images");

        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        mAuth= FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        UsersReference=database.getReference().child("Users").child(currentUserID);
        UsersRef=database.getReference().child("Users");
        loadingBar=new ProgressDialog(this);

        getSupportActionBar().setTitle("Profile");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        email = findViewById(R.id.name);

        ProfileImg=(ImageView)findViewById(R.id.profile_pic);
        ProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Pick);
            }
        });

        UsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("profileImage")) {
                        String image = dataSnapshot.child("profileImage").getValue().toString();
                        loadingBar.dismiss();
                        Picasso.get().load(image).placeholder(R.drawable.default_profile_pic).into(ProfileImg);
                    }
                    else
                    {
                        loadingBar.dismiss();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("userName")) {
                        String pusername = dataSnapshot.child("userName").getValue().toString();
                        email.setText(pusername);
                    }
                    if (dataSnapshot.hasChild("registeredDate")) {
                        String date = dataSnapshot.child("registeredDate").getValue().toString();
                        //date.setText(date);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button logoutBtn = findViewById(R.id.idBtnLogout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // calling method to edit values in shared prefs.
                SharedPreferences.Editor editor = sharedpreferences.edit();

                // below line will clear
                // the data in shared prefs.
                editor.clear();

                // below line will apply empty
                // data to shared prefs.
                editor.apply();

                Intent i = new Intent(profile.this, sign_in.class);
                startActivity(i);
                finish();
            }
        });
        /*
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
                Uri ImageUri = data.getData();
                CropImage.activity(ImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            }

           if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {
                    loadingBar.setMessage("Please wait, while we update your profile picture.");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(false);
                    Uri resultUri = result.getUri();

                    final StorageReference filePath = ProfileImgRef.child(currentUserID + ".jpg");
                    filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    UsersReference.child("profileImage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent intent = new Intent(profile.this, profile.class);
                                                        startActivity(intent);
                                                        finish();

                                                    } else {
                                                        Toast.makeText(profile.this, "Error occured..." + task.getException(), Toast.LENGTH_LONG).show();

                                                    }

                                                }
                                            });
                                }
                            });
                        }
                    });
                }
           }
        }
        */
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