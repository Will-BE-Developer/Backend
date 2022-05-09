package com.team7.project.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    private String contents;
    //댓글(피드백)이면 interview-id, 대댓글이면 댓글(피드백)id
    private Long rootId;
    private String rootName; //comment/interview
}
