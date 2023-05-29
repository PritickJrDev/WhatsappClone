package com.example.cloudmessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextView titleTV;
    private EditText usernameET, passwordET, emailET;
    private Button registerButton;

    FirebaseAuth firebaseAuth;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameET = findViewById(R.id.userEditText);
        passwordET = findViewById(R.id.editText2);
        emailET = findViewById(R.id.emailEditText);

        registerButton = findViewById(R.id.buttonRegister);

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(usernameET.getText().toString()) || !TextUtils.isEmpty(passwordET.getText().toString())
                || !TextUtils.isEmpty(emailET.getText().toString())){
                    registerNow(usernameET.getText().toString(), passwordET.getText().toString(), emailET.getText().toString());
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void registerNow(final String username, String password, String email) {

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String userID = firebaseUser.getUid();

                    myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(userID);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id",userID);
                    hashMap.put("username",username);
                    hashMap.put("imageURL","default");
                    hashMap.put("status","offline");

                    myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Enter password with minimum 6 character", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}