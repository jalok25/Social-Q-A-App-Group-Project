package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, final String authorizationToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorizationToken);

        // Validate if user is signed in or not
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Validate if user has signed out
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        questionEntity.setUser(userAuthEntity.getUser());
        return questionDao.createQuestion(questionEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(final String authorization) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorization);

        // Validate if user is signed in or not
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Validate if user has signed out
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }

        return questionDao.getAllQuestions();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(String questionUuid, String authorization, String editedQuestion)
            throws AuthorizationFailedException, InvalidQuestionException {

        /*get the user auth Entity and check if the user is signed in and
        has not logged out at the time of function call*/
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorization);
        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        else if(userAuthEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }

        /*Check if the question to be edited Exists in the database
        If the question doesn't exist, throw the InvalidQuestionException*/
        boolean questionExists = questionDao.checkQuestionExistence(questionUuid);
        if(questionExists == false) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        /*Check if the User editing the Question is the owner or not
        If the user is not the owner. throw AuthorizationFailedException*/
        boolean isOwner = questionDao.isOwner(questionUuid ,userAuthEntity.getUser());
        if(isOwner == false) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        //Edit the Question and return the edited Question Entity
        return questionDao.editQuestion(questionUuid, editedQuestion);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(String questionUuid, String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        /*get the user auth Entity and check if the user is signed in and
        has not logged out at the time of function call*/
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorization);
        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        else if(userAuthEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }

        //If the question doesn't exist, throw the InvalidQuestionException
        boolean questionExists = questionDao.checkQuestionExistence(questionUuid);
        if(questionExists == false) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        /*check if the User is either the owner of the post or
        The Admin. If the user id neither of them then throw the AuthorizationFailedException*/
        boolean isOwner = questionDao.isOwner(questionUuid ,userAuthEntity.getUser());
        boolean isAdmin = userAuthEntity.getUser().getRole().equals("admin");
        if(!(isOwner || isAdmin)) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

        //Delete the Question and return the deleted Question Entity
        return questionDao.deleteQuestion(questionUuid);
    }

    public List<QuestionEntity> getAllQuestionsByUser(final String authorization, final String uuid) throws AuthorizationFailedException, UserNotFoundException {
        //Add the business logic to get all the question of particular user
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorization);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002",
                    "User is signed out.Sign in first to get all questions posted by a specific user");
        }
        UserEntity user = userDao.getUserByUuid(uuid);
        if (user == null) {
            throw new UserNotFoundException(
                    "USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return questionDao.getAllQuestionsByUser(user);

    }
}
