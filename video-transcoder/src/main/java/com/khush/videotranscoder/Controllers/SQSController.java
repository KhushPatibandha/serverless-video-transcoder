package com.khush.videotranscoder.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khush.videotranscoder.Services.SQSService;

@RestController
@RequestMapping("/api/sqs")
public class SQSController {

    @Autowired
    private SQSService sqsService;
    
    @PostMapping("/create/queue")
    public ResponseEntity<String> createQueue() {
        String response = sqsService.createQueue();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/get/log/messages")
    public ResponseEntity<String> logAllMessages() {
        String response = sqsService.logAllMessages();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
