package com.upgrad.quora.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "question")
@NamedQueries({

        @NamedQuery(name = "getQuestionByQuestionUuid", query = "select q from QuestionEntity q where q.uuid = :questionUuid "),

        @NamedQuery(name = "getAllQuestions", query = "select q from QuestionEntity q"),

        @NamedQuery(name = "getAllQuestionsByUser",query = "select q from QuestionEntity q where q.user= :userEntity"),

        @NamedQuery(name = "getQuestionByQuestionID", query = "select q from QuestionEntity q where q.id = :questionId")

})

public class QuestionEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    @Column(name = "content")
    @Size(max = 500)
    @NotNull
    private String content;

    @Column(name = "date")
    @NotNull
    private ZonedDateTime date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}