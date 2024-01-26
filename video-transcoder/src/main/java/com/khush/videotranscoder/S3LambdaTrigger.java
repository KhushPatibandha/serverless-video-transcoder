package com.khush.videotranscoder;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import com.khush.videotranscoder.Services.S3ServiceImpl;

import io.github.cdimascio.dotenv.Dotenv;

public class S3LambdaTrigger implements RequestHandler<SQSEvent, String>{

    private MainObject mainObject = new MainObject();
    private S3ServiceImpl s3Service = new S3ServiceImpl();

    private static final Dotenv dotenv = Dotenv.load();
    private static final Logger log = LoggerFactory.getLogger(S3LambdaTrigger.class);
    private static final String emailAddress = dotenv.get("EMAIL_ADDRESS");
    private static final AmazonSQS sqsClient = AwsClientProvider.getSqsclient();
    private static final AmazonSimpleEmailService sesClient = AwsClientProvider.getSesclient();

    @Override
    public String handleRequest(SQSEvent sqsEvent, Context context) {
        ArrayList<String> urls = new ArrayList<>();
        for(SQSMessage message : sqsEvent.getRecords()) {
            String json = message.getBody();
            try {
                JsonNode jsonNode = Jackson.jsonNodeOf(json);
                String messageJson = jsonNode.get("Message").asText();

                JsonNode messageJsonNode = Jackson.jsonNodeOf(messageJson);
                if(messageJsonNode.has("Event") && messageJsonNode.get("Event").asText().equals("s3:TestEvent")) {
                    System.out.println("Test event received, skipping!");
                    
                    String reciptHandle = message.getReceiptHandle();
                    final DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest().withQueueUrl(mainObject.getSqsQueueUrlForPermS3()).withReceiptHandle(reciptHandle);
                    sqsClient.deleteMessage(deleteMessageRequest);
                } else {
                    JsonNode messageNode = Jackson.jsonNodeOf(messageJson);
                    String objectKey = messageNode.get("Records").get(0).get("s3").get("object").get("key").asText();
                    log.info("Object key: " + objectKey);

                    String getUrl = s3Service.getPreSignedUrlGeneratorForAnyBucket(mainObject.getPermBucketName(), objectKey).toString();
                    urls.add(getUrl);
                }
            } catch (Exception e) {
                log.error("Error parsing JSON message", e);
                e.printStackTrace();
            }
        }
        String urlsString = String.join("\n", urls);

        SendEmailRequest request = new SendEmailRequest()
                                        .withDestination(new Destination().withToAddresses(emailAddress))
                                        .withMessage(new Message()
                                                .withBody(new Body()
                                                        .withText(new Content()
                                                                .withCharset("UTF-8")
                                                                .withData(urlsString)))
                                                .withSubject(new Content()
                                                        .withCharset("UTF-8")
                                                        .withData("Your URLs")))
                                        .withSource(emailAddress);

        sesClient.sendEmail(request);

        return urls.toString();
    }
    
}
