package com.example.demo.filemanager.model;

public class SentimentTableResponse {

	private String sentiment;
	private String cleanText;
	private String focusArea;

	public String getSentiment() {
		return sentiment;
	}

	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}

	public String getCleanText() {
		return cleanText;
	}

	public void setCleanText(String cleanText) {
		this.cleanText = cleanText;
	}
	
	public String getFocusArea() {
        return focusArea;  // Getter for the focus area
    }

    public void setFocusArea(String focusArea) {
        this.focusArea = focusArea;  // Setter for the focus area
    }
	
}
