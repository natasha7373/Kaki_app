package com.example.kakihomeui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.widget.Toast;
import android.net.Uri;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.text.TextUtils;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;

import com.canhub.cropper.CropImageActivity;
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
import com.canhub.cropper.CropImageActivity;
import com.canhub.cropper.CropImageView.CropResult;
import com.canhub.cropper.CropImageView.OnCropImageCompleteListener;
import com.canhub.cropper.CropImageView.OnSetImageUriCompleteListener;
//import com.canhub.cropper.sample.options_dialog.SampleOptionsBottomSheet;
//import com.canhub.cropper.sample.options_dialog.SampleOptionsEntity;
//import com.example.croppersample.R;
//import com.example.croppersample.databinding.FragmentCropImageViewBinding;

import java.util.HashMap;


public class profile extends AppCompatActivity implements dialog.dialogListener {

    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";

    // key for storing email.
    public static final String EMAIL_KEY = "email_key";

    // key for storing password.
    //public static final String PASSWORD_KEY = "password_key";

    SharedPreferences sharedpreferences;

    private FirebaseAuth mAuth;
    String currentUserID;
    private TextView email, regisdate, username;
    DatabaseReference UsersReference;
    DatabaseReference UsersRef;
    DatabaseReference SettingUserRef;
    private ProgressDialog loadingBar;
    private StorageReference ProfileImgRef;
    private ImageFilterView ProfileImg;
    final static int Gallery_Pick=1;
    private static final int RESULT_CODE = 100;
    FirebaseUser firebaseUser;
    String currentUsername;
    String currentPassword;
    EditText enteredPassword,newPassword,confirmPassword;
    Button save;
    Button deleteAccount;

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
        firebaseUser = mAuth.getCurrentUser();
        enteredPassword=findViewById(R.id.old_pw_banner);
        newPassword=findViewById(R.id.new_pw_banner);
        confirmPassword=findViewById(R.id.cfm_pw_banner);
        save=findViewById(R.id.confirm_button);
        SettingUserRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        getSupportActionBar().setTitle("Profile");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        username = findViewById(R.id.username);
        regisdate = findViewById(R.id.regis_date);
        email = findViewById(R.id.email);


        ProfileImg=(ImageFilterView)findViewById(R.id.ivProfile);
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

        ImageButton changename = findViewById(R.id.change_name);
        changename.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog Dialog = new dialog();
                Dialog.show(getSupportFragmentManager(), "dialog");
            }
        });

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("phoneNumber")) {
                        currentUsername = dataSnapshot.child("phoneNumber").getValue().toString();
                        username.setText(currentUsername);
                    }
                    if (dataSnapshot.hasChild("registeredDate")) {
                        String date = dataSnapshot.child("registeredDate").getValue().toString();
                        regisdate.setText("Registered on: "+date);
                    }
                    if (dataSnapshot.hasChild("email")) {
                        String mail = dataSnapshot.child("email").getValue().toString();
                        email.setText("Email: "+mail);
                    }
                    if (dataSnapshot.hasChild("password")) {
                        currentPassword = dataSnapshot.child("password").getValue().toString();
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails();
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
                        .getIntent(this);

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
        deleteAccount=findViewById(R.id.idBtnDelete);

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(profile.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Deleting this account will result in completely removing your " +
                        "account from the app and you won't able to access the app!");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUserData(currentUserID);
                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(profile.this, "Account Deleted", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(profile.this,sign_in.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    profile.this.finish();

                                    FirebaseAuth.getInstance().signOut();

                                    SharedPreferences.Editor editor = sharedpreferences.edit();

                                    // below line will clear
                                    // the data in shared prefs.
                                    editor.clear();

                                    // below line will apply empty
                                    // data to shared prefs.
                                    editor.apply();

                                }
                            }
                        });
                    }
                });
                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();;
            }
        });

        Button logoutBtn = findViewById(R.id.idBtnLogout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

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

    private void deleteUserData(String currentUserID) {
        DatabaseReference deUsers = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        deUsers.removeValue();
    }

    private void saveDetails() {
        loadingBar.setMessage("Updating.");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);

        String tempPassword=enteredPassword.getText().toString();
        String tempNewPassword=newPassword.getText().toString();
        String tempConfirmPassword=confirmPassword.getText().toString();

            if(tempPassword.equals(currentPassword)) {

                if (!TextUtils.isEmpty(tempNewPassword) && tempNewPassword.equals(tempConfirmPassword)) {
                    loadingBar.dismiss();
                    HashMap UserMap=new HashMap();
                    final Drawable updateIcon=getResources().getDrawable(R.drawable.checked_icon);
                    updateIcon.setBounds(0,0,updateIcon.getIntrinsicWidth(),updateIcon.getIntrinsicHeight());
                    UserMap.put("password",tempNewPassword);
                    firebaseUser.updatePassword(tempNewPassword);
                    SettingUserRef.updateChildren(UserMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                newPassword.setError("Updated",updateIcon);
                                Toast.makeText(profile.this, "Password Updated.", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());



                            }
                            else {
                                newPassword.setError("Update Failed");
                                Toast.makeText(profile.this, "Password Update Failed.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                if(!(!TextUtils.isEmpty(tempNewPassword) && tempNewPassword.equals(tempConfirmPassword)))
                {
                    loadingBar.dismiss();
                    Toast.makeText(profile.this, "Nothing was Updated.", Toast.LENGTH_SHORT).show();
                }

                loadingBar.dismiss();

            }
            else {
                loadingBar.dismiss();
                enteredPassword.setError("Please Enter Correct Password");
                enteredPassword.requestFocus();
            }
    }

    @Override
    public void applyText(String username2) {

        loadingBar.setMessage("Updating.");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);

        if (!TextUtils.isEmpty(username2)) {
            loadingBar.dismiss();
            HashMap UserMap = new HashMap();
            UserMap.put("phoneNumber", username2);
            SettingUserRef.updateChildren(UserMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(profile.this, "Username updated.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(profile.this, "Username update failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(profile.this, "Please enter username.", Toast.LENGTH_SHORT).show();
        }
        loadingBar.dismiss();
        loadingBar.dismiss();
    }

}