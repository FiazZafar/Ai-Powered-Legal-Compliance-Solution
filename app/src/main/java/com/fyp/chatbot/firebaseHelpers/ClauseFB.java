package com.fyp.chatbot.firebaseHelpers;


import androidx.annotation.NonNull;

import com.fyp.chatbot.interfaces.ClauseInterface;
import com.fyp.chatbot.interfaces.FirebaseCallback;
import com.fyp.chatbot.models.ClauseModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClauseFB implements ClauseInterface {
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Smart_Goval");
    @Override
    public void saveClause(String userId, ClauseModel clauseModel, FirebaseCallback<Boolean> onSave) {
        myRef.child("Clause Node").child(userId).push().setValue(clauseModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                onSave.onComplete(true);
            }else {
                onSave.onComplete(false);
            }
        });
    }

    @Override
    public void fetchClause(String userId, FirebaseCallback<List<ClauseModel>> onClauseList) {
        List<ClauseModel> myList = new ArrayList<>();
        myRef.child("Clause Node").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        ClauseModel clauseModel = snapshot1.getValue(ClauseModel.class);
                        if (clauseModel != null){
                            myList.add(clauseModel);
                        }
                    }
                    onClauseList.onComplete(myList);
                }else {
                    onClauseList.onComplete(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onClauseList.onComplete(new ArrayList<>());
            }
        });
    }

    @Override
    public void deleteClause(String userId, String clauseId, FirebaseCallback<Boolean> onDeleteClause) {
        myRef.child(userId).child("Clause Node").removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                onDeleteClause.onComplete(true);
            }else {
                onDeleteClause.onComplete(false);
            }
        });
    }

    @Override
    public void deleteAll(String userId, FirebaseCallback<Boolean> onDeleteAll) {
        myRef.child(userId).child("Clause Node").removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                onDeleteAll.onComplete(true);
            }else {
                onDeleteAll.onComplete(false);
            }
        });
    }
}
