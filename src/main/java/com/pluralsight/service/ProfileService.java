package org.yearup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.yearup.data.ProfileDao;
import org.yearup.models.Profile;

@Service
public class ProfileService {
    private ProfileDao profileDao;

    @Autowired
    public ProfileService(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    public Profile create(Profile profile){
        return profileDao.create(profile);
    }
    public Profile getByUserId(Integer userId){
        return profileDao.getByUserId(userId);
    }
    public Profile updateProfile(Integer userId, Profile profile) {return profileDao.updateProfile(userId, profile);}
}
