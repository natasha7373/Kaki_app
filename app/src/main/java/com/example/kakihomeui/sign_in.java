package com.example.kakihomeui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class sign_in extends AppCompatActivity {

    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";

    // key for storing email.
    public static final String EMAIL_KEY = "email_key";

    // key for storing password.
    public static final String PASSWORD_KEY = "password_key";

    SharedPreferences sharedpreferences;
    String semail, spassword;

    EditText email, password;
    CheckBox remember;
    TextView login, forgetPass;
    TextView googleSignInButton, signUp;
    private ProgressDialog loadingBar;

    private static final int RC_SIGN_IN = 100;

    private FirebaseAuth mAuth;
    FirebaseUser user;
    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG = "GOOGLE_SIGN_IN_TAG";
    private DatabaseReference UsersReference;

    private GoogleSignInOptions gso;
    GoogleSignInClient gsc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signUp = findViewById(R.id.signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(sign_in.this, sign_up.class);
                startActivity(i);
                finish();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        login = findViewById(R.id.log_in);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        semail = sharedpreferences.getString(EMAIL_KEY, null);
        spassword = sharedpreferences.getString(PASSWORD_KEY, null);

        forgetPass = findViewById(R.id.forget_pw);
        loadingBar = new ProgressDialog(this);
        googleSignInButton = findViewById(R.id.google);
        remember = findViewById(R.id.checkbox_meat);
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("remember", "false");
        editor.apply();

        if (checkbox.equals("true") && semail != null && spassword != null) {
            Intent i = new Intent(sign_in.this, MainActivity.class);
            startActivity(i);

        } else if (checkbox.equals("false")) {
            Toast.makeText(sign_in.this, "Please sign in", Toast.LENGTH_SHORT).show();
        }

        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
        if (acc != null) {
            Intent intent = new Intent(sign_in.this, MainActivity.class);
            startActivity(intent);
        }

        googleSignInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: begin Google signIn");
                Intent intent = gsc.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);

            }
        });
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Fusnm;
                Fusnm = email.getText().toString();
                sendForgotPassLink(Fusnm);

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Lusnm, Lpass;
                Lusnm = email.getText().toString().trim();
                Lpass = password.getText().toString().trim();
                Login(Lusnm, Lpass);

            }
        });

        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                    Toast.makeText(sign_in.this, "Checked", Toast.LENGTH_SHORT).show();
                }else if(!compoundButton.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(sign_in.this, "UnChecked", Toast.LENGTH_SHORT).show();
                }
            }


        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: Google signin intent result");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                GoogleSignInAccount acc = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(acc);
            } catch (ApiException e) {
                Log.d(TAG, "onActivityResult: " +e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acc){
        Log.d(TAG, "firebaseAuthWithGoogle: begin firebase auth");
        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "onSuccess: Logged In");

                        //get logged in user
                        FirebaseUser user = mAuth.getCurrentUser();
                        //get user info
                        String uid = user.getUid();
                        String email = user.getEmail();
                        String phone = user.getPhoneNumber();
                        String pass = "Google user";

                        //check if user is new or existing
                        if(authResult.getAdditionalUserInfo().isNewUser()){
                            registerNewUser(email,phone, pass);
                            Toast.makeText(sign_in.this, "Account created"+ email, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent(sign_in.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Login Failed" + e.getMessage());
                    }
                });
    }

    public void Login(String Lusnm, final String Lpass) {
        loadingBar.setMessage("Please wait, while we load your application.");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);
        if (TextUtils.isEmpty(Lusnm)) {
            email.setError("Please enter Email.");
            email.requestFocus();
            loadingBar.dismiss();
            return;
        } else if (TextUtils.isEmpty(Lpass)) {
            password.setError("Please enter Password.");
            password.requestFocus();
            loadingBar.dismiss();
            return;
        }
        mAuth.signInWithEmailAndPassword(Lusnm, Lpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    CheckUserExists();
                    loadingBar.dismiss();
                    Intent intent = new Intent(sign_in.this, MainActivity.class);
                    startActivity(intent);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(EMAIL_KEY, email.getText().toString());
                    editor.putString(PASSWORD_KEY, password.getText().toString());
                    editor.apply();
                    finish();
                } else {
                    //Toast.makeText(getApplicationContext(), "Please check email input is correct" + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Please check email input is correct" , Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    return;
                }
            }
        });
    }

    public void sendForgotPassLink(String emailId) {
        if (!TextUtils.isEmpty(emailId)) {
            mAuth.sendPasswordResetEmail(emailId).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Reset link sent to" + email.getText().toString(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            email.setError("Please enter Email.");
            email.requestFocus();
        }
    }

    private void CheckUserExists() {

        final String user_id = mAuth.getCurrentUser().getUid();
        UsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)) {

                    Intent MainIntent = new Intent(sign_in.this, MainActivity.class);
                    MainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(MainIntent);
                } else {

                    Toast.makeText(sign_in.this, "You need to setup your Account.. ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void registerNewUser(String email, String pass, String phone)
    {
        loadingBar.setTitle("Create New Account");
        loadingBar.setMessage("Please wait, while we create your new account.");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);

        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String currentUserID=mAuth.getCurrentUser().getUid();
                            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                            storeUserOnFireBase(email,phone,pass,currentUserID,date);

                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(),"Account created",Toast.LENGTH_LONG).show();
                            Intent i=new Intent(sign_in.this,sign_in.class);
                            startActivity(i);
                            finish();


                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(),task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void storeUserOnFireBase(String email, String phoneNumber, String password, String userID, String date){
        final FirebaseDatabase database=FirebaseDatabase.getInstance("https://kaki-real-default-rtdb.asia-southeast1.firebasedatabase.app");
        UsersReference=database.getReference().child("Users").child(userID);
        HashMap userMap=new HashMap();
        userMap.put("email",email);
        userMap.put("phoneNumber",phoneNumber);
        userMap.put("password",password);
        userMap.put("registeredDate",date);

        UsersReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {

                }
                else {
                    Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }

}


