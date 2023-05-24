package com.example.demo2.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.example.demo2.Feed;
import com.example.demo2.User;
import com.example.demo2.Wishlist;

import com.example.demo2.repository.FeedRepository;
import com.example.demo2.repository.LikeRepository;
import com.example.demo2.repository.UserRepository;
import com.example.demo2.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@EnableScheduling
public class FeedPageController {
    private final AmazonS3 amazonS3;
    private final Functions functions;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final FeedRepository feedRepository;

    @Autowired
    private final WishlistRepository wishlistRepository;

    @Autowired
    private final LikeRepository likeRepository;

    @Value("${cloud.aws.s3.bucket2}")
    private String bucket2;

    @Value("${cloud.aws.s3.bucket3}")
    private String bucket3;
    @GetMapping("/getFeedpage/uploadTime")
    @Operation(summary = "업로드 시간을 기준으로 피드 페이지를 보여주는 기능 - 완료")
    public ResponseEntity<Map<String, Object>> getFeedPageSortedByUploadTime(@RequestParam("userId") String userId) {
        List<Map<String, Object>> imageList = new ArrayList<>();
        List<Feed> feedList = feedRepository.findAllByOrderByUploadTimeAsc();
        User user = userRepository.findByUserId(userId);

        int count = 1;
        for (Feed feed : feedList) {
            boolean click = likeRepository.existsByUserAndFeed(user, feed);

            Map<String, Object> imageMetadata = new LinkedHashMap<>();
            imageMetadata.put("number", count);
            imageMetadata.put("imageUrl", feed.getUrl());
            imageMetadata.put("imageUserId", user.getUserId());
            imageMetadata.put("uploadTime", feed.getUploadTime());
            imageMetadata.put("like", feed.getLikeCount());
            imageMetadata.put("click", click);

            imageList.add(imageMetadata);
            count++;
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId", userId);
        response.put("images", imageList);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/getFeedpage/acount")
    @Operation(summary = "좋아요 수를 기준으로 피드 페이지를 보여주는 기능 - 완료")
    public ResponseEntity<Map<String, Object>> getFeedPageSortedByLikeCount(@RequestParam("userId") String userId) {
        List<Map<String, Object>> imageList = new ArrayList<>();
        List<Feed> feedList = feedRepository.findAllByOrderByLikeCountDesc();
        User user = userRepository.findByUserId(userId);

        int count = 1;
        for (Feed feed : feedList) {
            boolean click = likeRepository.existsByUserAndFeed(user, feed);

            Map<String, Object> imageMetadata = new LinkedHashMap<>();
            imageMetadata.put("number", count);
            imageMetadata.put("imageUrl", feed.getUrl());
            imageMetadata.put("imageUserId", user.getUserId());
            imageMetadata.put("uploadTime", feed.getUploadTime());
            imageMetadata.put("like", feed.getLikeCount());
            imageMetadata.put("click", click);

            imageList.add(imageMetadata);
            count++;
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId", userId);
        response.put("images", imageList);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/increaseCount")  //좋아요 수를 올려주는 기능
    @Operation(summary = "좋아요 수를 올려주는 기능 - 완료")
    public ResponseEntity<Void> incrementCounts(@RequestParam("imageUrl") String imageUrl, @RequestParam("userId") String userId) {
        try {
            functions.updateAcount(imageUrl, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getWishlist2/{id}")
    @Operation(summary = "위시리스트 목록을 보여주는 기능 - 완료")
    public ResponseEntity<List<Map<String, Object>>> getImageUrlsById(@PathVariable String id) throws IOException {
        List<Map<String, Object>> response = new ArrayList<>();
        List<Map<String, Object>> imageUrls = new ArrayList<>();

        // 조회된 Wishlist 엔티티 리스트 가져오기
        User user = userRepository.findByUserId(id);
        List<Wishlist> wishlists = wishlistRepository.findByUser(user);

        int count = 1;
        for (Wishlist wishlist : wishlists) {
            String imageUrl = wishlist.getUrl();
            Map<String, Object> imageMap = new HashMap<>();
            imageMap.put("number", count);
            imageMap.put("imgsrc", imageUrl);
            imageUrls.add(imageMap);
            count++;
        }

        Map<String, Object> idImageMap = new LinkedHashMap<>();
        idImageMap.put("id", id);
        idImageMap.put("images", imageUrls);

        response.add(idImageMap);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/postFeedpage")
    @Operation(summary = "피드 페이지에 이미지를 업로드하는 기능 - 완료")
    public ResponseEntity<List<String>> uploadImageUrls(@RequestParam String imageUrl, @RequestParam String userId) {
        try {
            List<String> uploadedImageUrls = functions.uploadUrlsFeed(imageUrl);
            Feed feed = new Feed();
            feed.setLikeCount(0);
            feed.setUploadTime(LocalDateTime.now());

            User user = userRepository.findByUserId(userId);
            feed.setUser(user);
            String savedImageUrl = uploadedImageUrls.get(0);
            feed.setUrl(savedImageUrl);
            feedRepository.save(feed);

            return ResponseEntity.ok(uploadedImageUrls);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
