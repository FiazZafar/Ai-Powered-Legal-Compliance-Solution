package com.fyp.chatbot.models;

public class DocomentModel {
    String docName;
    String analyzedTime;

    public DocomentModel(String docName, String analyzedTime){
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
