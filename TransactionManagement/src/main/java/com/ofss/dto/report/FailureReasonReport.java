package com.ofss.dto.report;

public class FailureReasonReport {

    private String failureReason;
    private Long count;

    public FailureReasonReport() {
    }

    public FailureReasonReport(
            String failureReason,
            Long count) {
        this.failureReason = failureReason;
        this.count = count;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}