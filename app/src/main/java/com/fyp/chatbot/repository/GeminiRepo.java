package com.fyp.chatbot.repository;

import static com.fyp.chatbot.activities.ChatBot.API_KEY;

import androidx.annotation.NonNull;

import com.fyp.chatbot.apimodels.Content;
import com.fyp.chatbot.apimodels.GeminiResponse;
import com.fyp.chatbot.apimodels.Part;
import com.fyp.chatbot.apimodels.RequestBodyGemini;
import com.fyp.chatbot.helpers.RetrofitClient;
import com.fyp.chatbot.interfaces.FirebaseCallback;
import com.fyp.chatbot.interfaces.GeminiApi;

import java.io.IOException;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class GeminiRepo {
    private final GeminiApi geminiApi;

    public GeminiRepo() {

        geminiApi = new RetrofitClient("https://generativelanguage.googleapis.com/")

                .getRetrofit().create(GeminiApi.class);
    }

        public void generateAnalysis(String prompt, FirebaseCallback<String> callback) {
            RequestBodyGemini requestBodyGemini = new RequestBodyGemini(
                    Collections.singletonList(new Content(Collections.singletonList(new Part(prompt))))
            );

            geminiApi.generateContent(API_KEY, requestBodyGemini).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<GeminiResponse> call,
                                       @NonNull Response<GeminiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String result = response.body().getCandidates().get(0)
                                    .getContent().getParts().get(0).getText();
                            callback.onComplete(result);
                        } catch (Exception e) {
                            callback.onComplete("Parsing error");
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody() != null
                                    ? response.errorBody().string()
                                    : "";

                            if (response.code() == 429 || errorBody.contains("RESOURCE_EXHAUSTED")) {
                                callback.onComplete("RESOURCE_EXHAUSTED");
                            } else {
                                callback.onComplete("Response error: " + response.code());
                            }

                        } catch (IOException e) {
                            callback.onComplete("Error reading errorBody");
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeminiResponse> call, Throwable t) {
                    callback.onComplete("Network error: " + t.getMessage());
                }
            });
        }
    }


