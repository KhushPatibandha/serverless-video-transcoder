package com.khush.videotranscoder;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import io.github.cdimascio.dotenv.Dotenv;

public class AwsClientProvider {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String userAccessKey = dotenv.get("USER_ACCESS_KEY"); 
    private static final String userSecretAccessKey = dotenv.get("USER_SECRET_ACCESS_KEY");
    private static final String region = dotenv.get("AWS_REGION");
    private static final Regions regions = Regions.fromName(region);
    private static final BasicAWSCredentials creds = new BasicAWSCredentials(userAccessKey, userSecretAccessKey);

    private static final AmazonIdentityManagement iamClient = AmazonIdentityManagementClientBuilder.standard()
                                                                .withCredentials(new AWSStaticCredentialsProvider(creds))
                                                                .withRegion(regions)
                                                                .build();

    private static final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                                                .withCredentials(new AWSStaticCredentialsProvider(creds))
                                                .withRegion(regions)
                                                .build();

    private static final AmazonSQS sqsClient = AmazonSQSClientBuilder.standard()
                                                .withCredentials(new AWSStaticCredentialsProvider(creds))
                                                .withRegion(regions)
                                                .build();

    private static final AmazonSNS snsClient = AmazonSNSClientBuilder.standard()
                                                .withCredentials(new AWSStaticCredentialsProvider(creds))
                                                .withRegion(regions)
                                                .build();

    private static final AWSLambda lambdaClient = AWSLambdaClientBuilder.standard()
                                                    .withCredentials(new AWSStaticCredentialsProvider(creds))
                                                    .withRegion(regions)
                                                    .build();

    private static final AmazonECS ecsClient = AmazonECSClientBuilder.standard()
                                                .withCredentials(new AWSStaticCredentialsProvider(creds))
                                                .withRegion(regions)
                                                .build();

    private static final AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
                                                .withCredentials(new AWSStaticCredentialsProvider(creds))
                                                .withRegion(regions)
                                                .build();

    public static AmazonIdentityManagement getIamclient() {
        return iamClient;
    }

    public static AmazonS3 getS3client() {
        return s3Client;
    }

    public static AmazonSQS getSqsclient() {
        return sqsClient;
    }

    public static AmazonSNS getSnsclient() {
        return snsClient;
    }

    public static AWSLambda getLambdaclient() {
        return lambdaClient;
    }

    public static AmazonECS getEcsclient() {
        return ecsClient;
    }

    public static AmazonEC2 getEc2client() {
        return ec2Client;
    }

}
