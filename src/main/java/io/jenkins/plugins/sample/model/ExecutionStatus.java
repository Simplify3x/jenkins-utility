package io.jenkins.plugins.sample.model;

public class ExecutionStatus {
    private final String status;
    String testcase;


    public ExecutionStatus(String status,String testcase) {
        this.status = status;
        this.testcase=testcase;

    }

    public String getStatus() {
        return status;
    }

    public String getTestcase() {
        return testcase;
    }

    public void setTestcase(String testcase) {
        this.testcase = testcase;
    }
}
