package com.khush.videotranscoder.Services;

import java.util.List;

public interface ECSService {
    public void createTaskDefination();
    public void createCluster();
    public List<String> getDefaultSubnets();
    public String createSecurityGroup(String securityGroupName);
    public String getSecurityGroupId(String securityGroupName);
    public void createRoleAndAttachPolicy();
}
