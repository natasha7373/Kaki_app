package com.example.kakihomeui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class sign_up extends AppCompatActivity {
    EditText email,phone_number,password,password_conf;

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

        email=findViewById(R.id.email);
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
                name=email.getText().toString().trim();
                pass=password.getText().toString().trim();
                cpass=password_conf.getText().toString().trim();
                phone=phone_number.getText().toString().trim();
                registerNewUser(name,pass,cpass,phone);

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

    public void registerNewUser(String name, String pass, String cpass, String phone)
    {
        if(TextUtils.isEmpty(name)){
            email.setError("Please enter full name.");
            email.requestFocus();
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
                            String name,pass,phone,date;
                            name=email.getText().toString().trim();
                            pass=password.getText().toString().trim();
                            phone=phone_number.getText().toString().trim();
                            date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                            storeUserOnFireBase(name,phone,pass,currentUserID,date);

                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(),"Account created",Toast.LENGTH_LONG).show();
                            Intent i=new Intent(sign_up.this,sign_in.class);
                            startActivity(i);
                            finish();


                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(),task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

}