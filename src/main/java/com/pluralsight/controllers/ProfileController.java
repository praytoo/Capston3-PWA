package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yearup.models.Profile;
import org.yearup.models.User;
import org.yearup.service.ProfileService;
import org.yearup.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("profile")
@CrossOrigin
public class ProfileController {
    private ProfileService profileService;
    private UserService userService;

    @Autowired
    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<Profile> getByUserId(@PathVariable Integer userId){
        Profile profile = profileService.getByUserId(userId);
        if (profile == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    public ResponseEntity<Profile> getProfile(Principal principal) {

        String username = principal.getName();
        User user = userService.getByUserName(username);

        Profile profile = profileService.getByUserId(user.getId());

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(profile);
    }


    @PutMapping("{userId}")
    public void updateProfile(@PathVariable Integer userId, @RequestBody Profile profile){
        profileService.updateProfile(userId, profile);
    }

    @PutMapping
    public ResponseEntity<Profile> updateProfile(Principal principal, @RequestBody Profile profile) {
        String username = principal.getName();
        User user = userService.getByUserName(username);
        Profile updated = profileService.updateProfile(user.getId(), profile);
        return ResponseEntity.ok(updated);
    }

}
