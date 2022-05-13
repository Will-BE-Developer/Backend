package com.team7.project.comments.service;

import com.team7.project.advice.RestException;
import com.team7.project.comments.dto.CommentListDto;
import com.team7.project.comments.dto.CommentRequestDto;
import com.team7.project.comments.model.Comment;
import com.team7.project.comments.repository.CommentRepository;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
import com.team7.project.interview.service.InterviewGeneralService;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;

    //1개 인터뷰의 댓글 리스트 조회(댓글+대댓글)
    public CommentListDto makeCommentList(Long interviewId, User user, int per, int page){
        if (per < 1) {
            throw new RestException(HttpStatus.BAD_REQUEST, "한 페이지 단위(per)는 0보다 커야 합니다.");
        }
        // note that pageable start with 0
        Pageable pageable = PageRequest.of(page-1, per, Sort.by("createdAt").descending());

        //로그인 안했으면 isMine null
        Boolean isMine = null;

        CommentListDto commentListDto = new CommentListDto();

        //댓글 조회
        Page<Comment> commentListPage = getListOfCommentOfInterview(interviewId, pageable);
        List<Comment> commentList = commentListPage.getContent();

        System.out.println("전체 페이지 갯수 : " + commentListPage.getTotalPages());
        System.out.println("해당 인터뷰의 총 댓글 갯수(대댓글 미포함):  " + commentListPage.getTotalElements());
        System.out.println("요청 페이지에서 조회 된 댓글 갯수(대댓글 미포함): " + commentListPage.getNumberOfElements());
        System.out.println("페이지 당 출력 갯수 : " + commentListPage.getSize());
        System.out.println("마지막 페이지 여부 : " + commentListPage.isLast());

        for( Comment eachComment : commentList){
            System.out.println("댓글 조회: " + eachComment.toString());
            if (user != null){
                isMine = user.getId().equals(eachComment.getUser().getId());
            }
            String profileUrl = interviewGeneralService.generateProfileImageUrl(eachComment.getUser().getProfileImageUrl());
            commentListDto.addComment(eachComment, isMine, profileUrl);
        }

        //대댓글 조회 + 대댓글 수
        //이 인터뷰의 전체 대댓글 조회, 대댓글의 부모댓글들이 이 페이지의 댓글목록에 있으면 add Nest
        //(-> 대댓글 중에, 현재 페이지 목록을 부모로 가진 대댓글들만 뽑아서 add)
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
                    System.out.println("대댓글을 포함시킬 부모댓글의 index: " + index);
                    commentListDto.addNestedComment(index, eachComment, isMine, commentListDto.getComments().get(index).getUser().getProfileImageUrl());
                }
            }
        }
        int totalCounts = commentRepository.countByInterview_Id(interviewId); //총댓글수(대댓글 포함)
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

        Page<Comment> commentListPage = commentRepository.findAllByInterviewIdAndRootName(interviewId, "interview", pageable);

        return commentListPage;
    }

    //대댓글 조회
    //지금은 인터뷰에 있는 대댓글을 모두 조회 (추후 -> 해당 페이지에 있는 댓글만 조회)
    public List<Comment> getListOfCommentOfComment(Long interviewId) {

        List<Comment> commentList = commentRepository.findAllNestedCommentInInterview(interviewId, "comment");

        return commentList;
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

    // 작성/수정/삭제한 댓글의 댓글 목록을 response 하기 위한 페이지 넘버 찾기
    // 작성한 댓글 ID를 불러오고, 그 ID의 인터뷰ID와 댓글 page번호를 알아내서, 그 댓글 페이지 조회
    public int getCurrentCommentPage(Comment comment, String type){
        int page = 1;

        //(신규 작성일때만....)comment가 댓글이면, 최신순이므로 1page로
        if (comment.getRootName().equals("interview") && type.equals("save")){
            System.out.println("save");
            page = 1;
        }else{
            //comment가 대댓글이면 부모댓글의 page로(인터뷰 총 댓글수->)
            //댓글(수정,삭제)이면 그 댓글의 원래 페이지로
            Long interviewId = comment.getInterview().getId();
            int commentIdNeedToKnowPage = 0;
            if(comment.getRootName().equals("interview")){ //댓글(수정,삭제)
                commentIdNeedToKnowPage = Math.toIntExact(comment.getId());
            }else{ //대댓글
                commentIdNeedToKnowPage = Math.toIntExact(comment.getRootId());
            }
            int totalComment = commentRepository.countByInterview_IdAndRootName(interviewId,"interview"); //대댓글 제외
            int per = 10;
            int totalPage = totalComment/per + 1;
            List<List<Integer>> list = new ArrayList<>();

            for (int i = 1; i <= totalPage; i++){
                Pageable pageable = PageRequest.of(i-1, per, Sort.by("createdAt").descending());
                List<Integer> thisPageRootCommentIds = commentRepository.rootCommentIdPerPage(interviewId, pageable);
                System.out.println("i: " + i + ", thisPageRootCommentIds: " + thisPageRootCommentIds);
                list.add(thisPageRootCommentIds);
                if (thisPageRootCommentIds.contains(commentIdNeedToKnowPage)){
                    System.out.println("contains i: " + i);
                    page = i;
                }
            }
            System.out.println("페이지별 부모댓글 목록: " + list);
            System.out.println("totalComment: " + totalComment);
        }
        System.out.println("등록/수정/삭제한 대댓글의 페이지: " + page);
        return page;
    }
}
