package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /*Get the question uuid and Edited Question, fetches the existing question from the database
    and update it with the edited Question*/
    public QuestionEntity editQuestion(String questionUuid, String editedQuestion) {
        try {
            entityManager.getTransaction().begin();
            QuestionEntity questionEntity = entityManager.createNamedQuery("getQuestionByQuestionUuid", QuestionEntity.class).
                    setParameter("questionUuid", questionUuid).getSingleResult();
            questionEntity.setContent(editedQuestion);
            entityManager.getTransaction( ).commit( );
            entityManager.close();;
            return questionEntity;
        }
        catch (NoResultException nre) {
            return null;
        }
    }

    //Gets the question uuid and the UserEntity, check if the user passed as User Entity is the owner of the question.
    public boolean isOwner(String questionUuid, UserEntity userEntity) {
        try {
            boolean found = false;
            List<QuestionEntity> questionEntities = entityManager.createNamedQuery("getAllQuestionsByUser", QuestionEntity.class).
                    setParameter("userEntity", userEntity).getResultList();
            for(QuestionEntity qe : questionEntities) {
                if(qe.getUuid() == questionUuid) {
                    return true;
                }
            }
            return false;
        }
        catch (NoResultException nre) {
            return false;
        }
    }

    //Gets the question uuid and checks if it exists on Database or not
    public boolean checkQuestionExistence(String uuid) {
        try {
            QuestionEntity questionEntity = entityManager.createNamedQuery("getQuestionByQuestionUuid", QuestionEntity.class).
                    setParameter("questionUuid", uuid).getSingleResult();
            return true;
        }
        catch (NoResultException nre) {
            return false;
        }
    }

    //Gets the question uuid, deletes the Question from the database
    public QuestionEntity deleteQuestion(String questionUuid) {
        try {
            QuestionEntity questionEntity = entityManager.createNamedQuery("getQuestionByQuestionUuid", QuestionEntity.class).
                    setParameter("questionUuid", questionUuid).getSingleResult();

            entityManager.remove(questionEntity);
            return questionEntity;
        }
        catch (NoResultException nre) {
            return null;
        }
    }

    public List<QuestionEntity> getAllQuestionsByUser(final UserEntity userEntity)
    {
        //Add the logic to get all the question which are specifc to user
        try {
            return entityManager
                    .createNamedQuery("getAllQuestionsByUser", QuestionEntity.class)
                    .setParameter("userEntity", userEntity)
                    .getResultList();
        }
        catch (NoResultException nre) {
            return null;
        }
    }
}
