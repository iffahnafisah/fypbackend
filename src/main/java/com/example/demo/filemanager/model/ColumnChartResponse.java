package com.example.demo.filemanager.model;

public class ColumnChartResponse {

    private String month;
    private long positiveCount;
    private long negativeCount;

    public ColumnChartResponse(String month, long positiveCount, long negativeCount) {
        this.month = month;
        this.positiveCount = positiveCount;
        this.negativeCount = negativeCount;
    }

    // Getters and setters
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public long getPositiveCount() {
        return positiveCount;
    }

    public void setPositiveCount(long positiveCount) {
        this.positiveCount = positiveCount;
    }

    public long getNegativeCount() {
        return negativeCount;
    }

    public void setNegativeCount(long negativeCount) {
        this.negativeCount = negativeCount;
    }
}
