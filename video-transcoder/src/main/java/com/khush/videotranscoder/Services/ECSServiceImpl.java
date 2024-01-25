package com.khush.videotranscoder.Services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.AmazonEC2Exception;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.IpRange;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.model.CreateClusterRequest;
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionRequest;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.model.AttachRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.CreateRoleRequest;
import com.amazonaws.util.json.Jackson;
import com.khush.videotranscoder.AwsClientProvider;
import com.khush.videotranscoder.MainObject;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class ECSServiceImpl implements ECSService {

    @Autowired
    private MainObject mainObject;

    private static final Dotenv dotenv = Dotenv.load();
    private static final Logger log = LoggerFactory.getLogger(ECSServiceImpl.class);
    private static final String region = dotenv.get("AWS_REGION");
    private static final AmazonECS ecsClient = AwsClientProvider.getEcsclient();
    private static final AmazonEC2 ec2Client = AwsClientProvider.getEc2client();
    private static final AmazonIdentityManagement iamClient = AwsClientProvider.getIamclient();

    @Override
    public void createTaskDefination() {
        String taskDefinitionName = mainObject.getTaskDefinationName();
        String containerName = mainObject.getContainerName();
        String imageLink = mainObject.getDockerImageLink();
        String roleArn = mainObject.getRoleArnTaskExecution();

        String taskDefinitionJson = "{\n" + //
                "    \"family\": \"" + taskDefinitionName + "\",\n" + //
                "    \"containerDefinitions\": [\n" + //
                "        {\n" + //
                "            \"name\": \"" + containerName + "\",\n" + //
                "            \"image\": \"" + imageLink + "\",\n" + //
                "            \"cpu\": 1024,\n" + //
                "            \"memory\": 2048,\n" + //
                "            \"portMappings\": [\n" + //
                "                {\n" + //
                "                    \"name\": \""+ containerName +"-80-tcp\",\n" + //
                "                    \"containerPort\": 80,\n" + //
                "                    \"hostPort\": 80,\n" + //
                "                    \"protocol\": \"tcp\",\n" + //
                "                    \"appProtocol\": \"http\"\n" + //
                "                },\n" + //
                "                {\n" + //
                "                    \"name\": \""+ containerName +"-8080-tcp\",\n" + //
                "                    \"containerPort\": 8080,\n" + //
                "                    \"hostPort\": 8080,\n" + //
                "                    \"protocol\": \"tcp\",\n" + //
                "                    \"appProtocol\": \"http\"\n" + //
                "                }\n" + //
                "            ],\n" + //
                "            \"essential\": true,\n" + //
                "            \"environment\": [],\n" + //
                "            \"environmentFiles\": [],\n" + //
                "            \"mountPoints\": [],\n" + //
                "            \"volumesFrom\": [],\n" + //
                "            \"ulimits\": [],\n" + //
                "            \"logConfiguration\": {\n" + //
                "                \"logDriver\": \"awslogs\",\n" + //
                "                \"options\": {\n" + //
                "                    \"awslogs-create-group\": \"true\",\n" + //
                "                    \"awslogs-group\": \"/ecs/" + taskDefinitionName + "\",\n" + //
                "                    \"awslogs-region\": \""+ region +"\",\n" + //
                "                    \"awslogs-stream-prefix\": \"ecs\"\n" + //
                "                },\n" + //
                "                \"secretOptions\": []\n" + //
                "            }\n" + //
                "        }\n" + //
                "    ],\n" + //
                "    \"taskRoleArn\": \"" + roleArn +"\",\n" + //
                "    \"executionRoleArn\": \"" + roleArn +"\",\n" + //
                "    \"networkMode\": \"awsvpc\",\n" + //
                "    \"requiresCompatibilities\": [\n" + //
                "        \"FARGATE\"\n" + //
                "    ],\n" + //
                "    \"cpu\": \"1024\",\n" + //
                "    \"memory\": \"2048\",\n" + //
                "    \"runtimePlatform\": {\n" + //
                "        \"cpuArchitecture\": \"X86_64\",\n" + //
                "        \"operatingSystemFamily\": \"LINUX\"\n" + //
                "    }\n" + //
                "}";

        RegisterTaskDefinitionRequest request = Jackson.fromJsonString(taskDefinitionJson, RegisterTaskDefinitionRequest.class);
        ecsClient.registerTaskDefinition(request);
    }

    @Override
    public void createCluster() {
        CreateClusterRequest request = new CreateClusterRequest().withClusterName(mainObject.getClusterName());
        ecsClient.createCluster(request);
    }

    @Override
    public List<String> getDefaultSubnets() {
        DescribeSubnetsRequest request = new DescribeSubnetsRequest().withFilters(new Filter().withName("default-for-az").withValues("true"));
        DescribeSubnetsResult result = ec2Client.describeSubnets(request);

        List<String> subnetIds = new ArrayList<>();
        for(Subnet subnet : result.getSubnets()) {
            subnetIds.add(subnet.getSubnetId());
        }
        return subnetIds;
    }

    @Override
    public String createSecurityGroup(String securityGroupName) {
        try {
            CreateSecurityGroupRequest request = new CreateSecurityGroupRequest().withGroupName(securityGroupName).withDescription("Security Group for video transcoder");
        
            CreateSecurityGroupResult result = ec2Client.createSecurityGroup(request);

            IpPermission allTcpPermission = new IpPermission().withIpProtocol("tcp").withFromPort(0).withToPort(65535).withIpv4Ranges(new IpRange().withCidrIp("0.0.0.0/0"));

            IpPermission allTrafficPermission = new IpPermission()
            .withIpProtocol("-1")
            .withIpv4Ranges(new IpRange().withCidrIp("0.0.0.0/0"));

            AuthorizeSecurityGroupIngressRequest authRequest = new AuthorizeSecurityGroupIngressRequest().withGroupId(result.getGroupId()).withIpPermissions(allTcpPermission, allTrafficPermission);

            try {
                ec2Client.authorizeSecurityGroupIngress(authRequest);
            } catch (AmazonEC2Exception e) {
                if (!"InvalidPermission.Duplicate".equals(e.getErrorCode())) {
                    throw e;
                }
            }

            return result.getGroupId();
        } catch (Exception e) {
            log.error("Error creating security group", e);
            return null;
        }
    }

    @Override
    public String getSecurityGroupId(String securityGroupName) {
        DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest().withFilters(new Filter().withName("group-name").withValues(securityGroupName));

        DescribeSecurityGroupsResult result = ec2Client.describeSecurityGroups(request);

        for(SecurityGroup group : result.getSecurityGroups()) {
            if(group.getGroupName().equals(securityGroupName)) {
                return group.getGroupId();
            }
        }
        return null;
    }

    @Override
    public void createRoleAndAttachPolicy() {
        String trustPolicy = "{\r\n" + //
                "  \"Version\": \"2012-10-17\",\r\n" + //
                "  \"Statement\": [\r\n" + //
                "    {\r\n" + //
                "      \"Sid\": \"\",\r\n" + //
                "      \"Effect\": \"Allow\",\r\n" + //
                "      \"Principal\": {\r\n" + //
                "        \"Service\": \"ecs-tasks.amazonaws.com\"\r\n" + //
                "      },\r\n" + //
                "      \"Action\": \"sts:AssumeRole\"\r\n" + //
                "    }\r\n" + //
                "  ]\r\n" + //
                "}";

        String roleName = mainObject.getRoleNameForTaskExecution();
        CreateRoleRequest request = new CreateRoleRequest().withRoleName(roleName).withAssumeRolePolicyDocument(trustPolicy);
        
        iamClient.createRole(request);
        
        AttachRolePolicyRequest policyRequest = new AttachRolePolicyRequest().withRoleName(roleName).withPolicyArn("arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy");
        iamClient.attachRolePolicy(policyRequest);

        AttachRolePolicyRequest logsPolicyRequest = new AttachRolePolicyRequest()
        .withRoleName(roleName)
        .withPolicyArn("arn:aws:iam::aws:policy/CloudWatchLogsFullAccess");
        iamClient.attachRolePolicy(logsPolicyRequest);
    }
    
}
