package com.khush.videotranscoder;

import org.springframework.stereotype.Component;

import io.github.cdimascio.dotenv.Dotenv;

@Component
public class MainObject {
    private final Dotenv dotenv = Dotenv.load();
    private final String awsAccountID = dotenv.get("AWS_ACCOUNT_ID");
    private final String awsRegion = dotenv.get("AWS_REGION");

    private String tempBucketName = dotenv.get("TEMP_BUCKET_NAME");
    private String permBucketName = dotenv.get("PERM_BUCKET_NAME");
    private String snsTopicName = "videoTranscoderTopic";
    private String sqsQueueName = "videoTranscoderQueue";
    private String lambdaFunctionName = "videoTranscoderLambdaFunction";
    private String roleName = "videoTranscoderRoleForLambdaTriggerFromSQS";
    private String containerName = "videoTranscoder";
    private String taskDefinationName = "videoTranscoderTaskDefination";
    private String clusterName = "videoTranscoderCluster";
    private String roleNameForTaskExecution = "videoTranscoderRoleForTaskExecution";
    private String securityGroupName = "videoTranscoderSecurityGroup";
    private String tempBucketArn = "arn:aws:s3:::" + tempBucketName;
    private String permBucketArn = "arn:aws:s3:::" + permBucketName;
    private String snsTopicArn = "arn:aws:sns:" + awsRegion + ":" + awsAccountID + ":" + snsTopicName;
    private String sqsQueueURL = "https://sqs." + awsRegion +".amazonaws.com/" + awsAccountID + "/" + sqsQueueName;
    private String sqsQueueArn = "arn:aws:sqs:" + awsRegion + ":" + awsAccountID + ":" + sqsQueueName;
    private String lambdaFunctionArn = "arn:aws:lambda:" + awsRegion + ":" + awsAccountID + ":function:" + lambdaFunctionName;
    private String roleArn = "arn:aws:iam::" + awsAccountID + ":role/" + roleName;
    private String taskDefinationArn = "arn:aws:ecs:" + awsRegion + ":" + awsAccountID + ":task-definition/" + taskDefinationName;
    private String clusterArn = "arn:aws:ecs:" + awsRegion + ":" + awsAccountID + ":cluster/" + clusterName;
    private String roleArnTaskExecution = "arn:aws:iam::" + awsAccountID + ":role/" + roleNameForTaskExecution;
    private String jarFilePath = "target/video-transcoder-0.0.1-SNAPSHOT.jar";
    private String lambdaHandler = "com.khush.videotranscoder.SQSLambdaTrigger::handleRequest";
    private String dockerImageLink = "docker.io/khushpatibandha/videotranscoder:2.0";

    public String getTempBucketName() {
        return tempBucketName;
    }

    public String getPermBucketName() {
        return permBucketName;
    }

    public String getSnsTopicName() {
        return snsTopicName;
    }

    public String getSnsTopicArn() {
        return snsTopicArn;
    }

    public String getTempBucketArn() {
        return tempBucketArn;
    }

    public String getPermBucketArn() {
        return permBucketArn;
    }

    public String getSqsQueueName() {
        return sqsQueueName;
    }

    public String getSqsQueueURL() {
        return sqsQueueURL;
    }

    public String getSqsQueueArn() {
        return sqsQueueArn;
    }

    public String getLambdaFunctionName() {
        return lambdaFunctionName;
    }

    public String getLambdaFunctionArn() {
        return lambdaFunctionArn;
    }

    public String getJarFilePath() {
        return jarFilePath;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRoleArn() {
        return roleArn;
    }

    public String getContainerName() {
        return containerName;
    }

    public String getTaskDefinationName() {
        return taskDefinationName;
    }

    public String getTaskDefinationArn() {
        return taskDefinationArn;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getClusterArn() {
        return clusterArn;
    }

    public String getRoleNameForTaskExecution() {
        return roleNameForTaskExecution;
    }

    public String getDockerImageLink() {
        return dockerImageLink;
    }

    public String getRoleArnTaskExecution() {
        return roleArnTaskExecution;
    }

    public String getLambdaHandler() {
        return lambdaHandler;
    }

    public String getSecurityGroupName() {
        return securityGroupName;
    }

}
