package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AdminDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AdminDao adminDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(String userID, String authorization) throws UserNotFoundException,
            AuthorizationFailedException{

        //get the user auth Entity and check if the user is signed in and
        //has not logged out at the time of function call
        UserAuthEntity userAuthEntity = adminDao.getAccessToken(authorization);

        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        else if(userAuthEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }

        //check if the user is the admin
        boolean isAdmin = userAuthEntity.getUser().getRole().equals("admin");

        //If the signed in user is admin, Delete the User else throw the
        //Unauthorized User exception
        if(isAdmin) {
            UserEntity userEntity = adminDao.deleteUser(userID);
            if(userEntity != null) {
                return userEntity;
            }
            else {
                throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
            }
        }
        else {
            throw  new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }
    }

}
