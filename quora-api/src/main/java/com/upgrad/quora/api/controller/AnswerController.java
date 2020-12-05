package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/")
@RestController
public class AnswerController {

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    AnswerBusinessService answerBusinessService;

    @RequestMapping(method = RequestMethod.POST, value = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable(value = "questionId") String questionId, @RequestHeader(value = "authorization") String authorization, String answerContent) throws AuthorizationFailedException, InvalidQuestionException {

        final AnswerEntity createdAnswer = answerBusinessService.createAnswer(answerContent, authorization, questionId);
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest, @PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAns(answerEditRequest.getContent());
        answerEntity.setUuid(answerId);

        AnswerEntity updatedAnswerEntity = answerBusinessService.editAnswerContent(answerEntity, authorization);
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(updatedAnswerEntity.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        answerBusinessService.deleteAnswer(answerId, authorization);

        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerId).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }



    @RequestMapping(method = RequestMethod.GET, value = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable(value = "questionId") String questionId, @RequestHeader(value = "authorization") String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        final List<AnswerEntity> answerEntityList = answerBusinessService.getAllAnswersToQuestion(questionId, authorization);
        List<AnswerDetailsResponse> allAnswerToQuestion = new ArrayList<AnswerDetailsResponse>();

        for (AnswerEntity ae : answerEntityList) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse()
                    .id(ae.getUuid())
                    .questionContent(ae.getQuestion().getContent())
                    .answerContent(ae.getAns());
            allAnswerToQuestion.add(answerDetailsResponse);
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(allAnswerToQuestion, HttpStatus.OK);
    }


}
