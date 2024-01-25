package com.khush.videotranscoder;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.model.AssignPublicIp;
import com.amazonaws.services.ecs.model.AwsVpcConfiguration;
import com.amazonaws.services.ecs.model.ContainerOverride;
import com.amazonaws.services.ecs.model.Failure;
import com.amazonaws.services.ecs.model.KeyValuePair;
import com.amazonaws.services.ecs.model.LaunchType;
import com.amazonaws.services.ecs.model.NetworkConfiguration;
import com.amazonaws.services.ecs.model.RunTaskRequest;
import com.amazonaws.services.ecs.model.RunTaskResult;
import com.amazonaws.services.ecs.model.TaskOverride;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import com.khush.videotranscoder.Services.ECSServiceImpl;
import com.khush.videotranscoder.Services.S3ServiceImpl;

import io.github.cdimascio.dotenv.Dotenv;

public class SQSLambdaTrigger implements RequestHandler<SQSEvent, String> {

    private MainObject mainObject = new MainObject();
    private S3ServiceImpl s3Service = new S3ServiceImpl();
    private ECSServiceImpl ecsService = new ECSServiceImpl();

    private static final Dotenv dotenv = Dotenv.load();
    private static final Logger log = LoggerFactory.getLogger(SQSLambdaTrigger.class);
    private static final AmazonSQS sqsClient = AwsClientProvider.getSqsclient();
    private static final AmazonECS ecsClient = AwsClientProvider.getEcsclient();
    private static final String accessKey = dotenv.get("USER_ACCESS_KEY");
    private static final String secretAccessKey = dotenv.get("USER_SECRET_ACCESS_KEY");
    private static final String region = dotenv.get("AWS_REGION");

    @Override
    public String handleRequest(SQSEvent sqsEvent, Context context) {
        for(SQSMessage message : sqsEvent.getRecords()) {
            String json = message.getBody();
            try {
                JsonNode jsonNode = Jackson.jsonNodeOf(json);
                String messageJson = jsonNode.get("Message").asText();

                JsonNode messageJsonNode = Jackson.jsonNodeOf(messageJson);
                if(messageJsonNode.has("Event") && messageJsonNode.get("Event").asText().equals("s3:TestEvent")) {
                    log.info("Test event received, skipping!");
                    
                    String reciptHandle = message.getReceiptHandle();
                    final DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest().withQueueUrl(mainObject.getSqsQueueURL()).withReceiptHandle(reciptHandle);
                    sqsClient.deleteMessage(deleteMessageRequest);
                } else {
                    JsonNode messageNode = Jackson.jsonNodeOf(messageJson);
                    String objectKey = messageNode.get("Records").get(0).get("s3").get("object").get("key").asText();
                    log.info("Object Key: " + objectKey);

                    URL getUrlToTempBucket = s3Service.getPreSignedUrlGeneratorForAnyBucket(mainObject.getTempBucketName(), objectKey);
                    log.info("Get Url to the object: "+ getUrlToTempBucket.toString());
                    String permBucketName = mainObject.getPermBucketName();

                    log.info("Setting up environment variable");
                    KeyValuePair objectKeyVariable = new KeyValuePair().withName("OBJECT_KEY").withValue(objectKey);
                    KeyValuePair getUrlVariable = new KeyValuePair().withName("GET_URL").withValue(getUrlToTempBucket.toString());                    
                    KeyValuePair accessKeyVariable = new KeyValuePair().withName("ACCESS_KEY").withValue(accessKey);
                    KeyValuePair secretAccessKeyVariable = new KeyValuePair().withName("SECRET_ACCESS_KEY").withValue(secretAccessKey);
                    KeyValuePair regionVariable = new KeyValuePair().withName("REGION").withValue(region);
                    KeyValuePair permBucketNameVariable = new KeyValuePair().withName("PERM_BUCKET_NAME").withValue(permBucketName);

                    log.info("Setting up container override");
                    List<KeyValuePair> environmentVariables = Arrays.asList(objectKeyVariable, getUrlVariable, accessKeyVariable, secretAccessKeyVariable, regionVariable, permBucketNameVariable);
                    String containerName = mainObject.getContainerName();
                    ContainerOverride containerOverride = new ContainerOverride().withName(containerName).withEnvironment(environmentVariables);

                    log.info("Getting default subnets");
                    List<String> subnets = ecsService.getDefaultSubnets();

                    log.info("Creating security group");
                    String securityGroupName = mainObject.getSecurityGroupName();
                    String securityGroupId = ecsService.getSecurityGroupId(securityGroupName);
                    if(securityGroupId == null) {
                        securityGroupId = ecsService.createSecurityGroup(securityGroupName);
                    }

                    log.info("Setting up network configuration");
                    NetworkConfiguration networkConfiguration = new NetworkConfiguration().withAwsvpcConfiguration(new AwsVpcConfiguration().withSubnets(subnets).withSecurityGroups(securityGroupId).withAssignPublicIp(AssignPublicIp.ENABLED));

                    log.info("Setting up run task request");
                    RunTaskRequest runTaskRequest = new RunTaskRequest()
                                        .withCluster(mainObject.getClusterName())
                                        .withTaskDefinition(mainObject.getTaskDefinationArn())
                                        .withOverrides(new TaskOverride().withContainerOverrides(containerOverride))
                                        .withLaunchType(LaunchType.FARGATE)
                                        .withNetworkConfiguration(networkConfiguration);

                    log.info("Starting task");
                    RunTaskResult runTaskResult = ecsClient.runTask(runTaskRequest);
                    log.info("Task started: " + runTaskResult.getTasks());
                
                    if(!runTaskResult.getFailures().isEmpty()) {
                        for(Failure failure : runTaskResult.getFailures()) {
                            log.error("Task start failure: " + failure.getReason());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error parsing JSON message", e);
                e.printStackTrace();
            }
        }

        return "Success";
        
    }
    
}
