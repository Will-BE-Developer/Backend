package com.sparta.willbe.comments.service;

import com.sparta.willbe._global.pagination.exception.PaginationPerInvalidException;
import com.sparta.willbe.comments.dto.CommentListDto;
import com.sparta.willbe.comments.dto.CommentRequestDto;
import com.sparta.willbe.comments.exception.*;
import com.sparta.willbe.comments.model.Comment;
import com.sparta.willbe.comments.repository.CommentRepository;
import com.sparta.willbe.interview.exception.InterviewNotFoundException;
import com.sparta.willbe.interview.model.Interview;
import com.sparta.willbe.interview.repository.InterviewRepository;
import com.sparta.willbe.interview.service.InterviewService;
import com.sparta.willbe.user.exception.UserNotFoundException;
import com.sparta.willbe.user.model.User;
import com.sparta.willbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final InterviewService interviewService;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;

    //1개 인터뷰의 댓글 리스트 조회(댓글+대댓글)
    public CommentListDto makeCommentList(Long interviewId, User user, int per, int page){
        if (per < 1) {
            throw new PaginationPerInvalidException();
        }
        // note that pageable start with 0
        Pageable pageable = PageRequest.of(page-1, per, Sort.by("createdAt").descending());

        //로그인 안했으면
        Boolean isMine = null;

        CommentListDto commentListDto = new CommentListDto();

        //댓글 조회
        Page<Comment> commentListPage = getListOfCommentOfInterview(interviewId, pageable);
        List<Comment> commentList = commentListPage.getContent();

        for( Comment eachComment : commentList){
            if (user != null){
                isMine = user.getId().equals(eachComment.getUser().getId());
            }
            String profileUrl = interviewService.getProfileImageUrl(eachComment.getUser().getProfileImageUrl());
            commentListDto.addComment(eachComment, isMine, profileUrl);
        }

        //대댓글 조회 + 대댓글 수
        //이 인터뷰의 전체 대댓글 조회, 대댓글의 부모댓글들이 이 페이지의 댓글목록에 있으면 add Nest
        //(-> 대댓글 중에, 현재 페이지 목록을 부모로 가진 대댓글들만 뽑아서 add)
        //이 인터뷰의 전체 대댓글 조회
        List<Comment> nestedCommentList = getListOfCommentOfComment(interviewId);
        for( Comment eachChild : nestedCommentList){
            Long itsParentId = eachChild.getRootId();

            List<Comment> result = commentList.stream()
                    .filter(a -> Objects.equals(a.getId(), itsParentId))
                    .collect(Collectors.toList());
            log.info("makeCommentList() >> 부모 댓글 조회 : {}", result);

            if (user != null) {
                isMine = user.getId().equals(eachChild.getUser().getId());
            }
            //response의 부모댓글 리스트
            List<CommentListDto.ResponseComment> commentListInDto = commentListDto.getComments();
            for (CommentListDto.ResponseComment parentComment: commentListInDto){
                if (parentComment.getId().equals(itsParentId)){
                    int index = commentListDto.getComments().indexOf(parentComment);
                    log.info("makeCommentList() >> 대댓글을 포함시킬 부모댓글의 index : {}", index);
                    String childProfileUrl = interviewService.getProfileImageUrl(eachChild.getUser().getProfileImageUrl());
                    commentListDto.addNestedComment(index, eachChild, isMine, childProfileUrl);
                }
            }
        }
        int totalCounts = commentRepository.countByInterview_IdAndUser_IsDeletedFalse(interviewId); //총댓글수(대댓글 포함)
        int totalPages = commentListPage.getTotalPages();
        int totalCountsInThisPage = commentListPage.getNumberOfElements();

        int currentPage = page;
        Boolean isLastPage = commentListPage.isLast();
        Integer nextPage = 0;

        if (isLastPage == true){
            nextPage = null;
        }else{
            nextPage = currentPage + 1;
        }
        commentListDto.addPagination(per, totalCountsInThisPage, totalPages, currentPage, nextPage, isLastPage);
        commentListDto.setTotalComments(totalCounts);

        return commentListDto;
    }

    // 1개 인터뷰의 전체 댓글 조회(대댓글 미포함)
    public Page<Comment> getListOfCommentOfInterview(Long interviewId, Pageable pageable) {

        Page<Comment> commentListPage = commentRepository.findAllByInterviewIdAndRootNameAndUser_IsDeletedFalse(interviewId, "interview", pageable);

        return commentListPage;
    }

    //대댓글 조회
    //지금은 인터뷰에 있는 대댓글을 모두 조회 (추후 -> 해당 페이지에 있는 댓글만 조회)
    public List<Comment> getListOfCommentOfComment(Long interviewId) {

        List<Comment> commentList = commentRepository.findAllNestedCommentInInterviewAndUser_IsDeletedFalse(interviewId, "comment");

        return commentList;
    }

    @Transactional  //메서드가 포함하고 있는 작업 중에 하나라도 실패할 경우 전체 작업을 취소
    public Comment saveComment(CommentRequestDto requestDto, User user) {

        userRepository.findById(user.getId()).orElseThrow(
                () -> new UserNotFoundException());

        //rootname이 인터뷰면, interview repo에서 rootId(=interview id)로 interview를 찾고, 없으면 에러
        //rootname이 댓글이면, comment repo에서 rootId(=comment id)로 comment를 찾고, 없으면 에러, 그 코멘트의 인터뷰를 get
        Comment comment = new Comment();
        if (requestDto.getRootName().equals("interview")){
            Interview interview = interviewRepository.findById(requestDto.getRootId()).orElseThrow(
                    InterviewNotFoundException::new);
            log.info("saveComment() >> 댓글 작성한 인터뷰 ID : {}", interview.getId());

            comment = new Comment(requestDto, user, interview);
            commentRepository.save(comment);
        }else if(requestDto.getRootName().equals("comment")){
            Comment rootComment = commentRepository.findById(requestDto.getRootId()).orElseThrow(
                    () -> new CommentNotFoundException());
            Interview interview = rootComment.getInterview();
            comment = new Comment(requestDto, user, interview);
            commentRepository.save(comment);
        }
        return comment;
    }

    @Transactional
    public Comment editComment(Long commentId, CommentRequestDto requestDto, User user) {
        userRepository.findById(user.getId()).orElseThrow(
                () -> new UserNotFoundException());

        //수정하려는 댓글 id가 존재하는지
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommentNotFoundException());

        //댓글 작성한 유저인지 확인
        if (comment.getUser().getId() != user.getId()){
            throw new CommentForbiddenUpdateException();
        }

        //기존에 rootName과 일치하는지
        if (comment.getRootName().equals(requestDto.getRootName())){
            //기존 rootId와 일치하는지
            if (comment.getRootId().equals(requestDto.getRootId())){
                //수정 실시
                comment.update(requestDto);
            }else{
                throw new CommentRootIdException();
            }
        }else{
            throw new CommentRootNameException();
        }

        return comment;
    }

    @Transactional
    public Comment deleteComment(Long commentId, User user) {
        userRepository.findById(user.getId()).orElseThrow(
                () -> new UserNotFoundException());

        //삭제 전 response를 위해 조회
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommentNotFoundException());

        //댓글 작성한 유저인지 확인
        if (comment.getUser().getId() != user.getId()){
            throw new CommentForbiddenDeleteException();
        }

        //부모댓글이면 자식댓글도 삭제
        if (comment.getRootName().equals("interview")){
            List<Comment> childCommentList = commentRepository.findByRootIdAndRootName(comment.getId(), "comment");
            for(Comment childComment: childCommentList){
                commentRepository.deleteById(childComment.getId());
            }
        }
        commentRepository.deleteById(comment.getId());
        log.info("deleteComment() >> {}번 댓글이 삭제 되었습니다", commentId);

        return comment;
    }

    // 작성/수정/삭제한 댓글의 댓글 목록을 response 하기 위한 페이지 번호 찾기
    // 작성한 댓글 ID를 불러오고, 그 ID의 인터뷰ID와 댓글 page번호를 알아내서, 그 댓글 페이지 조회
    public int getCurrentCommentPage(Comment comment, String type){
        int page = 1;

        //(신규 작성일때만)comment가 댓글이면, 최신순이므로 1page로
        if (comment.getRootName().equals("interview") && type.equals("save")){
            page = 1;
        //수정/삭제한 댓글, 등록/수정/삭제한 대댓글의 페이지를 알아내기 위해
        }else{
            //comment가 대댓글이면 부모댓글의 페이지로
            //댓글(수정,삭제)이면 그 댓글의 원래 페이지로
            Long interviewId = comment.getInterview().getId();
            int commentIdNeedToKnowPage = 0;
            //수정/삭제한 댓글의 ID
            if(comment.getRootName().equals("interview")){ 
                commentIdNeedToKnowPage = Math.toIntExact(comment.getId());
            //등록/수정/삭제한 대댓글의 부모댓글 ID
            }else{ 
                commentIdNeedToKnowPage = Math.toIntExact(comment.getRootId());
            }
            int totalComment = commentRepository.countByInterview_IdAndRootNameAndUser_IsDeletedFalse(interviewId,"interview"); //대댓글 제외
            int per = 10;
            int totalPage = totalComment/per + 1;
            List<List<Integer>> list = new ArrayList<>();

            //전체 페이지를 돌면서 그 안에
            for (int i = 1; i <= totalPage; i++){
                //각 페이지에서 부모댓글 리스트를 뽑고
                Pageable pageable = PageRequest.of(i-1, per, Sort.by("createdAt").descending());
                List<Integer> thisPageRootCommentIds = commentRepository.rootCommentIdPerPage(interviewId, pageable);
                log.info("getCurrentCommentPage() >> 댓글 페이지번호: {}, 이 페이지의 부모댓글 리스트: {}", i, thisPageRootCommentIds);
                list.add(thisPageRootCommentIds);
                //이 페이지 부모댓글 리스트에 찾으려는 댓글ID가 있으면
                if (thisPageRootCommentIds.contains(commentIdNeedToKnowPage)){
                    page = i;
                }
            }
            log.info("getCurrentCommentPage() >> 페이지별 부모댓글 목록 : {}", list);
        }
        log.info("getCurrentCommentPage() >> 수정/삭제한 댓글 또는 등록/수정/삭제한 대댓글의 페이지 : {}", page);
        return page;
    }
}
