package com.khush.videotranscoder.Controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khush.videotranscoder.Services.LambdaService;

@RestController
@RequestMapping("/api/lambda")
public class LambdaController {
    
    @Autowired
    private LambdaService lambdaService;

    @PostMapping("/create/function")
    public ResponseEntity<String> createLambdaFunction() throws IOException {
        String res = lambdaService.createLambdaFunction();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/create/role-and-attach-policy")
    public ResponseEntity<String> createRoleAndAttachPolicy() {
        lambdaService.createRoleAndAttachPolicy();
        return new ResponseEntity<>("Role created and policy attached", HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/mapping")
    public ResponseEntity<String> deleteMapping() {
        lambdaService.deleteMapping();
        return new ResponseEntity<>("Mappings deleted", HttpStatus.OK);
    }

}
