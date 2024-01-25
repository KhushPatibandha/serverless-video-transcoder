package com.khush.videotranscoder.Controllers;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khush.videotranscoder.Services.S3Service;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {
    @Autowired
    private S3Service s3Service;

    @PostMapping("/create/temp-bucket")
    public ResponseEntity<String> createTempBucket() {
        s3Service.createTempBucket();
        return new ResponseEntity<>("Temporary  bucket created", HttpStatus.CREATED);
    }

    @PostMapping("/create/perm-bucket")
    public ResponseEntity<String> createPermBucket() {
        s3Service.createPermBucket();
        return new ResponseEntity<>("Permanent bucket created", HttpStatus.CREATED);
    }

    @GetMapping("/get/put-url")
    public ResponseEntity<String> putPreSignedUrl() {
        URL url = s3Service.putPreSignedUrlGeneratorForTempBucket();
        String urlStr = url.toString();
        return new ResponseEntity<>(urlStr, HttpStatus.OK);
    }

    @GetMapping("/get/get-url/{bucketName}/{objectKey}")
    public ResponseEntity<String> getPreSignedUrl(@PathVariable("bucketName") String bucketName, @PathVariable("objectKey") String objectKey) {
        URL url = s3Service.getPreSignedUrlGeneratorForAnyBucket(bucketName, objectKey);
        String urlStr = url.toString();
        return new ResponseEntity<>(urlStr, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{objectKey}")
    public ResponseEntity<String> deleteObject(@PathVariable("objectKey") String objectKey) {
        String message = s3Service.deleteObjectForTempBucket(objectKey);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
