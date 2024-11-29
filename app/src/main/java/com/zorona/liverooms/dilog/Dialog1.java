package com.app.liverooms.dilog;

public class Dialog1 {
    private String title;
    private String positiveText;
    private String NegativeText;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPositiveText() {
        return positiveText;
    }

    public void setPositiveText(String positiveText) {
        this.positiveText = positiveText;
    }

    public String getNegativeText() {
        return NegativeText;
    }

    public void setNegativeText(String negativeText) {
        NegativeText = negativeText;
    }
}
