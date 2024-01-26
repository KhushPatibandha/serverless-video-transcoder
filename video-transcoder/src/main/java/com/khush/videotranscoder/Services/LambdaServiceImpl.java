package com.khush.videotranscoder.Services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.model.AttachRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.CreateRoleRequest;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.CreateEventSourceMappingRequest;
import com.amazonaws.services.lambda.model.CreateFunctionRequest;
import com.amazonaws.services.lambda.model.CreateFunctionResult;
import com.amazonaws.services.lambda.model.DeleteEventSourceMappingRequest;
import com.amazonaws.services.lambda.model.EventSourceMappingConfiguration;
import com.amazonaws.services.lambda.model.FunctionCode;
import com.amazonaws.services.lambda.model.ListEventSourceMappingsResult;
import com.amazonaws.services.lambda.model.Runtime;
import com.khush.videotranscoder.AwsClientProvider;
import com.khush.videotranscoder.MainObject;

@Service
public class LambdaServiceImpl implements LambdaService {

    @Autowired
    private MainObject mainObject;

    private static final AWSLambda lambdaClient = AwsClientProvider.getLambdaclient();
    private static final AmazonIdentityManagement iamClient = AwsClientProvider.getIamclient();

    @Override
    public String createLambdaFunction() throws IOException {
        String filePath = mainObject.getJarFilePath();
        ByteBuffer fileToUpload;
        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            FileChannel channel = fis.getChannel();
            fileToUpload = ByteBuffer.allocate((int) channel.size());
            channel.read(fileToUpload);
            fileToUpload.flip();
        }

        FunctionCode code = new FunctionCode().withZipFile(fileToUpload);

        CreateFunctionRequest request = new CreateFunctionRequest()
                                            .withFunctionName(mainObject.getLambdaFunctionName())
                                            .withCode(code)
                                            .withRole(mainObject.getRoleArn())
                                            .withHandler(mainObject.getLambdaHandler())
                                            .withRuntime(Runtime.Java17)
                                            .withTimeout(30);

        CreateFunctionResult result = lambdaClient.createFunction(request);
        String resultString = result.toString();

        CreateEventSourceMappingRequest eventSourceMappingRequest = new CreateEventSourceMappingRequest()
                                                .withEventSourceArn(mainObject.getSqsQueueArn())
                                                .withFunctionName(mainObject.getLambdaFunctionName())
                                                .withEnabled(true)
                                                .withBatchSize(3)
                                                .withMaximumBatchingWindowInSeconds(60);

        lambdaClient.createEventSourceMapping(eventSourceMappingRequest);
        return resultString;
    }

    @Override
    public String createLambdaFunctionToTriggerFromPermS3() throws IOException {
        String filePath = mainObject.getJarFilePath();
        ByteBuffer fileToUpload;
        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            FileChannel channel = fis.getChannel();
            fileToUpload = ByteBuffer.allocate((int) channel.size());
            channel.read(fileToUpload);
            fileToUpload.flip();
        }

        FunctionCode code = new FunctionCode().withZipFile(fileToUpload);

        CreateFunctionRequest request = new CreateFunctionRequest()
                                            .withFunctionName(mainObject.getLambdaS3FunctionName())
                                            .withCode(code)
                                            .withRole(mainObject.getRoleNameForPermS3LambdaTriggerArn())
                                            .withHandler(mainObject.getLambdaS3Handler())
                                            .withRuntime(Runtime.Java17)
                                            .withTimeout(30);

        CreateFunctionResult result = lambdaClient.createFunction(request);
        String resulString = result.toString();

        CreateEventSourceMappingRequest eventSourceMappingRequest = new CreateEventSourceMappingRequest()
                                            .withEventSourceArn(mainObject.getSqsQueueArnForPermS3())
                                            .withFunctionName(mainObject.getLambdaS3FunctionName())
                                            .withEnabled(true)
                                            .withBatchSize(8)
                                            .withMaximumBatchingWindowInSeconds(60);

        lambdaClient.createEventSourceMapping(eventSourceMappingRequest);
        return resulString;
    }

    @Override
    public void createRoleAndAttachPolicy() {
        String trustPolicy = "{\n" + //
                "    \"Version\": \"2012-10-17\",\n" + //
                "    \"Statement\": [\n" + //
                "        {\n" + //
                "            \"Effect\": \"Allow\",\n" + //
                "            \"Action\": [\n" + //
                "                \"sts:AssumeRole\"\n" + //
                "            ],\n" + //
                "            \"Principal\": {\n" + //
                "                \"Service\": [\n" + //
                "                    \"lambda.amazonaws.com\"\n" + //
                "                ]\n" + //
                "            }\n" + //
                "        }\n" + //
                "    ]\n" + //
                "}";

        CreateRoleRequest request = new CreateRoleRequest()
                                        .withRoleName(mainObject.getRoleName())
                                        .withAssumeRolePolicyDocument(trustPolicy);

        iamClient.createRole(request);

        AttachRolePolicyRequest policyRequest = new AttachRolePolicyRequest()
                                                    .withRoleName(mainObject.getRoleName())
                                                    .withPolicyArn("arn:aws:iam::aws:policy/service-role/AWSLambdaSQSQueueExecutionRole");

        iamClient.attachRolePolicy(policyRequest);
    }

    @Override
    public void createRoleAndAttachPolicyForS3() {
        String trustPolicy = "{\n" + //
                "    \"Version\": \"2012-10-17\",\n" + //
                "    \"Statement\": [\n" + //
                "        {\n" + //
                "            \"Effect\": \"Allow\",\n" + //
                "            \"Action\": [\n" + //
                "                \"sts:AssumeRole\"\n" + //
                "            ],\n" + //
                "            \"Principal\": {\n" + //
                "                \"Service\": [\n" + //
                "                    \"lambda.amazonaws.com\"\n" + //
                "                ]\n" + //
                "            }\n" + //
                "        }\n" + //
                "    ]\n" + //
                "}";

        CreateRoleRequest request = new CreateRoleRequest()
                                        .withRoleName(mainObject.getRoleNameForPermS3LambdaTrigger())
                                        .withAssumeRolePolicyDocument(trustPolicy);
        iamClient.createRole(request);

        AttachRolePolicyRequest policyRequest1 = new AttachRolePolicyRequest()
                                                    .withRoleName(mainObject.getRoleNameForPermS3LambdaTrigger())
                                                    .withPolicyArn("arn:aws:iam::aws:policy/service-role/AWSLambdaSQSQueueExecutionRole");

        iamClient.attachRolePolicy(policyRequest1);

        AttachRolePolicyRequest policyRequest2 = new AttachRolePolicyRequest()
                                                .withRoleName(mainObject.getRoleNameForPermS3LambdaTrigger())
                                                .withPolicyArn("arn:aws:iam::aws:policy/AmazonSESFullAccess");
        iamClient.attachRolePolicy(policyRequest2);
    }

    @Override
    public void deleteMapping() {
        ListEventSourceMappingsResult listResult = lambdaClient.listEventSourceMappings();

        for (EventSourceMappingConfiguration mapping : listResult.getEventSourceMappings()) {
            DeleteEventSourceMappingRequest deleteRequest = new DeleteEventSourceMappingRequest()
                                                                .withUUID(mapping.getUUID());
            lambdaClient.deleteEventSourceMapping(deleteRequest);
        }
    }
    
}
