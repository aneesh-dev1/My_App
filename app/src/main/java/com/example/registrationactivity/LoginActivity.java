package com.example.registrationactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText name,password;
    private Button login;
    private TextView sUp;
    private int counter=5;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        name=(EditText)findViewById(R.id.loginname);
        password=(EditText)findViewById(R.id.loginpassword);
        login=(Button)findViewById(R.id.loginbtn);
        sUp=(TextView)findViewById(R.id.loginsignup);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user=firebaseAuth.getCurrentUser();

        if(user !=null){
            finish();
            startActivity(new Intent(LoginActivity.this,signin.class));
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(name.getText().toString(),password.getText().toString());
            }
        });
        sUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        });

    }
    private void validate(String username,String password){

      progressDialog.setMessage("Loading");
      progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    checkEmailVerification();
                }
                else {
                    progressDialog.dismiss();
                   Toast.makeText(LoginActivity.this,"Login Failed", Toast.LENGTH_SHORT).show();
                   counter--;
                   if(counter==0){
                       login.setEnabled(false);
                   }

                }

            }
        });

    }
    private void checkEmailVerification(){
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        boolean email_flag=firebaseUser.isEmailVerified();

        if(email_flag){
            finish();
            startActivity(new Intent(LoginActivity.this,signin.class));
        }
        else{
            Toast.makeText(this,"Verify Your Email",Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }


    }
}