package com.example.demo2.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.example.demo2.Feed;
import com.example.demo2.User;
import com.example.demo2.repository.FeedRepository;
import com.example.demo2.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@EnableScheduling
public class MyFeedController {
    private final AmazonS3 amazonS3;
    private final Functions functions;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final FeedRepository feedRepository;
    @GetMapping("/getMYfeed/{id}")  //feedpage 버킷에서 내가 올린 게시글 이미지만 조회
    @Operation(summary = "피드 페이지에 내가 올린 게시물만 보여주는 기능 - 완료")
    public ResponseEntity<List<Map<String, Object>>> getFeedById(@PathVariable String id) {
        List<Map<String, Object>> response = new ArrayList<>();
        List<Map<String, Object>> feedData = new ArrayList<>();

        User user = userRepository.findByUserId(id);
        List<Feed> feeds = feedRepository.findByUser(user);

        int count = 1;
        for (Feed feed : feeds) {
            Map<String, Object> feedMap = new LinkedHashMap<>();
            feedMap.put("number", count);
            feedMap.put("imgsrc", feed.getUrl());
            feedMap.put("upload-time", feed.getUploadTime());
            feedMap.put("like", feed.getLikeCount());
            feedData.add(feedMap);
            count++;
        }

        Map<String, Object> idFeedMap = new LinkedHashMap<>();
        idFeedMap.put("id", id);
        idFeedMap.put("feeds", feedData);

        response.add(idFeedMap);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteMyfeed")
    @Operation(summary = "마이 피드에서 이미지를 삭제하는 기능 - 완료")
    public ResponseEntity<Void> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            // URL에서 버킷, 폴더 및 파일 이름 추출
            String bucketName = functions.getBucketNameFromUrl2(imageUrl);
            String fileName = functions.getFileNameFromUrl2(imageUrl);

            System.out.println("Bucket Name: " + bucketName);
            System.out.println("File Name: " + fileName);

            // S3에서 이미지 삭제
            functions.deleteImageFromS32(bucketName, fileName);


            Feed feed = feedRepository.findByUrl("https://" + bucketName +  ".s3.ap-northeast-2.amazonaws.com/" + fileName);
            if (feed != null) {
                feedRepository.delete(feed);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
