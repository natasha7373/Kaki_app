package com.example.kakihomeui;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;

public class sign_up extends AppCompatActivity {
    EditText full_name,phone_number,password,password_conf;

    private ProgressDialog loadingBar;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference UsersReference;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth= FirebaseAuth.getInstance();

        user=mAuth.getCurrentUser();

        full_name=findViewById(R.id.full_name);
        phone_number=findViewById(R.id.phone_number);
        password=findViewById(R.id.password);
        password_conf=findViewById(R.id.password_conf);

        loadingBar=new ProgressDialog(this);

        TextView goLogin;
        goLogin=findViewById(R.id.sign_in);
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(sign_up.this,sign_in.class);
                startActivity(i);
                finish();
            }
        });

        TextView submit;
        submit=findViewById(R.id.sign_up);
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String name,pass,cpass,phone;
                name=full_name.getText().toString();
                pass=password.getText().toString();
                cpass=password_conf.getText().toString();
                phone=phone_number.getText().toString();
                registerNewUser(name,pass,cpass,phone);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void storeUserOnFireBase(String userName, String phoneNumber, String password, String userID){
        final FirebaseDatabase database=FirebaseDatabase.getInstance("https://kaki-real-default-rtdb.asia-southeast1.firebasedatabase.app");
        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        //LocalDateTime now = LocalDateTime.now();
        UsersReference=database.getReference().child("Users").child(userID);
        HashMap userMap=new HashMap();
        userMap.put("userName",userName);
        userMap.put("phoneNumber",phoneNumber);
        userMap.put("password",password);
        //userMap.put("registeredDate",dtf.format(now));

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

    public void registerNewUser(String name, String pass, String cpass, String phone)
    {
        if(TextUtils.isEmpty(name)){
            full_name.setError("Please enter full name.");
            full_name.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(pass)){
            password.setError("Please enter password.");
            password.requestFocus();
            return;
        }
        else if(!pass.equals(cpass)){
            password_conf.setError("Please confirm password.");
            password_conf.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(phone)){
            phone_number.setError("Please enter phone number.");
            phone_number.requestFocus();
            return;
        }

        loadingBar.setTitle("Create New Account");
        loadingBar.setMessage("Please wait, while we create your new account.");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);

        mAuth.createUserWithEmailAndPassword(name,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            currentUserID=mAuth.getCurrentUser().getUid();
                            String name,pass,phone;
                            name=full_name.getText().toString();
                            pass=password.getText().toString();
                            phone=phone_number.getText().toString();

                            storeUserOnFireBase(name,phone,pass,currentUserID);

                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(),"Okay.",Toast.LENGTH_LONG).show();

                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(),task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

}