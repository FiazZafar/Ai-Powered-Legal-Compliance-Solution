package com.fyp.chatbot.viewModels;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.chatbot.apimodels.GeminiResponse;
import com.fyp.chatbot.repository.GeminiRepository;

import java.util.List;
import java.util.Map;

public class GenerateClauseMVVM extends ViewModel {
    GeminiRepository geminiRepository = new GeminiRepository();


    public LiveData<String> setClause(String clauseType , Map<String,String> inputValues){

        Log.d("MvvM", "setClause: Setup of clause started");
        String prompt = buildClausePrompt(clauseType,inputValues);
        if (!prompt.isEmpty()){
            return geminiRepository.generateAnalysis(prompt);
        }
        return null;
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

}
