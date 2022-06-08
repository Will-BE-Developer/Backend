package com.sparta.willbe.interview.model;

import com.sparta.willbe.likes.model.Likes;
import com.sparta.willbe._global.timestamped.model.Timestamped;
import com.sparta.willbe.comments.model.Comment;
import com.sparta.willbe.question.model.Question;
import com.sparta.willbe.scrap.model.Scrap;
import com.sparta.willbe.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor
@Entity
public class Interview extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String videoKey;

    @Column(nullable = false, unique = true)
    private String thumbnailKey;

    @Column(nullable = true, length = 1000)
    private String memo;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false)
    private Boolean isDone;

    @Column(nullable = false)
    private String badge;

    @Column(nullable = true)
    private Boolean isVideoConverted;

    @Column(nullable = true)
    private Boolean isThumbnailConverted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Scrap> scraps = new ArrayList<>();

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments = new ArrayList<>();

    @OneToOne(cascade=CascadeType.ALL, mappedBy = "interview",orphanRemoval = true, optional = false, fetch = LAZY)
    @JoinColumn
    Likes likes;


    //    making draft
    public Interview(String videoKey, String thumbnailKey, User user) {
        this.videoKey = videoKey;
        this.thumbnailKey = thumbnailKey;
        this.user = user;
        this.memo = "";
        this.isPublic = false;
        this.isDone = false;
        this.badge = "NONE";
        this.isVideoConverted = false;
        this.isThumbnailConverted = false;
    }

    // complete interview
    public Interview complete(String memo, Boolean isPublic, Question question, String videoKey, String thumbnailKey) {
        this.memo = memo;
        this.isPublic = isPublic;
        this.question = question;
        this.isDone = true;
        this.videoKey = videoKey;
        this.thumbnailKey = thumbnailKey;
        return this;
    }

    //  update interview (only memo, isPublic)
    public Interview update(String memo, Boolean isPublic) {
        this.memo = memo;
        this.isPublic = isPublic;
        return this;
    }

    //  convert webm to mp4
    public Interview convertVideo() {
        this.isVideoConverted = true;
        return this;
    }

    public Interview convertThumbnail() {
        this.isThumbnailConverted = true;
        return this;
    }

    public void updateBadge(String badge) {
        this.badge = badge;
    }
    public void makeScrapNullForDelete(){
        this.scraps = null;
    }

}
