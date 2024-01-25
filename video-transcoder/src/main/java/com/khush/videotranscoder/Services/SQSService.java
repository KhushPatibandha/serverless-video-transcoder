package com.khush.videotranscoder.Services;

public interface SQSService {
    public String createQueue();
    public String logAllMessages();
}
