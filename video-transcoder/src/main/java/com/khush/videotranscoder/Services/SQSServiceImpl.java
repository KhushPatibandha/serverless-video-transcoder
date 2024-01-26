package com.khush.videotranscoder.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueAttributeName;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.khush.videotranscoder.AwsClientProvider;
import com.khush.videotranscoder.MainObject;

@Service
public class SQSServiceImpl implements SQSService {

    @Autowired
    private MainObject mainObject;

    private static final AmazonSQS sqsClient = AwsClientProvider.getSqsclient();

    @Override
    public String createQueue(String sqsQueueName) {
        CreateQueueRequest request = new CreateQueueRequest(sqsQueueName);
        CreateQueueResult result = sqsClient.createQueue(request);
        String sqsUrl = result.getQueueUrl();

        GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest(sqsUrl).withAttributeNames(QueueAttributeName.QueueArn);
        
        GetQueueAttributesResult getQueueAttributesResult = sqsClient.getQueueAttributes(getQueueAttributesRequest);
    
        String sqsArn = getQueueAttributesResult.getAttributes().get(QueueAttributeName.QueueArn.toString());

        return "URL: " + sqsUrl + ", ARN: " + sqsArn;
    }

    @Override
    public String logAllMessages() {
        String logs = "";
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(mainObject.getSqsQueueURL())
                        .withMaxNumberOfMessages(1)
                        .withWaitTimeSeconds(0);

        while(true) {
            for(Message message : sqsClient.receiveMessage(receiveMessageRequest).getMessages()) {
                System.out.println(message.getBody());
                logs += message.getBody() + "\n";
            }
            return logs;
        }
    }
    
}
