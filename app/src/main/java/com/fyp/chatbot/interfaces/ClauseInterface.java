package com.fyp.chatbot.interfaces;

import com.fyp.chatbot.models.ClauseModel;

import java.util.List;

public interface ClauseInterface {
    void saveClause(String userId,ClauseModel clauseModel,FirebaseCallback<Boolean> onSave);
    void fetchClause(String userId, FirebaseCallback<List<ClauseModel>> onClauseList);
    void deleteClause(String userId , String clauseId , FirebaseCallback<Boolean> oDeleteClause);
    void deleteAll(String userId,FirebaseCallback<Boolean> onDeleteAll);
}
