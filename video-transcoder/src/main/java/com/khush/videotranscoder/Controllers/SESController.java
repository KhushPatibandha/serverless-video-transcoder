package com.khush.videotranscoder.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khush.videotranscoder.Services.SESService;

@RestController
@RequestMapping("/api/ses")
public class SESController {
    
    @Autowired
    private SESService sesService;

    @PostMapping("/create-identity")
    public ResponseEntity<String> createIdentity() {
        sesService.createIdentity();
        return new ResponseEntity<>("Check Email to verify identity", HttpStatus.OK);
    }
}
