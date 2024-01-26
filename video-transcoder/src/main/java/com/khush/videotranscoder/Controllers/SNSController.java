package com.khush.videotranscoder.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khush.videotranscoder.MainObject;
import com.khush.videotranscoder.Services.SNSService;

@RestController
@RequestMapping("/api/sns")
public class SNSController {

    @Autowired
    private SNSService snsService;

    @Autowired
    private MainObject mainObject;

    @PostMapping("/create/topic/temp-s3")
    public ResponseEntity<String> createSNSTopicForTempBucket() {
        String topicName = mainObject.getSnsTopicName();
        String bucketArn = mainObject.getTempBucketArn();
        
        String response = snsService.createSNSTopic(topicName, bucketArn);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/create/topic/perm-s3")
    public ResponseEntity<String> createSNSTopicForPermBucket() {
        String topicName = mainObject.getSnsTopicForPermS3Name();
        String bucketArn = mainObject.getPermBucketArn();

        String response = snsService.createSNSTopic(topicName, bucketArn);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/create/sub/temp-s3")
    public ResponseEntity<String> createSubForTemp() {
        String snsTopicArn = mainObject.getSnsTopicArn();
        String sqsQueueUrl = mainObject.getSqsQueueURL();

        snsService.createSub(snsTopicArn, sqsQueueUrl);
        return new ResponseEntity<>("Subscription created", HttpStatus.CREATED);
    }

    @PostMapping("/create/sub/perm-s3")
    public ResponseEntity<String> createSubForPerm() {
        String snsTopicArn = mainObject.getSnsTopicForPermS3Arn();
        String sqsQueueUrl = mainObject.getSqsQueueUrlForPermS3();

        snsService.createSub(snsTopicArn, sqsQueueUrl);
        return new ResponseEntity<>("Subscription created", HttpStatus.CREATED);
    }
}
