package com.example.demo.filemanager.model;

import java.util.List;

public class UploadResponse {
	
	private List<PieChartResponse> pieChart;
	private List<SentimentTableResponse> sentimentTable;
	private List<List<Object>> lineChart;
	private long totalSize;
	
	
	public List<PieChartResponse> getPieChart() {
		return pieChart;
	}
	public void setPieChart(List<PieChartResponse> pieChart) {
		this.pieChart = pieChart;
	}
	
	public List<SentimentTableResponse> getSentimentTable() {
		return sentimentTable;
	}
	public void setSentimentTable(List<SentimentTableResponse> sentimentTable) {
		this.sentimentTable = sentimentTable;
	}
	
	public long getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	public List<List<Object>> getLineChart() {
		return lineChart;
	}
	public void setLineChart(List<List<Object>> lineChart) {
		this.lineChart = lineChart;
	}
	
	
		
}
