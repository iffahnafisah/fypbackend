package com.example.demo.filemanager.model;

import lombok.Data;

@Data
public class PythonResponse {

	private String focusArea;
	private String recordedDate;
	private String sentiment;
	private String state;
	private String clean_text;
	private String text;
	
	
	public String getFocusArea() {
		return focusArea;
	}
	public void setFocusArea(String focusArea) {
		this.focusArea = focusArea;
	}
	public String getRecordedDate() {
		return recordedDate;
	}
	public void setRecordedDate(String recordedDate) {
		this.recordedDate = recordedDate;
	}
	public String getSentiment() {
		return sentiment;
	}
	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getClean_text() {
		return clean_text;
	}
	public void setClean_text(String clean_text) {
		this.clean_text = clean_text;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
	
}
