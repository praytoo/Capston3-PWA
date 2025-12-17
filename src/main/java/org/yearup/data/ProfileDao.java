package org.yearup.data;


import org.springframework.stereotype.Component;
import org.yearup.models.Profile;

@Component
public interface ProfileDao
{
    //methods to be overridden in ProfileDaoImpl
    Profile create(Profile profile);
    Profile getByUserId(Integer userId);
    Profile updateProfile(Integer userId, Profile profile);
}
