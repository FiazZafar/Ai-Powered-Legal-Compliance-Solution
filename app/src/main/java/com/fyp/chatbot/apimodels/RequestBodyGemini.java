package com.fyp.chatbot.apimodels;

import java.util.List;

public class RequestBodyGemini {
    private List<Content> contents;

    public RequestBodyGemini(List<Content> contents) {
        this.contents = contents;
    }

    public List<Content> getContents() {
        return contents;
    }
}
