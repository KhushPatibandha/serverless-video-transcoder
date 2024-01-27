package com.khush.videotranscoder.Controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khush.videotranscoder.MainObject;
import com.khush.videotranscoder.Services.LambdaService;

@RestController
@RequestMapping("/api/lambda")
public class LambdaController {

    @Autowired
    private MainObject mainObject;
    
    @Autowired
    private LambdaService lambdaService;

    @PostMapping("/create/function/temp")
    public ResponseEntity<String> createLambdaFunction() throws IOException {
        String res = lambdaService.createLambdaFunction();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/create/function/perm")
    public ResponseEntity<String> createLambdaS3Function() throws IOException {
        String res = lambdaService.createLambdaFunctionToTriggerFromPermS3();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/create/role-and-attach-policy/sqs")
    public ResponseEntity<String> createRoleAndAttachPolicy() {
        lambdaService.createRoleAndAttachPolicy();
        return new ResponseEntity<>("Role created and policy attached for sqs tasks", HttpStatus.CREATED);
    }

    @PostMapping("/create/role-and-attach-policy/s3")
    public ResponseEntity<String> createRoleAndAttachPolicyForS3() {
        lambdaService.createRoleAndAttachPolicyForS3();
        return new ResponseEntity<>("Role created and policy attached for s3 tasks", HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/mapping")
    public ResponseEntity<String> deleteMapping() {
        lambdaService.deleteMapping();
        return new ResponseEntity<>("Mappings deleted", HttpStatus.OK);
    }

    @PutMapping("/update/code")
    public ResponseEntity<String> updateLambdaFunctionCode() throws IOException {
        String firstLambdaFunction = mainObject.getLambdaFunctionName();
        String secondLambdaFunction = mainObject.getLambdaS3FunctionName();
        lambdaService.updateLambdaFunctionCode(firstLambdaFunction);
        lambdaService.updateLambdaFunctionCode(secondLambdaFunction);
        return new ResponseEntity<>("All lambda function's code is updated now!", HttpStatus.OK);
    }

}
