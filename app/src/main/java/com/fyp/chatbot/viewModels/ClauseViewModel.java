package com.fyp.chatbot.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.chatbot.firebaseHelpers.ClauseFB;
import com.fyp.chatbot.interfaces.ClauseInterface;
import com.fyp.chatbot.models.ClauseModel;
import com.fyp.chatbot.repository.GeminiRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClauseViewModel extends ViewModel {
    GeminiRepo geminiRepo = new GeminiRepo();
    ClauseInterface clauseInterface = new ClauseFB();
    MutableLiveData<List<ClauseModel>> clauseList = new MutableLiveData<>();
    MutableLiveData<String> generateClause = new MutableLiveData<>();

    public MutableLiveData<String> getGenerateClause() {
        return generateClause;
    }

    public void setClause(String clauseType , Map<String,String> inputValues){

        Log.d("MvvM", "setClause: Setup of clause started");
        String prompt = buildClausePrompt(clauseType,inputValues);
        if (!prompt.isEmpty()){
            geminiRepo.generateAnalysis(prompt, onResult -> generateClause.postValue(onResult));
        }

    }
    private String buildClausePrompt(String clauseType, Map<String,String> inputs) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a legal clause generator. ");
        prompt.append("Generate a single, concise clause of 2–4 sentences for the type: ").append(clauseType)
                .append(". ");
        prompt.append("Avoid explanations or extra context. Just the clause text.\\n");

        for (Map.Entry<String,String> entry: inputs.entrySet()){
            prompt.append(entry.getKey()).append(": ").append(entry.getValue());
        }
        prompt.append("Output ONLY the clause.");
        return prompt.toString();
    }

    public LiveData<Boolean> saveClause(String clauseType, String clauseTxt) {
        Log.d("ClauseMVVM", "saveClause: Started");
        MutableLiveData<Boolean> clauseStatus = new MutableLiveData<>();
        ClauseModel clauseModel = new ClauseModel(UUID.randomUUID().toString(),clauseType,clauseTxt,System.currentTimeMillis());
        clauseInterface.saveClause("",clauseModel,onSavedClause -> {
            Log.d("ClauseMVVM", "saveClause: result is " + onSavedClause);
            clauseStatus.postValue(onSavedClause);
        });
        return clauseStatus;
    }

    public MutableLiveData<List<ClauseModel>> getClauseList() {
        return clauseList;
    }

    public void setClauseList() {
        clauseInterface.fetchClause("",onClauseList -> {
            if (onClauseList != null){
                clauseList.postValue(onClauseList);
            }else {
                clauseList.postValue(new ArrayList<>());
            }
        });
    }
}
