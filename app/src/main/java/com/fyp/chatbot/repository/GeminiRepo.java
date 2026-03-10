package com.fyp.chatbot.repository;

import static com.fyp.chatbot.activities.ChatBot.API_KEY;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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
    private static final int MAX_RETRIES = 3;

    public GeminiRepo() {
        geminiApi = new RetrofitClient("https://generativelanguage.googleapis.com/")
                .getRetrofit().create(GeminiApi.class);
    }

    public void generateAnalysis(String prompt, FirebaseCallback<String> callback) {
        generateWithRetry(prompt, callback, 0);
    }

    private void generateWithRetry(String prompt, FirebaseCallback<String> callback, int retryCount) {
        RequestBodyGemini requestBodyGemini = new RequestBodyGemini(
                Collections.singletonList(new Content(Collections.singletonList(new Part(prompt))))
        );
        Log.d("response", "generateWithRetry: " + API_KEY);
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
                                ? response.errorBody().string() : "";

                        if (response.code() == 429 || errorBody.contains("RESOURCE_EXHAUSTED")) {
                            if (retryCount < MAX_RETRIES) {
                                long delayMs = (long) Math.pow(2, retryCount + 1) * 1000;
                                Log.d("GeminiRepo", "429 hit, retrying in " + delayMs + "ms (attempt " + (retryCount + 1) + ")");

                                new Handler(Looper.getMainLooper()).postDelayed(() ->
                                        generateWithRetry(prompt, callback, retryCount + 1), delayMs);
                            } else {
                                Log.d("GeminiRepo", "All retries exhausted");
                                callback.onComplete("RESOURCE_EXHAUSTED");
                            }
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