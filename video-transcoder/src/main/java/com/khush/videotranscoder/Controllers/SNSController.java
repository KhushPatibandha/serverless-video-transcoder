package com.khush.videotranscoder.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khush.videotranscoder.Services.SNSService;

@RestController
@RequestMapping("/api/sns")
public class SNSController {

    @Autowired
    private SNSService snsService;

    @PostMapping("/create/topic")
    public ResponseEntity<String> createSNSTopic() {
        String response = snsService.createSNSTopic();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/create/sub")
    public ResponseEntity<String> createSub() {
        snsService.createSub();
        return new ResponseEntity<>("Subscription created", HttpStatus.CREATED);
    }
}
