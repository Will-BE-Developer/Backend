package com.team7.project.comments.service;

import com.team7.project.advice.RestException;
import com.team7.project.comments.dto.CommentListDto;
import com.team7.project.comments.dto.CommentRequestDto;
import com.team7.project.comments.model.Comment;
import com.team7.project.comments.repository.CommentRepository;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;

    public CommentService(
            CommentRepository commentRepository,
            UserRepository userRepository,
            InterviewRepository interviewRepository){
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
    }

    @Transactional  //메서드가 포함하고 있는 작업 중에 하나라도 실패할 경우 전체 작업을 취소
    public Comment saveComment(CommentRequestDto requestDto, User user) {

        userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자는 존재하지 않습니다."));

        //rootname이 인터뷰면, interview repo에서 rootId(=interview id)로 interview를 찾고, 없으면 에러
        //rootname이 댓글이면, comment repo에서 rootId(=comment id)로 comment를 찾고, 없으면 에러, 그 코멘트의 인터뷰를 get
        Comment comment = new Comment();
        if (requestDto.getRootName().equals("interview")){
            Interview interview = interviewRepository.findById(requestDto.getRootId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 인터뷰는 존재하지 않습니다.")
            );
            System.out.println("댓글 작성한 인터뷰 ID: " + interview.getId());
            comment = new Comment(requestDto, user, interview);
            commentRepository.save(comment);
        }else if(requestDto.getRootName().equals("comment")){
            Comment rootComment = commentRepository.findById(requestDto.getRootId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 댓글은 존재하지 않습니다.")
            );
            Interview interview = rootComment.getInterview();
            comment = new Comment(requestDto, user, interview);
            commentRepository.save(comment);
        }
        return comment;
    }

    @Transactional
    public Comment editComment(Long commentId, CommentRequestDto requestDto, User user) {
        userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자는 존재하지 않습니다."));

        //수정하려는 댓글 id가 존재하는지
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글은 존재하지 않습니다.")
        );

        //기존에 rootName과 일치하는지
        if (comment.getRootName().equals(requestDto.getRootName())){
            //기존 rootId와 일치하는지
            if (comment.getRootId().equals(requestDto.getRootId())){
                //수정 실시
                comment.update(requestDto);
            }else{
                throw new RestException(HttpStatus.BAD_REQUEST, "rootId가 일치하지 않습니다.");
            }
        }else{
            throw new RestException(HttpStatus.BAD_REQUEST, "rootName이 일치하지 않습니다.");
        }

        return comment;
    }

    @Transactional
    public Comment deleteComment(Long commentId, User user) {
        userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자는 존재하지 않습니다.")
        );

        //삭제 전 response를 위해 조회
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글은 존재하지 않습니다.")
        );
        commentRepository.deleteById(comment.getId());
        System.out.println(commentId + "번 댓글이 삭제 되었습니다");

        return comment;
    }

    //댓글 리스트 조회
    public CommentListDto makeCommentList(Long interviewId, User user, int per, int page){
        if (per < 1) {
            throw new RestException(HttpStatus.BAD_REQUEST, "한 페이지 단위(per)는 0보다 커야 합니다.");
        }
        // note that pageable start with 0
        Pageable pageable = PageRequest.of(page - 1, per, Sort.by("createdAt").descending());

        //로그인 안했으면 isMine null
        Boolean isMine = null;

        CommentListDto commentListDto = new CommentListDto();

        //댓글 조회
        //List<Comment> commentList = getListOfCommentOfInterview(interviewId);
        //List<Comment> commentList = getListOfCommentOfInterview(interviewId, pageable);
        List<Comment> commentList = getListOfCommentOfInterview(interviewId, per, page);
        System.out.println("댓글 리스트(per: " + per + ", page: " + page + ": " + commentList);

        for( Comment eachComment : commentList){
            System.out.println("댓글 조회: " + eachComment.toString());
            if (user != null){
                isMine = user.getId().equals(eachComment.getUser().getId());
            }
            commentListDto.addComment(eachComment, isMine);
        }

        //대댓글 조회 + 대댓글 수
        List<Comment> nestedCommentList = getListOfCommentOfComment(interviewId);

        for( Comment eachComment : nestedCommentList){
            System.out.println("대댓글 조회: " + eachComment.toString());

            Long RootId = eachComment.getRootId();

            List<Comment> result = commentList.stream()
                    .filter(a -> Objects.equals(a.getId(), RootId))
                    .collect(Collectors.toList());
            System.out.println("부모 댓글 조회: " + result);

            if (user != null) {
                isMine = user.getId().equals(eachComment.getUser().getId());
            }
            List<CommentListDto.ResponseComment> commentListInDto = commentListDto.getComments();
            for (CommentListDto.ResponseComment eachCommentDto: commentListInDto){
                if (eachCommentDto.getId().equals(RootId)){
                    int index = commentListDto.getComments().indexOf(eachCommentDto);
                    System.out.println("nested 넣을 댓글 목록의 index: " + index);
                    commentListDto.addNestedComment(index, eachComment, isMine);
                }
            }
        }
        //int totalCounts = commentList.size();
        //Long totalCountsLong = commentRepository.countByInterview_Id(interviewId);
        Long totalCountsLong = commentRepository.countByInterview_IdAndRootName(interviewId,"interview"); //대댓글 제외
        int totalCounts = totalCountsLong.intValue();
        int totalPages = (int) Math.ceil((double)totalCounts/per);
        System.out.println("totalPages: " + totalPages);

        int totalCountsInThisPage = commentList.size();

        int currentPage = page;
        Boolean isLastPage = false;
        int nextPage = 0;

        if (currentPage == totalPages){
            isLastPage = true;
        }else{
            isLastPage = false;
        }

        if (isLastPage == true){
            nextPage = currentPage;
        }else{
            nextPage = currentPage + 1;
        }
        System.out.println("nextPage: " + nextPage);

        //commentListDto.addPagination(per, totalCounts, totalPages, currentPage, nextPage, isLastPage);
        commentListDto.addPagination(per, totalCountsInThisPage, totalPages, currentPage, nextPage, isLastPage);

        return commentListDto;
    }

    //public List<Comment> getListOfCommentOfInterview(Long interviewId, Pageable pageable) {
    public List<Comment> getListOfCommentOfInterview(Long interviewId, int per, int page) {

        System.out.println("offset: " + ((page-1)*per));
        //List<Comment> commentList = commentRepository.findAllByInterviewIdAndRootName(interviewId, "interview");
        List<Comment> commentList = commentRepository.findAllByInterviewIdAndRootName(interviewId, "interview", per, (page-1)*per);

        return commentList;
    }

    public List<Comment> getListOfCommentOfComment(Long interviewId) {
        List<Comment> commentList = commentRepository.findAllByInterviewIdAndRootNameNest(interviewId, "comment");

        return commentList;
    }

    // 댓글 리스트 response
    // 작성한 댓글 ID를 불러오고, 그 ID의 인터뷰ID와 댓글 page번호를 알아내서, 그 댓글 페이지 조회
    public int getCurrentCommentPage(Comment comment){
        Long commentId = comment.getId();
        Long interviewId = comment.getInterview().getId();
        //Long totalCommentCountLong = commentRepository.countByInterview_Id(interviewId);
        Long totalCommentCountLong = commentRepository.countByInterview_IdAndRootName(interviewId,"interview"); //대댓글 제외
        int totalCommentCount = totalCommentCountLong.intValue();
        int per = 10;
        int page = (int) Math.ceil((double)totalCommentCount/per);
        System.out.println("commentId: " + commentId);
        System.out.println("totalCommentCount: " + totalCommentCount);
        System.out.println("totalCommentCount/per: " + (double)totalCommentCount/per);
        System.out.println("등록/수정/삭제한 댓글의 페이지: " + page);

        return page;
    }

}
