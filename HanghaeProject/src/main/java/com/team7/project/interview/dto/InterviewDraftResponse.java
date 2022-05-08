package com.team7.project.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InterviewDraftResponse {
    private InterviewDraftBody interview;
    private UrlBody presignedUrl;

    @Getter
    @AllArgsConstructor
    public static class InterviewDraftBody {
        private Long id;
    }

    @Getter
    @AllArgsConstructor
    public static class UrlBody {
        private String video;
        private String thumbnail;
    }
}
