package com.khush.videotranscoder.Services;

import java.io.IOException;

public interface LambdaService {
    public String createLambdaFunction() throws IOException;
    public void createRoleAndAttachPolicy();
    public void deleteMapping();
}
