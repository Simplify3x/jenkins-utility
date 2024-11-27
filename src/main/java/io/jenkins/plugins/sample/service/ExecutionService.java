package io.jenkins.plugins.sample.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

public class ExecutionService {
    private static final Logger logger = Logger.getLogger(ExecutionService.class.getName());
    private static final int MAX_RETRIES = 6; // 6 retries
    private static final int RETRY_INTERVAL = 10 * 1000; // 10 seconds

    public static void executionStart(int pipelineId, String apiUrl, String apiKey) {
        String url = String.format("%s/pl/exec/start/%d", apiUrl, pipelineId);

        Timer timer = new Timer();
        final int[] retries = {0};

        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (retries[0] >= MAX_RETRIES) {
                            logger.log(
                                    Level.SEVERE, "Failed to start pipeline execution after {0} retries", MAX_RETRIES);
                            timer.cancel();
                            return;
                        }

                        try {
                            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                            connection.setConnectTimeout(5 * 60 * 1000); // 5 minutes
                            connection.setReadTimeout(5 * 60 * 1000);
                            connection.setDoOutput(true);

                            int responseCode = connection.getResponseCode();
                            if (responseCode >= 200 && responseCode < 300) {
                                String response = IOUtils.toString(connection.getInputStream(), "UTF-8");
                                logger.log(Level.INFO, "Pipeline execution started successfully: {0}", response);
                                timer.cancel();
                            } else {
                                logger.log(Level.WARNING, "Retrying... Attempt {0}", retries[0] + 1);
                                retries[0]++;
                            }
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "Error in starting the pipeline execution: {0}", e.getMessage());
                            retries[0]++;
                        }
                    }
                },
                0,
                RETRY_INTERVAL);
    }
}
