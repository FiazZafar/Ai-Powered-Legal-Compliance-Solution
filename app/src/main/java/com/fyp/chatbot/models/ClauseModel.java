package com.fyp.chatbot.models;

public class ClauseModel {
    String clauseTitle;
    String clauseBody;
    long timeStamp;

    public ClauseModel() {
    }

    public ClauseModel(String clauseTitle, String clauseBody, long timeStamp) {
        this.clauseTitle = clauseTitle;
        this.clauseBody = clauseBody;
        this.timeStamp = timeStamp;
    }
    public String getClauseTitle() {
        return clauseTitle;
    }

    public void setClauseTitle(String clauseTitle) {
        this.clauseTitle = clauseTitle;
    }

    public String getClauseBody() {
        return clauseBody;
    }

    public void setClauseBody(String clauseBody) {
        this.clauseBody = clauseBody;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}
