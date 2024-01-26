package com.khush.videotranscoder.Services;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3Event;
import com.amazonaws.services.s3.model.SetBucketNotificationConfigurationRequest;
import com.amazonaws.services.s3.model.TopicConfiguration;
import com.khush.videotranscoder.AwsClientProvider;
import com.khush.videotranscoder.MainObject;

@Service
public class S3ServiceImpl implements S3Service {

    @Autowired
    private MainObject mainObject;

    private static final AmazonS3 s3Client = AwsClientProvider.getS3client();
    private static String customName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    private static String objectKeyForPut = "upload/user-upload/" + customName + ".mp4";

    @Override
    public void createTempBucket() {
        try {
            if(!s3Client.doesBucketExistV2(mainObject.getTempBucketName())) {
                s3Client.createBucket(new CreateBucketRequest(mainObject.getTempBucketName()));

                BucketNotificationConfiguration notificationConfiguration = new BucketNotificationConfiguration();

                TopicConfiguration topicConfiguration = new TopicConfiguration(mainObject.getSnsTopicArn(), S3Event.ObjectCreated.toString());

                notificationConfiguration.addConfiguration("ObjectCreatedEvents", topicConfiguration);

                SetBucketNotificationConfigurationRequest setBucketNotificationConfigurationRequest = new SetBucketNotificationConfigurationRequest(mainObject.getTempBucketName(), notificationConfiguration);

                s3Client.setBucketNotificationConfiguration(setBucketNotificationConfigurationRequest);
            }
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public URL putPreSignedUrlGeneratorForTempBucket() {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(mainObject.getTempBucketName(), objectKeyForPut).withMethod(HttpMethod.PUT);
        URL url = s3Client.generatePresignedUrl(request);
        
        return url;
    }

    @Override
    public URL getPreSignedUrlGeneratorForAnyBucket(String bucketName, String objectKeyForGet) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKeyForGet).withMethod(HttpMethod.GET);
        URL url = s3Client.generatePresignedUrl(request);
        
        return url;
    }

    @Override
    public String deleteObjectForTempBucket(String objectKeyForDelete) {
        s3Client.deleteObject(mainObject.getTempBucketName(), objectKeyForDelete);
        return "Object deleted successfully";
    }

    @Override
    public void createPermBucket() {
        try {
            if(!s3Client.doesBucketExistV2(mainObject.getPermBucketName())) {
                s3Client.createBucket(new CreateBucketRequest(mainObject.getPermBucketName()));

                BucketNotificationConfiguration notificationConfiguration = new BucketNotificationConfiguration();

                TopicConfiguration topicConfiguration = new TopicConfiguration(mainObject.getSnsTopicForPermS3Arn(), S3Event.ObjectCreated.toString());

                notificationConfiguration.addConfiguration("ObjectCreatedEvents", topicConfiguration);

                SetBucketNotificationConfigurationRequest setBucketNotificationConfigurationRequest = new SetBucketNotificationConfigurationRequest(mainObject.getPermBucketName(), notificationConfiguration);

                s3Client.setBucketNotificationConfiguration(setBucketNotificationConfigurationRequest);

            }
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }
    
}
