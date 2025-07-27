package com.fyp.chatbot.viewModels;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.chatbot.apimodels.Content;
import com.fyp.chatbot.apimodels.GeminiResponse;
import com.fyp.chatbot.apimodels.Part;
import com.fyp.chatbot.apimodels.RequestBodyGemini;
import com.fyp.chatbot.helpers.RetrofitClient;
import com.fyp.chatbot.interfaces.GeminiApi;
import com.fyp.chatbot.models.MessagesModel;
import com.fyp.chatbot.repository.GeminiRepo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBotViewModel extends ViewModel {
    MutableLiveData<String> aiResponse = new MutableLiveData<>();
    GeminiRepo geminiRepo = new GeminiRepo();
    public MutableLiveData<String> getResponse() {
        return aiResponse;
    }


    public void setResponse(List<Map<String,String>> chatHistory,String apiKey) {
        RetrofitClient retrofitClient = new
                RetrofitClient("https://generativelanguage.googleapis.com/");
        GeminiApi geminiApi =  retrofitClient.getRetrofit().create(GeminiApi.class);
        List<Part> parts = new ArrayList<>();
        for (Map<String ,String > entry:chatHistory){
            parts.add(new Part(entry.get("content")));
        }
        List<Content> contents = new ArrayList<>();
        contents.add(new Content(parts));
        RequestBodyGemini requestBody = new RequestBodyGemini(contents);
        Call<GeminiResponse> call = geminiApi.generateContent(apiKey, requestBody);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String reply = response.body()
                            .getCandidates()
                            .get(0)
                            .getContent()
                            .getParts()
                            .get(0)
                            .getText();
                    aiResponse.postValue(reply);
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                aiResponse.postValue("Error: " + t.getMessage());
            }
        });
    }
}
