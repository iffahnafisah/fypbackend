package com.example.demo.filemanager.model;

public class PieChartResponse {

	public PieChartResponse(String x, long value) {
		super();
		this.x = x;
		this.value = value;
	}
	
	private String x;
	private long value;
	
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	

	
	
}
