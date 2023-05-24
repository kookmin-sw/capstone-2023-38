package com.example.demo2.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.demo2.Feed;
import com.example.demo2.Like;
import com.example.demo2.User;
import com.example.demo2.repository.FeedRepository;
import com.example.demo2.repository.LikeRepository;
import com.example.demo2.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@EnableScheduling
public class MyLikeImgController {
    private final AmazonS3 amazonS3;
    private final Functions functions;

    @Value("${cloud.aws.s3.bucket2}")
    private String bucket2;

    @Autowired
    private final LikeRepository likeRepository;

    @Autowired
    private final UserRepository userRepository;

    @GetMapping("/getMylike/{id}")
    @Operation(summary = "내가 좋아요 누른 게시물을 보여주는 기능 - 완료")
    public ResponseEntity<List<Map<String, Object>>> getImageUrlsById(@PathVariable String id) {
        List<Map<String, Object>> response = new ArrayList<>();
        List<Map<String, Object>> imageUrls = new ArrayList<>();

        User user = userRepository.findByUserId(id);
        List<Like> likes = likeRepository.findByUser(user);

        int count = 1;
        for (Like like : likes) {
            Feed feed = like.getFeed();
            String imageUrl = feed.getUrl();

            Map<String, Object> imageMap = new LinkedHashMap<>();
            imageMap.put("number", count);
            imageMap.put("imgsrc", imageUrl);
            imageMap.put("imageUserId", feed.getUser().getUserId());
            imageMap.put("upload-time", feed.getUploadTime().toString());
            imageMap.put("like", feed.getLikeCount());

            imageUrls.add(imageMap);
            count++;
        }

        Map<String, Object> idImageMap = new LinkedHashMap<>();
        idImageMap.put("id", id);
        idImageMap.put("images", imageUrls);

        response.add(idImageMap);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/deliteMylike/{id}")
    @Operation(summary = "좋아요 누른 게시물의 좋아요 취소 및 내가 좋아요 누른 목록 페이지에서 삭제하는 기능")
    public ResponseEntity<Void> deleteImage(@RequestParam("imageUrl") String imageUrl, @PathVariable String id) {
        try {
            String bucket = bucket2;
            String key = getImageKeyFromUrl(imageUrl);
            ObjectMetadata metadata = amazonS3.getObjectMetadata(bucket, key);

            String likedKey = id + "-liked";
            boolean liked = metadata.getUserMetadata().containsKey(likedKey) && "true".equals(metadata.getUserMetaDataOf(likedKey));
            if (liked) {
                ObjectMetadata newMetadata = new ObjectMetadata();
                for (Map.Entry<String, String> entry : metadata.getUserMetadata().entrySet()) {
                    if (!entry.getKey().equals(likedKey)) {
                        newMetadata.addUserMetadata(entry.getKey(), entry.getValue());
                    }
                }

                String acountStr = metadata.getUserMetaDataOf("acount");
                int acount = acountStr != null ? Integer.parseInt(acountStr) : 0;
                acount--; // acount 값을 1 감소시킴

                newMetadata.addUserMetadata("acount", String.valueOf(acount)); // 감소된 acount 값을 새로운 메타데이터에 추가

                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucket, key, bucket, key)
                        .withNewObjectMetadata(newMetadata)
                        .withCannedAccessControlList(CannedAccessControlList.PublicRead);
                amazonS3.copyObject(copyObjectRequest);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getImageKeyFromUrl(String imageUrl) throws MalformedURLException {
        URL url = new URL(imageUrl);
        String path = url.getPath();
        return path.substring(1);
    }
}
