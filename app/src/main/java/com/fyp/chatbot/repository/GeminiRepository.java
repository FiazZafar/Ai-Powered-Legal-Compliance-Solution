package com.fyp.chatbot.repository;

import static com.fyp.chatbot.ChatBot.API_KEY;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fyp.chatbot.apimodels.Content;
import com.fyp.chatbot.apimodels.GeminiResponse;
import com.fyp.chatbot.apimodels.Part;
import com.fyp.chatbot.apimodels.RequestBodyGemini;
import com.fyp.chatbot.helpers.RetrofitClient;
import com.fyp.chatbot.interfaces.GeminiApi;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class GeminiRepository {
    private final GeminiApi geminiApi;

    public GeminiRepository() {
        geminiApi = new RetrofitClient("https://generativelanguage.googleapis.com/")
                .getRetrofit().create(GeminiApi.class);
    }

    public LiveData<String> generateAnalysis(String prompt) {
        Log.d("MvvM", "setClause: Sending to Ai");
        MutableLiveData<String> resultLiveData = new MutableLiveData<>();

        RequestBodyGemini requestBodyGemini = new RequestBodyGemini(
                Collections.singletonList(new Content(Collections.singletonList(new Part(prompt))))
        );

        geminiApi.generateContent(API_KEY, requestBodyGemini)
                .enqueue(new Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeminiResponse> call,
                                           @NonNull Response<GeminiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String result = response.body().getCandidates().get(0)
                                        .getContent().getParts().get(0).getText();
                                Log.d("MvvM", "setClause:Ai result is " + result);
                                resultLiveData.postValue(result);
                            } catch (Exception e) {
                                resultLiveData.postValue("Parsing error");
                            }
                        } else {
                            resultLiveData.postValue("Response error");
                        }
                    }

                    @Override
                    public void onFailure(Call<GeminiResponse> call, Throwable t) {
                        resultLiveData.postValue("Network error: " + t.getMessage());
                    }
                });

        return resultLiveData;
    }
}

