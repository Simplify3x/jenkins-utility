package io.jenkins.plugins.sample;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import io.jenkins.plugins.sample.model.ExecutionStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletException;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.json.JSONException;
import org.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

public class HelloWorldBuilder extends Builder implements SimpleBuildStep {

    private final String name;
    private boolean useFrench;
    private String apiUrl;
    private String apiKey;
    private String pipelineId;
    private String threshold;

    @DataBoundConstructor
    public HelloWorldBuilder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isUseFrench() {
        return useFrench;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    @DataBoundSetter
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    @DataBoundSetter
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPipelineId() {
        return pipelineId;
    }

    @DataBoundSetter
    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getThreshold() {
        return threshold;
    }

    @DataBoundSetter
    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    @DataBoundSetter
    public void setUseFrench(boolean useFrench) {
        this.useFrench = useFrench;
    }

//        @Override
//        public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
//                throws InterruptedException, IOException {
//            String apiUrl = env.get("API URL");
//            String apiKey = env.get("API Key");
//            String pipelineId = env.get("Pipeline ID");
//
//            listener.getLogger().println("API URL: " + apiUrl);
//            listener.getLogger().println("API Key: " + apiKey);
//            listener.getLogger().println("Pipeline ID: " + pipelineId);
//
//            // Start Execution
//            ExecutionResponse startResponse = startPipelineExecution(apiUrl, apiKey, pipelineId, listener);
//            if (startResponse != null) {
//                String projectId = startResponse.getProjectId();
//                String execId = startResponse.getExecutionId();
//
//                listener.getLogger().println("Pipeline execution started successfully.");
//                listener.getLogger().println("Project ID: " + projectId);
//                listener.getLogger().println("Execution ID: " + execId);
//
//                // Check Status
//                if (checkPipelineStatus(apiUrl, apiKey, projectId, execId, listener)) {
//                    listener.getLogger().println("Pipeline execution status retrieved successfully.");
//                } else {
//                    listener.getLogger().println("Failed to retrieve pipeline execution status.");
//                }
//            } else {
//                listener.getLogger().println("Failed to start pipeline execution.");
//            }
//        }
    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {
        String apiUrl = env.get("API URL");
        String apiKey = env.get("API Key");
        String pipelineId = env.get("Pipeline ID");
        double threshold = Double.parseDouble(env.getOrDefault("THRESHOLD", "100")); // Default to 100 if not provided
        //    boolean verbose = Boolean.parseBoolean(env.getOrDefault("VERBOSE", "false"));

        listener.getLogger()
                .println(
                        "************************************** SIMPLIFYQA PIPELINE EXECUTOR **************************************");
        listener.getLogger().println("Pipeline Execution Started...");
        listener.getLogger().println("API URL: " + apiUrl);
        listener.getLogger().println("Pipeline ID: " + pipelineId);

        // Start Execution
        ExecutionResponse startResponse = startPipelineExecution(apiUrl, apiKey, pipelineId, listener);
        if (startResponse == null) {
            listener.getLogger().println("Failed to start execution.");
            run.setResult(Result.FAILURE);
            return;
        }

        String projectId = startResponse.getProjectId();
        String execId = startResponse.getExecutionId();
        listener.getLogger().println("Execution Started - Project ID: " + projectId + ", Execution ID: " + execId);

        try {
            ExecutionStatus currentStatus;
            ExecutionStatus previousStatus = null;

            do {
                // Fetch Execution Status
                currentStatus = fetchPipelineStatus(apiUrl, apiKey, projectId, execId, listener);
                if (currentStatus == null) {
                    listener.getLogger().println("Failed to fetch execution status.");
                    run.setResult(Result.FAILURE);
                    return;
                }

                // Print progress if updated
                if (previousStatus == null
                        || previousStatus.getExecutedPercent() < currentStatus.getExecutedPercent()) {
                    listener.getLogger().println("Progress: " + currentStatus.getExecutedPercent() + "%");
                }

                // Check threshold
                if (currentStatus.getFailedPercent() >= threshold) {
                    listener.getLogger().println("Threshold reached (" + threshold + "%). Stopping execution...");
                    stopPipelineExecution(apiUrl, apiKey, projectId, execId, listener);
                    run.setResult(Result.FAILURE);
                    return;
                }

                previousStatus = currentStatus;
                Thread.sleep(5000); // Delay for status polling

            } while ("INPROGRESS".equalsIgnoreCase(currentStatus.getStatus()));

            // Handle final status
            if ("FAILED".equalsIgnoreCase(currentStatus.getStatus())) {
                listener.getLogger().println("Execution failed. Stopping pipeline...");
                stopPipelineExecution(apiUrl, apiKey, projectId, execId, listener);
                run.setResult(Result.FAILURE);
            } else {
                listener.getLogger().println("Execution completed successfully.");
                run.setResult(Result.SUCCESS);
            }

        } catch (Exception e) {
            listener.getLogger().println("Error occurred: " + e.getMessage());
            stopPipelineExecution(apiUrl, apiKey, projectId, execId, listener);
            run.setResult(Result.FAILURE);
        }
    }

    private void stopPipelineExecution(
            String apiUrl, String apiKey, String projectId, String execId, TaskListener listener) {
        String urlStr = apiUrl + "/pl/exec/stop/" + projectId + "/" + execId;
        listener.getLogger().println("Stopping execution at: " + urlStr);
        makeApiCall("POST", urlStr, apiKey, listener);
    }

    private ExecutionStatus fetchPipelineStatus(
            String apiUrl, String apiKey, String projectId, String execId, TaskListener listener) {
        String urlStr = apiUrl + "/pl/exec/status/" + projectId + "/" + execId;
        listener.getLogger().println("Fetching status from: " + urlStr);
        try {
            // Perform GET request
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);

            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                double executedPercent = jsonResponse.optDouble("executedPercent", 0.0);
                double failedPercent = jsonResponse.optDouble("failedPercent", 0.0);
                String status=jsonResponse.getString("status");

                listener.getLogger().println("Status: " + status);
                listener.getLogger().println("Executed Percent: " + executedPercent + "%");
                listener.getLogger().println("Failed Percent: " + failedPercent + "%");
                return new ExecutionStatus(status, executedPercent, failedPercent);
            } else {
                listener.getLogger().println("Failed to fetch status, response code: " + responseCode);
                return null;
            }

        } catch (Exception e) {
            listener.getLogger().println("Error fetching status: " + e.getMessage());
            return null;
        }
    }

    private ExecutionResponse startPipelineExecution(
            String apiUrl, String apiKey, String pipelineId, TaskListener listener) {
        String urlStr = apiUrl + "/pl/exec/start/" + pipelineId;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();
            listener.getLogger().println("Response Code (POST): " + responseCode);

            if (responseCode >= 200 && responseCode < 300) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseBody = response.toString();

                if (responseBody != null && !responseBody.isEmpty()) {
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    // Handle projectId (dynamic type handling)
                    Object projectIdObj = jsonResponse.get("projectId");
                    String projectId;
                    if (projectIdObj instanceof String) {
                        projectId = (String) projectIdObj;
                    } else if (projectIdObj instanceof Number) {
                        projectId = String.valueOf(projectIdObj);
                    } else {
                        throw new JSONException("Unexpected type for projectId");
                    }

                    //                    String execId = jsonResponse.getString("id");
                    Object execIdObj = jsonResponse.get("id");
                    String execId;
                    if (execIdObj instanceof String) {
                        execId = (String) execIdObj;
                    } else if (execIdObj instanceof Number) {
                        execId = String.valueOf(execIdObj);
                    } else {
                        throw new JSONException("Unexpected type for id");
                    }
                    listener.getLogger()
                            .println("API call successful. Project ID: " + projectId + ", Execution ID: " + execId);

                    return new ExecutionResponse(projectId, execId);
                } else {
                    listener.getLogger().println("Response body is empty or null.");
                    return null;
                }
            } else {
                listener.getLogger().println("API call failed with response code: " + responseCode);
                return null;
            }
        } catch (IOException | JSONException e) {
            listener.getLogger().println("Error during API call (POST): " + e.getMessage());
            return null;
        }
    }

    private boolean checkPipelineStatus(
            String apiUrl, String apiKey, String projectId, String execId, TaskListener listener) {
        String urlStr = apiUrl + "/pl/exec/status/" + projectId + "/" + execId;
        return makeApiCall("GET", urlStr, apiKey, listener);
    }

    private boolean makeApiCall(String method, String urlStr, String apiKey, TaskListener listener) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();
            listener.getLogger().println("Response Code (" + method + "): " + responseCode);
            if (responseCode >= 400) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = in.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                in.close();
                listener.getLogger().println("Error Response Body: " + errorResponse.toString());
            }
            if (responseCode >= 200 && responseCode < 300) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseBody = response.toString();
                listener.getLogger().println("Response Body: " + responseBody);

                return responseBody != null && !responseBody.isEmpty();
            } else {
                listener.getLogger().println("API call failed with response code: " + responseCode);
                return false;
            }

        } catch (IOException e) {
            listener.getLogger().println("Error during API call (" + method + "): " + e.getMessage());
            return false;
        }
    }

    // Helper class to encapsulate the response from the startPipelineExecution API
    static class ExecutionResponse {
        private final String projectId;
        private final String executionId;

        public ExecutionResponse(String projectId, String executionId) {
            this.projectId = projectId;
            this.executionId = executionId;
        }

        public String getProjectId() {
            return projectId;
        }

        public String getExecutionId() {
            return executionId;
        }
    }

    //        @Override
    //        public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener
    // listener)
    //                throws InterruptedException, IOException {
    //            listener.getLogger().println("Entered perform method");
    //            listener.getLogger().println("API URL: " + env.get("API URL"));
    //            listener.getLogger().println("API Key: " + env.get("API Key"));
    //            listener.getLogger().println("Pipeline ID: " + env.get("Pipeline ID"));
    //            listener.getLogger().println("Threshold: " + env.get("Threshold"));
    //
    //            // Make the API call
    //            if (triggerApiCall(env.get("API URL"), env.get("API Key"), env.get("Pipeline ID"), listener)) {
    //                listener.getLogger().println("Pipeline execution triggered successfully.");
    //            } else {
    //                listener.getLogger().println("Failed to trigger pipeline execution.");
    //            }
    //
    //            // Print a friendly greeting
    //            if (useFrench) {
    //                listener.getLogger().println("Bonjour, " + name + "!");
    //            } else {
    //                listener.getLogger().println("Hello, " + name + "!");
    //            }
    //
    //        }
    //    public boolean triggerApiCall(String apiUrl, String apiKey, String pipelineId, TaskListener listener) {
    //        try {
    //            // Construct the API endpoint URL
    //            String urlStr = apiUrl + "/pl/exec/start/" + pipelineId;
    //
    //            // Open a connection
    //            URL url = new URL(urlStr);
    //            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    //            connection.setRequestMethod("POST");
    //            connection.setRequestProperty("Content-Type", "application/json");
    //            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
    //            connection.setDoOutput(true); // Allow sending a request body
    //
    //            // Send the request
    //            int responseCode = connection.getResponseCode();
    //            listener.getLogger().println("Response Code: " + responseCode);
    //
    //            // Check response
    //            if (responseCode >= 200 && responseCode < 300) {
    //                // Read the response body
    //                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    //                String inputLine;
    //                StringBuilder response = new StringBuilder();
    //                while ((inputLine = in.readLine()) != null) {
    //                    response.append(inputLine);
    //                }
    //                in.close();
    //
    //                // Log the response for debugging
    //                String responseBody = response.toString();
    //                listener.getLogger().println("Response Body: " + responseBody);
    //
    //                // Check if response is not empty
    //                if (responseBody != null && !responseBody.isEmpty()) {
    //                    // Parse the response JSON
    //                    try {
    //                        JSONObject jsonResponse = new JSONObject(responseBody);
    //                        String id = jsonResponse.getString("_id");
    //                        String projectId = jsonResponse.getString("projectId");
    //
    //                        listener.getLogger().println("API call successful. _id: " + id + ", projectId: " +
    // projectId);
    //                        return true;
    //                    } catch (Exception e) {
    //                        listener.getLogger().println("Error parsing JSON response: " + e.getMessage());
    //                        return false;
    //                    }
    //                } else {
    //                    listener.getLogger().println("Response body is empty or null.");
    //                    return false;
    //                }
    //            } else {
    //                listener.getLogger().println("API call failed with response code: " + responseCode);
    //                return false;
    //            }
    //        } catch (IOException e) {
    //            listener.getLogger().println("Error during API call: " + e.getMessage());
    //            return false;
    //        }
    //    }
    //        private boolean triggerApiCall(String apiUrl, String apiKey, String pipelineId, TaskListener listener) {
    //            try {
    //                // Construct the API endpoint URL
    //                String urlStr = apiUrl + "/pl/exec/start/" + pipelineId;
    //
    //                // Open a connection
    //                URL url = new URL(urlStr);
    //                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    //                connection.setRequestMethod("POST");
    //                connection.setRequestProperty("Content-Type", "application/json");
    //                connection.setRequestProperty("Authorization", "Bearer " + apiKey);
    //                connection.setDoOutput(true); // Allow sending a request body
    //
    //                // Send the request
    //                int responseCode = connection.getResponseCode();
    //                listener.getLogger().println("Response Code: " + responseCode);
    //
    //                // Check response
    //                if (responseCode >= 200 && responseCode < 300) {
    //                    listener.getLogger().println("API call successful.");
    //                    return true;
    //                } else {
    //                    listener.getLogger().println("API call failed with response code: " + responseCode);
    //                    return false;
    //                }
    //            } catch (IOException e) {
    //                listener.getLogger().println("Error during API call: " + e.getMessage());
    //                return false;
    //            }
    //        }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckName(@QueryParameter String value, @QueryParameter boolean useFrench)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.HelloWorldBuilder_DescriptorImpl_errors_missingName());
            if (value.length() < 4)
                return FormValidation.warning(Messages.HelloWorldBuilder_DescriptorImpl_warnings_tooShort());
            if (!useFrench && value.matches(".*[éáàç].*")) {
                return FormValidation.warning(Messages.HelloWorldBuilder_DescriptorImpl_warnings_reallyFrench());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckApiUrl(@QueryParameter String value) {
            if (value == null || value.trim().isEmpty()) {
                return FormValidation.error("API URL is required");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckApiKey(@QueryParameter String value) {
            if (value == null || value.trim().isEmpty()) {
                return FormValidation.error("API Key is required");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckPipelineId(@QueryParameter String value) {
            if (value == null || value.trim().isEmpty()) {
                return FormValidation.error("Pipeline ID is required");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckThreshold(@QueryParameter int value) {
            if (value < 0) {
                return FormValidation.error("Threshold cannot be negative");
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.HelloWorldBuilder_DescriptorImpl_DisplayName();
        }
    }
}
