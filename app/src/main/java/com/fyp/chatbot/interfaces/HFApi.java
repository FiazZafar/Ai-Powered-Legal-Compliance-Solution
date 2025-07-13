package com.fyp.chatbot.interfaces;

import com.fyp.chatbot.apimodels.RequestSummary;
import com.fyp.chatbot.apimodels.ResponseSummary;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface HFApi {
    @Headers({
            "Authorization: Bearer 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", // 🔥 Replace YOUR_HF_API_KEY properly
            "Content-Type: application/json"
    })
    @POST("models/google/flan-t5-xl")
    Call<List<ResponseSummary>> summarizeText(@Body RequestSummary request);
}