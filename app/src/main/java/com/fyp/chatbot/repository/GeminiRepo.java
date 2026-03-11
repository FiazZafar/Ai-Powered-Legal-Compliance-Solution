package com.fyp.chatbot.repository;

import static com.fyp.chatbot.activities.ChatBot.API_KEY;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeminiRepo {

    private static final String TAG = "GeminiRepo";
    private static final int MAX_RETRIES = 3;
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/";

    private final GeminiApi geminiApi;

    public GeminiRepo() {
        geminiApi = new RetrofitClient(BASE_URL)
                .getRetrofit()
                .create(GeminiApi.class);
    }
    public void generateAnalysis(String prompt, FirebaseCallback<String> callback) {
        if (!isApiKeyValid()) {
            callback.onComplete(GeminiError.INVALID_API_KEY.message);
            return;
        }
        if (prompt == null || prompt.trim().isEmpty()) {
            callback.onComplete(GeminiError.EMPTY_PROMPT.message);
            return;
        }
        generateWithRetry(prompt, callback, 0);
    }

    public void generateChatResponse(List<Content> contents, FirebaseCallback<String> callback) {
        if (!isApiKeyValid()) {
            callback.onComplete(GeminiError.INVALID_API_KEY.message);
            return;
        }
        if (contents == null || contents.isEmpty()) {
            callback.onComplete(GeminiError.EMPTY_PROMPT.message);
            return;
        }
        RequestBodyGemini requestBody = new RequestBodyGemini(contents);
        executeCall(requestBody, callback, 0);
    }


    private void generateWithRetry(String prompt,
                                   FirebaseCallback<String> callback,
                                   int retryCount) {
        RequestBodyGemini requestBody = new RequestBodyGemini(
                Collections.singletonList(
                        new Content(Collections.singletonList(new Part(prompt)))
                )
        );
        executeCall(requestBody, callback, retryCount);
    }

    private void executeCall(RequestBodyGemini requestBody,
                             FirebaseCallback<String> callback,
                             int retryCount) {

        geminiApi.generateContent(API_KEY, requestBody).enqueue(new Callback<>() {

            @Override
            public void onResponse(@NonNull Call<GeminiResponse> call,
                                   @NonNull Response<GeminiResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    handleSuccess(response, callback);
                } else {
                    handleHttpError(response, requestBody, callback, retryCount);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeminiResponse> call,
                                  @NonNull Throwable t) {
                handleNetworkFailure(t, callback);
            }
        });
    }
    private void handleSuccess(Response<GeminiResponse> response,
                               FirebaseCallback<String> callback) {
        try {
            String result = response.body()
                    .getCandidates().get(0)
                    .getContent()
                    .getParts().get(0)
                    .getText();
            Log.d(TAG, "Success: received reply");
            callback.onComplete(result);
        } catch (Exception e) {
            Log.e(TAG, "Parsing error: " + e.getMessage());
            callback.onComplete(GeminiError.PARSING_ERROR.message);
        }
    }

    private void handleHttpError(Response<GeminiResponse> response,
                                 RequestBodyGemini requestBody,
                                 FirebaseCallback<String> callback,
                                 int retryCount) {
        String errorBody = readErrorBody(response);
        int code = response.code();
        Log.e(TAG, "HTTP " + code + " — " + errorBody);

        switch (code) {
            case 400:
                callback.onComplete(GeminiError.BAD_REQUEST.message);
                break;

            case 401:
                callback.onComplete(GeminiError.INVALID_API_KEY.message);
                break;

            case 403:
                if (errorBody.contains("RESOURCE_EXHAUSTED") ||
                        errorBody.contains("quota")) {
                    retryOrFail(requestBody, callback, retryCount,
                            GeminiError.QUOTA_EXCEEDED.message);
                } else {
                    Log.d("errorBody", "handleHttpError: " + errorBody.toString());
                    callback.onComplete(GeminiError.API_NOT_ENABLED.message);
                }
                break;

            case 429:
                retryOrFail(requestBody, callback, retryCount,
                        GeminiError.RATE_LIMITED.message);
                break;

            case 500:
            case 503:
                callback.onComplete(GeminiError.SERVER_ERROR.message);
                break;

            default:
                callback.onComplete("Something went wrong (error " + code + "). Please try again.");
                break;
        }
    }

    private void handleNetworkFailure(Throwable t, FirebaseCallback<String> callback) {
        String msg = t.getMessage() != null ? t.getMessage().toLowerCase() : "";
        Log.e(TAG, "Network failure: " + msg);

        if (msg.contains("unable to resolve host") || msg.contains("no address")) {
            callback.onComplete(GeminiError.NO_INTERNET.message);
        } else if (msg.contains("timeout") || msg.contains("timed out")) {
            callback.onComplete(GeminiError.TIMEOUT.message);
        } else {
            callback.onComplete(GeminiError.NETWORK_ERROR.message);
        }
    }
    private void retryOrFail(RequestBodyGemini requestBody,
                             FirebaseCallback<String> callback,
                             int retryCount,
                             String fallbackError) {
        if (retryCount < MAX_RETRIES) {
            long delayMs = (long) Math.pow(2, retryCount + 1) * 1000;
            Log.d(TAG, "Retrying in " + delayMs + "ms (attempt " + (retryCount + 1) + ")");
            new Handler(Looper.getMainLooper()).postDelayed(
                    () -> executeCall(requestBody, callback, retryCount + 1),
                    delayMs
            );
        } else {
            Log.d(TAG, "All retries exhausted");
            callback.onComplete(fallbackError);
        }
    }

    private String readErrorBody(Response<?> response) {
        try {
            return response.errorBody() != null ? response.errorBody().string() : "";
        } catch (IOException e) {
            return "";
        }
    }

    private boolean isApiKeyValid() {
        boolean valid = API_KEY != null && !API_KEY.trim().isEmpty();
        if (!valid) Log.e(TAG, "API key is null or empty");
        return valid;
    }

    public enum GeminiError {
        INVALID_API_KEY   ("API key is missing or invalid. Please check your configuration."),
        API_NOT_ENABLED   ("Gemini API is not enabled for this project. Please check Google Cloud Console."),
        QUOTA_EXCEEDED    ("Daily quota exceeded. Please try again tomorrow or upgrade your plan."),
        RATE_LIMITED      ("Too many requests. Please wait a moment and try again."),
        BAD_REQUEST       ("Your request could not be processed. Please rephrase and try again."),
        SERVER_ERROR      ("Gemini service is temporarily unavailable. Please try again later."),
        NO_INTERNET       ("No internet connection. Please check your network."),
        TIMEOUT           ("Request timed out. Please check your connection and try again."),
        NETWORK_ERROR     ("Network error. Please try again."),
        PARSING_ERROR     ("Received an unexpected response. Please try again."),
        EMPTY_PROMPT      ("Please enter a message before sending.");

        public final String message;
        GeminiError(String message) { this.message = message; }
    }
}