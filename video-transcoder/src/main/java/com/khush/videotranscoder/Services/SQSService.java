package com.khush.videotranscoder.Services;

public interface SQSService {
    public String createQueue(String sqsQueueName);
    public String logAllMessages();
}
