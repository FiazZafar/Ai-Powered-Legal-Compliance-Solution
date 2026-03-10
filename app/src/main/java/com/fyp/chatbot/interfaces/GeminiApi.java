package com.fyp.chatbot.interfaces;

import com.fyp.chatbot.apimodels.GeminiResponse;
import com.fyp.chatbot.apimodels.RequestBodyGemini;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiApi {
    // BEST for FYP - highest RPD (1000/day)
    @POST("v1beta/models/gemini-2.5-flash-lite:generateContent")
    Call<GeminiResponse> generateContent(
            @Query("key") String apiKey,
            @Body RequestBodyGemini requestBody
    );
}
