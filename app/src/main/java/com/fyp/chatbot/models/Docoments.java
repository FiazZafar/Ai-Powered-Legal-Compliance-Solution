package com.fyp.chatbot.models;

public class Docoments {
    String docName;
    String analyzedTime;

    public Docoments(String docName, String analyzedTime){
        this.analyzedTime = analyzedTime;
        this.docName = docName;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getAnalyzedTime() {
        return analyzedTime;
    }

    public void setAnalyzedTime(String analyzedTime) {
        this.analyzedTime = analyzedTime;
    }
}
