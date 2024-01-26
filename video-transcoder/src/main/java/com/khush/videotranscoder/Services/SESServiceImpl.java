package com.khush.videotranscoder.Services;

import org.springframework.stereotype.Service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.VerifyEmailIdentityRequest;
import com.khush.videotranscoder.AwsClientProvider;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class SESServiceImpl implements SESService {

    private static final AmazonSimpleEmailService sesClient = AwsClientProvider.getSesclient();
    private static final Dotenv dotenv = Dotenv.load();
    private static final String emailAddress = dotenv.get("EMAIL_ADDRESS");

    @Override
    public void createIdentity() {
        VerifyEmailIdentityRequest request = new VerifyEmailIdentityRequest().withEmailAddress(emailAddress);
        sesClient.verifyEmailIdentity(request);
    }
    
}
