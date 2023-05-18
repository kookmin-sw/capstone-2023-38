package com.example.demo2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedPageData {     //피드 페이지 업로드시 필요한 데이터
    private List<String> imageUrls;
    private String userId;
    private int wcount=0;
    private int acount=0;
}
