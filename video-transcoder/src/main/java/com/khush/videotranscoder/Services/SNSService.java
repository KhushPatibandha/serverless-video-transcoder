package com.khush.videotranscoder.Services;

public interface SNSService {
    public String createSNSTopic(String topicName, String bucketArn);
    public void createSub(String snsTopicArn, String sqsQueueUrl);
}
