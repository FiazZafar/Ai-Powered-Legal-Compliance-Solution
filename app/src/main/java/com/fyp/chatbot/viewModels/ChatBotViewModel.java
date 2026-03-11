package com.fyp.chatbot.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.chatbot.apimodels.Content;
import com.fyp.chatbot.apimodels.Part;
import com.fyp.chatbot.repository.GeminiRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatBotViewModel extends ViewModel {

    private final MutableLiveData<String>  aiResponse   = new MutableLiveData<>();
    private final MutableLiveData<String>  errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading    = new MutableLiveData<>(false);

    private final GeminiRepo geminiRepo = new GeminiRepo();

    public MutableLiveData<String>  getResponse()     { return aiResponse;   }
    public MutableLiveData<String>  getErrorMessage() { return errorMessage; }
    public MutableLiveData<Boolean> getIsLoading()    { return isLoading;    }

    public void setResponse(List<Map<String, String>> chatHistory, String apiKey) {

        if (Boolean.TRUE.equals(isLoading.getValue())) return;

        isLoading.postValue(true);

        List<Part> parts = new ArrayList<>();
        for (Map<String, String> entry : chatHistory) {
            String content = entry.get("content");
            if (content != null) parts.add(new Part(content));
        }
        List<Content> contents = new ArrayList<>();
        contents.add(new Content(parts));

        geminiRepo.generateChatResponse(contents, result -> {
            isLoading.postValue(false);

            if (isGeminiError(result)) {
                errorMessage.postValue(result);
            } else {
                aiResponse.postValue(result);
            }
        });
    }

    private boolean isGeminiError(String result) {
        if (result == null) return false;
        for (GeminiRepo.GeminiError error : GeminiRepo.GeminiError.values()) {
            if (error.message.equals(result)) return true;
        }
        return false;
    }
}