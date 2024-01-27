package com.khush.videotranscoder.Services;

import java.io.IOException;

public interface LambdaService {
    public String createLambdaFunction() throws IOException;
    public String createLambdaFunctionToTriggerFromPermS3() throws IOException;
    public void createRoleAndAttachPolicy();
    public void createRoleAndAttachPolicyForS3();
    public void deleteMapping();
    public void updateLambdaFunctionCode(String functionName) throws IOException;
}
