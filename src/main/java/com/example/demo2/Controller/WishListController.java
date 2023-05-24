package com.example.demo2.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.example.demo2.Feed;
import com.example.demo2.User;
import com.example.demo2.Wishlist;
import com.example.demo2.repository.FeedRepository;
import com.example.demo2.repository.UserRepository;
import com.example.demo2.repository.WishlistRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class WishListController {
    private final AmazonS3 amazonS3;
    private final Functions functions;

    @Value("${cloud.aws.s3.bucket3}")
    private String bucket3;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final FeedRepository feedRepository;

    @Autowired
    private final WishlistRepository wishlistRepository;


    @GetMapping("/getWishlist/{id}")
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
    @PostMapping("/postFeedpage2")
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

    @DeleteMapping("/deleteWishlist")
    @Operation(summary = "위시리스트에서 이미지를 삭제하는 기능 - 완료")
    public ResponseEntity<Void> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            // URL에서 버킷, 폴더 및 파일 이름 추출
            String bucketName = functions.getBucketNameFromUrl2(imageUrl);
            String fileName = functions.getFileNameFromUrl2(imageUrl);

            System.out.println("Bucket Name: " + bucketName);
            System.out.println("File Name: " + fileName);

            // S3에서 이미지 삭제
            functions.deleteImageFromS32(bucketName, fileName);

            Wishlist wishlist = wishlistRepository.findByUrl("https://" + bucketName +  ".s3.ap-northeast-2.amazonaws.com/" + fileName);
            if (wishlist != null) {
                wishlistRepository.delete(wishlist);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
