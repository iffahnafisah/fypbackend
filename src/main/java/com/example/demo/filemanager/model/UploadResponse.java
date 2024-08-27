package com.example.demo.filemanager.model;

import java.util.List;

public class UploadResponse {
	
	private List<PieChartResponse> pieChart;
	private List<SentimentTableResponse> sentimentTable;
	private List<List<Object>> lineChart;
	private List<List<Object>> columnChart;
	private long totalSize;
	private long totalPos;
	private long totalNeg;
	private long totalNeu;
	private String bestFocusArea;
    private String poorFocusArea;
	
	
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
	public long getTotalPos() {
		return totalPos;
	}
	public void setTotalPos(long totalPos) {
		this.totalPos = totalPos;
	}
	public long getTotalNeg() {
		return totalNeg;
	}
	public void setTotalNeg(long totalNeg) {
		this.totalNeg = totalNeg;
	}
	public long getTotalNeu() {
		return totalNeu;
	}
	public void setTotalNeu(long totalNeu) {
		this.totalNeu = totalNeu;
	}
	public List<List<Object>> getLineChart() {
		return lineChart;
	}
	public void setLineChart(List<List<Object>> lineChart) {
		this.lineChart = lineChart;
	}
	
	public List<List<Object>> getColumnChart() {
        return columnChart;
    }

    public void setColumnChart(List<List<Object>> columnChart) {
        this.columnChart = columnChart;
    }
    
    public String getBestFocusArea() {
        return bestFocusArea;
    }

    public void setBestFocusArea(String bestFocusArea) {
        this.bestFocusArea = bestFocusArea;
    }

    public String getPoorFocusArea() {
        return poorFocusArea;
    }

    public void setPoorFocusArea(String poorFocusArea) {
        this.poorFocusArea = poorFocusArea;
    }
	
}
