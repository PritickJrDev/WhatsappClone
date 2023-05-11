package com.example.cloudmessaging.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmessaging.R;
import com.example.cloudmessaging.adapters.UserAdapter;
import com.example.cloudmessaging.model.ChatList;
import com.example.cloudmessaging.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private UserAdapter userAdapter;
    private List<Users> listOfUsers;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    private List<ChatList> listOfChatUsers;

    RecyclerView recyclerView;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        listOfChatUsers = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listOfChatUsers.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    listOfChatUsers.add(chatList);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void chatList() {
        // Getting all recent chats;
        listOfUsers = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("MyUsers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listOfUsers.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    for(ChatList chatList : listOfChatUsers){
                        if(users.getId().equals(chatList.getId())){
                            listOfUsers.add(users);
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(),listOfUsers,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}