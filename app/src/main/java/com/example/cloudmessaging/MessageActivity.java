package com.example.cloudmessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cloudmessaging.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

    ImageView imageProfile;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference myRef;
    Intent intent;
    String userID;

    EditText messageText;
    ImageButton sendMessageBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        imageProfile = findViewById(R.id.imageview_profile);
        username = findViewById(R.id.username);
        messageText = findViewById(R.id.text_send);
        sendMessageBtn = findViewById(R.id.btn_send);

        intent = getIntent();
        userID = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(userID);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                username.setText(users.getUsername());
                if(users.getImageURL().equals("default")){
                    imageProfile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(MessageActivity.this)
                                    .load(users.getImageURL())
                            .into(imageProfile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageText.getText().toString().trim();
                if(!msg.isEmpty()){
                    sendMessage(firebaseUser.getUid(), userID, msg);
                } else {
                    Toast.makeText(MessageActivity.this,"cant send",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage(String sender, String receiver, String msg) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> map = new HashMap<>();
        map.put("sender",sender);
        map.put("receiver",receiver);
        map.put("message",msg);

        databaseReference.child("Chats").push().setValue(map);
    }
}