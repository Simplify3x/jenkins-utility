package io.jenkins.plugins.sample.model;

public class ExecutionStatus {
    private final String status;
    private final double executedPercent;
    private final double failedPercent;

    public ExecutionStatus(String status, double executedPercent, double failedPercent) {
        this.status = status;
        this.executedPercent = executedPercent;
        this.failedPercent = failedPercent;
    }

    public String getStatus() {
        return status;
    }

    public double getExecutedPercent() {
        return executedPercent;
    }

    public double getFailedPercent() {
        return failedPercent;
    }
}
