# Jenkins Pipeline Executor Plugin

## Overview

The Jenkins Pipeline Executor Plugin is designed to trigger and manage the execution of a pipeline through a third-party API. It interacts with the pipeline system by initiating test executions via the start API, capturing the status periodically, and automatically stopping the execution when a specified threshold is reached.

## Key Features:

*Start pipeline execution through an external API.
*Periodically fetch the execution status.
*Automatically stop the execution when the failure percentage exceeds a user-defined threshold.
*Supports flexible configuration options such as custom threshold and verbosity.


## Prerequisites

Before using this plugin, ensure that:

1.Jenkins is properly set up and configured.
2.The necessary API URL and API Key for pipeline interaction are available.
3.The pipeline system is accessible via the provided API for execution status tracking.
4.The plugin supports running tests in your desired pipeline configuration.

Report issues and enhancements in the [Jenkins issue tracker](https://issues.jenkins.io/).

## Installation

1.Download the plugin JAR file or build it from the source code.
2.Go to Jenkins Dashboard > Manage Jenkins > Manage Plugins.
3.Click on the Advanced tab and upload the plugin JAR file.
4.Restart Jenkins to apply the plugin.

## Configuration
## Global Configuration:
In Jenkins, navigate to the Manage Jenkins > Configure System to add the following global parameters:

*API URL: The base URL of the API to interact with the pipeline.
*API Key: The API key to authenticate the requests.
*Pipeline ID: The unique identifier of the pipeline to be triggered.
*Threshold: A percentage value indicating the failure rate threshold at which the execution should be stopped (default is 100).
These settings will be used to configure the pipeline execution.

## Usage
## Step 1: Add the Pipeline Executor Build Step
*Create or open your Jenkins pipeline job.
*Add a new Build Step: Select Execute Pipeline Executor.
*Configure the API URL, API Key, Pipeline ID, and Threshold.

## Step 2: Execution
1.When the build is triggered, the plugin will:
   *Start the pipeline execution via the start API.
   *Periodically fetch the execution status until the threshold is reached or the execution fails.
   *If the failure percentage reaches or exceeds the threshold, the execution will be automatically stopped.
2.The plugin will log the execution status and the result of the pipeline in the build logs.

## Example Configuration
In the Build Step configuration, you should provide the following environment variables:

Parameter	Description
API URL	The API endpoint URL to trigger the pipeline execution.
API Key	The API key for authenticating the API requests.
Pipeline ID	The ID of the pipeline to execute.
Threshold	The threshold (failure percentage) at which execution should stop (optional).

## Build Logs
The plugin outputs the following logs:

API call logs: The plugin logs when the execution starts, and any intermediate responses from the API.
Execution status logs: Logs detailing the current status of the execution, including success or failure.
Threshold reached logs: When the failure percentage exceeds the defined threshold, the plugin will log the message and stop the execution.
Completion logs: The final result will be logged, indicating whether the execution was successful or failed.

## License
This plugin is licensed under the MIT License. See the LICENSE file for details.

Licensed under MIT, see [LICENSE](LICENSE.md)

