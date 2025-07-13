package com.fyp.chatbot.interfaces;

import com.fyp.chatbot.apimodels.GeminiResponse;
import com.fyp.chatbot.apimodels.RequestBodyGemini;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiApi {

    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    Call<GeminiResponse> generateContent(
            @Query("key") String apiKey,
            @Body RequestBodyGemini requestBody
    );
}
