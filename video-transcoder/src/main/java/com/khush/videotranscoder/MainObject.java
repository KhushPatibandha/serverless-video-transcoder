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
    private String snsTopicForPermS3Name = "videoTranscoderTopicForPermS3";
    
    private String sqsQueueName = "videoTranscoderQueue";
    private String sqsQueueForPermS3Name = "videoTranscoderQueueForPermS3";
    
    private String lambdaFunctionName = "videoTranscoderLambdaFunction";
    private String lambdaS3FunctionName = "videoTranscoderLambdaFunctionForPermS3";
    
    private String roleNameForTaskExecution = "videoTranscoderRoleForTaskExecution";
    
    private String roleName = "videoTranscoderRoleForLambdaTriggerFromSQS";
    private String roleNameForPermS3LambdaTrigger = "videoTranscoderRoleForPermS3LambdaTrigger";
    
    private String containerName = "videotranscoder";
    private String taskDefinationName = "videoTranscoderTaskDefination";
    private String clusterName = "videoTranscoderCluster";
    private String securityGroupName = "videoTranscoderSecurityGroup";
    
    private String tempBucketArn = "arn:aws:s3:::" + tempBucketName;
    private String permBucketArn = "arn:aws:s3:::" + permBucketName;

    private String snsTopicArn = "arn:aws:sns:" + awsRegion + ":" + awsAccountID + ":" + snsTopicName;
    private String snsTopicForPermS3Arn = "arn:aws:sns:" + awsRegion + ":" + awsAccountID + ":" + snsTopicForPermS3Name;

    private String sqsQueueArn = "arn:aws:sqs:" + awsRegion + ":" + awsAccountID + ":" + sqsQueueName;
    private String sqsQueueURL = "https://sqs." + awsRegion +".amazonaws.com/" + awsAccountID + "/" + sqsQueueName;
    private String sqsQueueUrlForPermS3 = "https://sqs." + awsRegion +".amazonaws.com/" + awsAccountID + "/" + sqsQueueForPermS3Name;
    private String sqsQueueArnForPermS3 = "arn:aws:sqs:" + awsRegion + ":" + awsAccountID + ":" + sqsQueueForPermS3Name;

    private String lambdaFunctionArn = "arn:aws:lambda:" + awsRegion + ":" + awsAccountID + ":function:" + lambdaFunctionName;
    private String lambdaS3FunctionArn = "arn:aws:lambda:" + awsRegion + ":" + awsAccountID + ":function:" + lambdaS3FunctionName;

    private String roleArnTaskExecution = "arn:aws:iam::" + awsAccountID + ":role/" + roleNameForTaskExecution;

    private String roleArn = "arn:aws:iam::" + awsAccountID + ":role/" + roleName;
    private String roleNameForPermS3LambdaTriggerArn = "arn:aws:iam::" + awsAccountID + ":role/" + roleNameForPermS3LambdaTrigger;
    
    private String taskDefinationArn = "arn:aws:ecs:" + awsRegion + ":" + awsAccountID + ":task-definition/" + taskDefinationName;
    private String clusterArn = "arn:aws:ecs:" + awsRegion + ":" + awsAccountID + ":cluster/" + clusterName;
    private String jarFilePath = "target/video-transcoder-0.0.1-SNAPSHOT.jar";
    private String lambdaHandler = "com.khush.videotranscoder.SQSLambdaTrigger::handleRequest";
    private String lambdaS3Handler = "com.khush.videotranscoder.S3LambdaTrigger::handleRequest";
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

    public String getRoleNameForPermS3LambdaTrigger() {
        return roleNameForPermS3LambdaTrigger;
    }

    public String getRoleNameForPermS3LambdaTriggerArn() {
        return roleNameForPermS3LambdaTriggerArn;
    }

    public String getLambdaS3FunctionName() {
        return lambdaS3FunctionName;
    }

    public String getLambdaS3Handler() {
        return lambdaS3Handler;
    }

    public String getLambdaS3FunctionArn() {
        return lambdaS3FunctionArn;
    }

    public String getSnsTopicForPermS3Name() {
        return snsTopicForPermS3Name;
    }

    public String getSnsTopicForPermS3Arn() {
        return snsTopicForPermS3Arn;
    }

    public String getSqsQueueUrlForPermS3() {
        return sqsQueueUrlForPermS3;
    }

    public String getSqsQueueArnForPermS3() {
        return sqsQueueArnForPermS3;
    }

    public String getSqsQueueForPermS3Name() {
        return sqsQueueForPermS3Name;
    }

}
