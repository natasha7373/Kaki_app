package com.example.kakihomeui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class sign_in extends AppCompatActivity {

    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";

    // key for storing email.
    public static final String EMAIL_KEY = "email_key";

    // key for storing password.
    public static final String PASSWORD_KEY = "password_key";

    // variable for shared preferences.
    SharedPreferences sharedpreferences;
    String semail,spassword;

    EditText email;
    EditText password;
    CheckBox remember;
    TextView login, forgetPass,signUp, googleSignInButton;
    private ProgressDialog loadingBar;

    FirebaseAuth mAuth;
    FirebaseUser user;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG = "sign_in";
    private DatabaseReference UsersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signUp = findViewById(R.id.signup);
        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(sign_in.this, sign_up.class);
                startActivity(i);
                finish();
            }
        });
        mAuth= FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        login = findViewById(R.id.log_in);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        // getting the data which is stored in shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        // in shared prefs inside het string method
        // we are passing key value as EMAIL_KEY and
        // default value is
        // set to null if not present.
        semail = sharedpreferences.getString(EMAIL_KEY, null);
        spassword = sharedpreferences.getString(PASSWORD_KEY, null);

        forgetPass = findViewById(R.id.forget_pw);
        loadingBar=new ProgressDialog(this);
        googleSignInButton=findViewById(R.id.google);
        remember = findViewById(R.id.checkbox_meat);
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");
        if(checkbox.equals("true")){
            Intent intent = new Intent(sign_in.this,MainActivity.class);
            startActivity(intent);
        }else if(checkbox.equals("false")){
            Toast.makeText(sign_in.this, "Please sign in", Toast.LENGTH_SHORT).show();
        }

        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
                    {
                        Toast.makeText(sign_in.this, "Connection to Google Sign is failed...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        forgetPass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String Fusnm;
                Fusnm=email.getText().toString();
                sendForgotPassLink(Fusnm);

            }
        });
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String Lusnm,Lpass;
                Lusnm=email.getText().toString().trim();
                Lpass=password.getText().toString().trim();
                Login(Lusnm,Lpass);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(EMAIL_KEY, email.getText().toString());
                editor.putString(PASSWORD_KEY, password.getText().toString());
                editor.apply();
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
                }else if (!compoundButton.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(sign_in.this, "UnChecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void signIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            loadingBar.setMessage("Please wait, while we load your application.");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else
            {
                Toast.makeText(this, "Can't get Authentication result.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "signInWithCredential:success");
                            Intent intent = new Intent(sign_in.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            loadingBar.dismiss();
                        }
                        else
                        {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String message = task.getException().toString();
                            Intent intent = new Intent(sign_in.this, sign_in.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(sign_in.this, "Not Authenticated : " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    public void Login(String Lusnm, final String Lpass)
    {
        loadingBar.setMessage("Please wait, while we load your application.");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);
        if(TextUtils.isEmpty(Lusnm))
        {
            email.setError("Please enter Email.");
            email.requestFocus();
            loadingBar.dismiss();
            return;
        }
        else if(TextUtils.isEmpty(Lpass)){
            password.setError("Please enter Password.");
            password.requestFocus();
            loadingBar.dismiss();
            return;
        }
        mAuth.signInWithEmailAndPassword(Lusnm,Lpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    CheckUserExists();
                    loadingBar.dismiss();

                    Intent intent = new Intent(sign_in.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    //Toast.makeText(getApplicationContext(), "Please check email input is correct" + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Please check email input is correct.", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    return;
                }
            }
        });
    }
    public void sendForgotPassLink(String emailId){
        if(!TextUtils.isEmpty(emailId)){
            mAuth.sendPasswordResetEmail(emailId).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(),"Reset link sent to"+email.getText().toString(),Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            email.setError("Please enter Email.");
            email.requestFocus();
        }
    }
    private void CheckUserExists() {

        final String user_id = mAuth.getCurrentUser().getUid();
        UsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id)){

                    Intent MainIntent = new Intent(sign_in.this,MainActivity.class);
                    MainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(MainIntent);
                }else
                {

                Toast.makeText(sign_in.this,"You need to setup your Account.. ",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (semail != null && spassword != null) {
            Intent i = new Intent(sign_in.this, MainActivity.class);
            startActivity(i);
        }


    }

}
