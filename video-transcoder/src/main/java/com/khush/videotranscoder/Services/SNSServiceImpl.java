package com.khush.videotranscoder.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SetTopicAttributesRequest;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQS;
import com.khush.videotranscoder.AwsClientProvider;
import com.khush.videotranscoder.MainObject;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class SNSServiceImpl implements SNSService {
    
    @Autowired
    private MainObject mainObject;

    private static final Dotenv dotenv = Dotenv.load();
    private static final AmazonSNS snsClient = AwsClientProvider.getSnsclient();
    private static final AmazonSQS sqsClient = AwsClientProvider.getSqsclient();
    private static final String ownerAccountId = dotenv.get("AWS_ACCOUNT_ID");

    @Override
    public String createSNSTopic() {
        String topicName = mainObject.getSnsTopicName();
        String bucketArn = mainObject.getTempBucketArn();
        CreateTopicRequest request = new CreateTopicRequest(topicName);
        CreateTopicResult result = snsClient.createTopic(request);

        String topicArn = result.getTopicArn();

        String policy = (
                """
                {\
                "Version":"2012-10-17",\
                "Id":"%s",\
                "Statement":[\
                {\
                "Sid":"AllowS3BucketToPublish",\
                "Effect":"Allow",\
                "Principal":{\
                "Service":"s3.amazonaws.com"\
                },\
                "Action":["SNS:Publish"],\
                "Resource":"%s",\
                "Condition":{\
                "ArnLike":{\
                "aws:SourceArn":"%s"\
                },\
                "StringEquals":{\
                "aws:SourceAccount":"%s"\
                }\
                }\
                }\
                ]\
                }\
                """).formatted(
                topicArn, topicArn, bucketArn, ownerAccountId
        );

        SetTopicAttributesRequest setTopicAttributesRequest = new SetTopicAttributesRequest(topicArn, "Policy", policy);
        snsClient.setTopicAttributes(setTopicAttributesRequest);
        return topicArn;
    }

    @Override
    public void createSub() {
        Topics.subscribeQueue(snsClient, sqsClient, mainObject.getSnsTopicArn(), mainObject.getSqsQueueURL());
    }
    
}
