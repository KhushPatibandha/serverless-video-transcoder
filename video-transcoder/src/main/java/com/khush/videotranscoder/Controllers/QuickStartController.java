package com.khush.videotranscoder.Controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khush.videotranscoder.MainObject;
import com.khush.videotranscoder.Services.ECSService;
import com.khush.videotranscoder.Services.LambdaService;
import com.khush.videotranscoder.Services.S3Service;
import com.khush.videotranscoder.Services.SNSService;
import com.khush.videotranscoder.Services.SQSService;

@RestController
@RequestMapping("/api/quickstart")
public class QuickStartController {
    
    @Autowired
    private MainObject mainObject;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private SNSService snsService;

    @Autowired
    private SQSService sqsService;

    @Autowired
    private ECSService ecsService;

    @Autowired
    private LambdaService lambdaService;

    @PostMapping("/create-resources")
    public ResponseEntity<String> createResources() throws IOException {

        /*
         * 1) Create Queue for temp bucket
         * 2) Create Queue for perm bucket 
         */
        String sqsQueueNameForTempBucket = mainObject.getSqsQueueName();
        sqsService.createQueue(sqsQueueNameForTempBucket);

        String sqsQueueNameForPermBucket = mainObject.getSqsQueueForPermS3Name();
        sqsService.createQueue(sqsQueueNameForPermBucket);


        /*
         * 3) Create Topic for temp bucket
         * 4) Create Sub for temp bucket
         * 5) Create Topic for perm bucket
         * 6) Create Sub for perm bucket
         */
        String topicNameForTempBucket = mainObject.getSnsTopicName();
        String topicArnForTempBucket = mainObject.getSnsTopicArn();
        String tempBucketArn = mainObject.getTempBucketArn();
        String sqsQueueUrlForTempBucket = mainObject.getSqsQueueURL();
        snsService.createSNSTopic(topicNameForTempBucket, tempBucketArn);
        snsService.createSub(topicArnForTempBucket, sqsQueueUrlForTempBucket);

        String topicNameForPermBucket = mainObject.getSnsTopicForPermS3Name();
        String topicArnForPermBucket = mainObject.getSnsTopicForPermS3Arn();
        String permBucketArn = mainObject.getPermBucketArn();
        String sqsQueueUrlForPermBucket = mainObject.getSqsQueueUrlForPermS3();
        snsService.createSNSTopic(topicNameForPermBucket, permBucketArn);
        snsService.createSub(topicArnForPermBucket, sqsQueueUrlForPermBucket);


        /*
         * 7) Create temp bucket
         * 8) Create perm bucket
         */
        s3Service.createTempBucket();

        s3Service.createPermBucket();


        /*
         * 9) Create role for the task defination to use
         * 10) Create Cluster
         * 11) Create Task defination
         */
        ecsService.createRoleAndAttachPolicy();
        ecsService.createCluster();
        ecsService.createTaskDefination();


        /*
         * 12) Delete all previous mappings
         * 13) Create role for the sqs lambda trigger function
         * 14) Create role fpr the s3 lambda trigger function
         * 15) Create Lambda function for sqs trigger
         * 16) Create Lambda function for s3 trigger
         */
        lambdaService.deleteMapping();
        lambdaService.createRoleAndAttachPolicy();
        lambdaService.createRoleAndAttachPolicyForS3();
        lambdaService.createLambdaFunction();
        lambdaService.createLambdaFunctionToTriggerFromPermS3();

        return new ResponseEntity<>("Resources are being created, it might take some time to set everything up.", HttpStatus.CREATED);

    }
    
}
