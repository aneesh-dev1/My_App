package com.example.registrationactivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText userName,userPassword,userEmail;
    private Button regButton;
    private TextView sign;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fstore;
    private String userId;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUIViews();

        firebaseAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    //Upload to database
                    String user_email =userEmail.getText().toString().trim();
                    String user_password =userPassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                          if(task.isSuccessful()) {
                                  String name=userName.getText().toString();
                                  String email=userEmail.getText().toString();
                                  userId=firebaseAuth.getUid();
                                  DocumentReference documentReference=fstore.collection("Users").document("Names");
                                  Map<String,Object> user=new HashMap<>();
                                  user.put("Name",name);
                                  user.put("Email",email);
                                  documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {
                                          Toast.makeText(MainActivity.this,"Registered",Toast.LENGTH_LONG).show();
                                      }
                                  });
                              Toast.makeText(MainActivity.this,"Successfully Registered,Verification mail is sent",Toast.LENGTH_LONG).show();
                              sendEmailVerification();
                          }
                          else{
                              Toast.makeText(MainActivity.this, "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                          }
                        }
                    });
                }

            }

        });
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });

    }
    private void setupUIViews(){
        userName =(EditText)findViewById(R.id.username);
        userEmail=(EditText)findViewById(R.id.email);
        userPassword=(EditText)findViewById(R.id.password);
        regButton=(Button)findViewById(R.id.register);
        sign=(TextView)findViewById(R.id.alsign);
    }

    public Boolean validate(){
        Boolean result=false;
        String name=userName.getText().toString();
        String password=userPassword.getText().toString();
        String email=userEmail.getText().toString();

        if(name.isEmpty() || password.isEmpty() || email.isEmpty()){
            Toast.makeText(this,"Please enter all the details",Toast.LENGTH_SHORT).show();
        }
        else{
            result=true;
        }
        return  result;

    }
    private void sendEmailVerification(){

        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Verification Mail was unable to send!!",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }
}
