package com.khush.videotranscoder.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khush.videotranscoder.Services.ECSService;

@RestController
@RequestMapping("/api/ecs")
public class ECSController {
    
    @Autowired
    private ECSService ecsService;

    @PostMapping("/create/task-defination")
    public ResponseEntity<String> createTaskDefination() {
        ecsService.createTaskDefination();
        return new ResponseEntity<>("Task defination created", HttpStatus.CREATED);
    }

    @PostMapping("/create/cluster")
    public ResponseEntity<String> createCluster() {
        ecsService.createCluster();
        return new ResponseEntity<>("Cluster created", HttpStatus.CREATED);
    }

    @GetMapping("/get/default-subnets")
    public ResponseEntity<List<String>> getDefaultSubnets() {
        List<String> subnets = ecsService.getDefaultSubnets();
        return new ResponseEntity<>(subnets, HttpStatus.OK);
    }

    @PostMapping("/create/security-group/{securityGroupName}")
    public ResponseEntity<String> createSecurityGroup(@PathVariable String securityGroupName) {
        String response = ecsService.createSecurityGroup(securityGroupName);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/get/security-group/{securityGroupName}")
    public ResponseEntity<String> getSecurityGroupId(@PathVariable String securityGroupName) {
        String response = ecsService.getSecurityGroupId(securityGroupName);
        if (response == null) {
            response = "Security group not created";
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/create/role-and-attach-policy")
    public ResponseEntity<String> createRoleAndAttachPolicy() {
        ecsService.createRoleAndAttachPolicy();
        return new ResponseEntity<>("Role and policy created and attached", HttpStatus.CREATED);
    }
}
