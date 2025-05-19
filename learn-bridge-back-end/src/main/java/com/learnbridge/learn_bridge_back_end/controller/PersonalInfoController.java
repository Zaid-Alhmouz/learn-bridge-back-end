package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dto.PersonalInfoDTO;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.PersonalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/personal-info")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonalInfoController {

    @Autowired
    private PersonalInfoService personalInfoService;


    @GetMapping("/get-personal-info")
    public ResponseEntity<PersonalInfoDTO> getPersonalInfo(
            @AuthenticationPrincipal SecurityUser loggedUser
    ) {
        PersonalInfoDTO dto = personalInfoService.getPersonalInfo(loggedUser.getUser().getId());
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }


    @PutMapping("/edit-info")
    public ResponseEntity<?> editPersonalInfo(
            @AuthenticationPrincipal SecurityUser loggedUser,
            @RequestBody PersonalInfoDTO dto
    ) {
        try {
            personalInfoService.editPersonalInfo(dto, loggedUser);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", ex.getMessage()));
        }
    }


    @PostMapping(
            value = "/upload-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, String>> uploadImage(
            @AuthenticationPrincipal SecurityUser loggedUser,
            @RequestPart("image") MultipartFile imageFile
    ) {
        if (imageFile == null || imageFile.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Collections.singletonMap("error", "No file uploaded"));
        }

        String base64Uri = personalInfoService
                .storeProfileImage(loggedUser.getUser().getId(), imageFile);
        return ResponseEntity.ok(
                Collections.singletonMap("imageUrl", base64Uri)
        );
    }
}
