package com.pluralsight.data;


import org.springframework.stereotype.Component;
import com.pluralsight.models.Profile;

@Component
public interface ProfileDao
{
    Profile create(Profile profile);
    Profile getByUserId(Integer userId);
    Profile updateProfile(Integer userId, Profile profile);
}
