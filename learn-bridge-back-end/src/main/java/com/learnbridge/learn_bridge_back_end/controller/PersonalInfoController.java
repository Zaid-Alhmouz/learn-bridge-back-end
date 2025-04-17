package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dto.PersonalInfoDTO;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.PersonalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personal-info")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonalInfoController {

    @Autowired
    PersonalInfoService personalInfoService;


    @PutMapping("/edit-info")
    public ResponseEntity<?> editPersonalInfo(@AuthenticationPrincipal SecurityUser loggedUser, @RequestBody PersonalInfoDTO editPersonalInfoRequest) {

        try{
            personalInfoService.editPersonalInfo(editPersonalInfoRequest, loggedUser);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
